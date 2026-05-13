package br.com.metodologia.cards

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetodologiaApp()
        }
    }
}

data class SourceReference(
    val pdf: String,
    val page: Int,
    val excerpt: String
)

data class Topic(
    val id: String,
    val name: String,
    val description: String
)

data class Flashcard(
    val id: String,
    val topicId: String,
    val type: String,
    val difficulty: Int,
    val front: String,
    val back: String,
    val source: SourceReference
)

data class QuizQuestion(
    val id: String,
    val topicId: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val source: SourceReference
)

data class ConceptRelation(
    val id: String,
    val topicId: String,
    val from: String,
    val to: String,
    val description: String,
    val source: SourceReference
)

data class GlossaryTerm(
    val id: String,
    val term: String,
    val definition: String,
    val topicId: String,
    val source: SourceReference
)

data class ExamPracticeQuestion(
    val id: String,
    val topicId: String,
    val prompt: String,
    val weight: String,
    val approach: String,
    val checklist: List<String>,
    val source: SourceReference
)

data class ExamModel(
    val id: String,
    val title: String,
    val focus: String,
    val duration: String,
    val questions: List<String>,
    val rubric: List<String>,
    val source: SourceReference
)

data class MaterialStudyGuide(
    val id: String,
    val pdf: String,
    val title: String,
    val purpose: String,
    val visualTree: List<String>,
    val flow: List<String>,
    val diagram: List<String>,
    val metacognition: List<String>,
    val strategies: List<String>,
    val source: SourceReference
)

data class StudyContent(
    val topics: List<Topic>,
    val flashcards: List<Flashcard>,
    val quizzes: List<QuizQuestion>,
    val relations: List<ConceptRelation>,
    val glossary: List<GlossaryTerm>,
    val examPractice: List<ExamPracticeQuestion>,
    val examModels: List<ExamModel>,
    val studyGuides: List<MaterialStudyGuide>
)

enum class Confidence(val label: String, val score: Int) {
    None("Não lembro", 0),
    Partial("Lembro parcialmente", 1),
    Explain("Consigo explicar", 2),
    Differentiate("Consigo diferenciar", 3)
}

enum class Screen(val label: String, val icon: ImageVector) {
    Home("Início", Icons.Outlined.LocalFireDepartment),
    Study("Estudo", Icons.Outlined.Psychology),
    Cards("Cards", Icons.Outlined.MenuBook),
    Quiz("Quiz", Icons.Outlined.Quiz),
    Errors("Erros", Icons.Outlined.ErrorOutline),
    Relations("Relações", Icons.Outlined.CheckCircle),
    Glossary("Termos", Icons.Outlined.Source),
    Exam("Treino", Icons.Outlined.CheckCircle)
}

class StudyRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("study_progress", Context.MODE_PRIVATE)

    fun loadContent(): StudyContent {
        val raw = context.assets.open("study_content.json").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        return StudyContent(
            topics = root.getJSONArray("topics").mapObjects {
                Topic(
                    id = getString("id"),
                    name = getString("name"),
                    description = getString("description")
                )
            },
            flashcards = root.getJSONArray("flashcards").mapObjects {
                Flashcard(
                    id = getString("id"),
                    topicId = getString("topicId"),
                    type = getString("type"),
                    difficulty = getInt("difficulty"),
                    front = getString("front"),
                    back = getString("back"),
                    source = getJSONObject("source").toSource()
                )
            },
            quizzes = root.getJSONArray("quizzes").mapObjects {
                QuizQuestion(
                    id = getString("id"),
                    topicId = getString("topicId"),
                    question = getString("question"),
                    options = getJSONArray("options").mapStrings(),
                    correctOptionIndex = getInt("correctOptionIndex"),
                    explanation = getString("explanation"),
                    source = getJSONObject("source").toSource()
                )
            },
            relations = root.getJSONArray("relations").mapObjects {
                ConceptRelation(
                    id = getString("id"),
                    topicId = getString("topicId"),
                    from = getString("from"),
                    to = getString("to"),
                    description = getString("description"),
                    source = getJSONObject("source").toSource()
                )
            },
            glossary = root.getJSONArray("glossary").mapObjects {
                GlossaryTerm(
                    id = getString("id"),
                    term = getString("term"),
                    definition = getString("definition"),
                    topicId = getString("topicId"),
                    source = getJSONObject("source").toSource()
                )
            },
            examPractice = root.getJSONArray("examPractice").mapObjects {
                ExamPracticeQuestion(
                    id = getString("id"),
                    topicId = getString("topicId"),
                    prompt = getString("prompt"),
                    weight = getString("weight"),
                    approach = getString("approach"),
                    checklist = getJSONArray("checklist").mapStrings(),
                    source = getJSONObject("source").toSource()
                )
            },
            examModels = root.getJSONArray("examModels").mapObjects {
                ExamModel(
                    id = getString("id"),
                    title = getString("title"),
                    focus = getString("focus"),
                    duration = getString("duration"),
                    questions = getJSONArray("questions").mapStrings(),
                    rubric = getJSONArray("rubric").mapStrings(),
                    source = getJSONObject("source").toSource()
                )
            },
            studyGuides = root.getJSONArray("studyGuides").mapObjects {
                MaterialStudyGuide(
                    id = getString("id"),
                    pdf = getString("pdf"),
                    title = getString("title"),
                    purpose = getString("purpose"),
                    visualTree = getJSONArray("visualTree").mapStrings(),
                    flow = getJSONArray("flow").mapStrings(),
                    diagram = getJSONArray("diagram").mapStrings(),
                    metacognition = getJSONArray("metacognition").mapStrings(),
                    strategies = getJSONArray("strategies").mapStrings(),
                    source = getJSONObject("source").toSource()
                )
            }
        )
    }

    fun snapshot(content: StudyContent): ProgressSnapshot {
        val today = LocalDate.now().toString()
        val lastDay = prefs.getString("lastDay", null)
        val streak = when {
            lastDay == today -> prefs.getInt("streak", 0)
            lastDay == null -> 0
            ChronoUnit.DAYS.between(LocalDate.parse(lastDay), LocalDate.now()) == 1L -> prefs.getInt("streak", 0)
            else -> 0
        }
        return ProgressSnapshot(
            xp = prefs.getInt("xp", 0),
            streak = streak,
            reviewedToday = prefs.getInt("reviewed_$today", 0),
            quizToday = prefs.getInt("quiz_$today", 0),
            fixedErrorsToday = prefs.getInt("fixed_$today", 0),
            weakTopicId = weakestTopic(content),
            errorCardIds = prefs.getStringSet("errors", emptySet()).orEmpty().toSet(),
            masteredCardIds = content.flashcards.mapNotNull { card ->
                if (prefs.getInt("ok_${card.id}", 0) >= 2) card.id else null
            }.toSet()
        )
    }

    fun recordCard(card: Flashcard, confidence: Confidence, correct: Boolean) {
        touchDay()
        val today = LocalDate.now().toString()
        val errors = prefs.getStringSet("errors", emptySet()).orEmpty().toMutableSet()
        val editor = prefs.edit()
        editor.putInt("reviewed_$today", prefs.getInt("reviewed_$today", 0) + 1)
        editor.putInt("xp", prefs.getInt("xp", 0) + if (correct) 12 else 6)
        editor.putInt("confidence_${card.id}", confidence.score)
        if (correct) {
            editor.putInt("ok_${card.id}", prefs.getInt("ok_${card.id}", 0) + 1)
            if (errors.remove(card.id)) {
                editor.putInt("fixed_$today", prefs.getInt("fixed_$today", 0) + 1)
            }
        } else {
            editor.putInt("miss_${card.id}", prefs.getInt("miss_${card.id}", 0) + 1)
            errors.add(card.id)
        }
        editor.putStringSet("errors", errors)
        editor.apply()
    }

    fun recordQuiz(question: QuizQuestion, correct: Boolean) {
        touchDay()
        val today = LocalDate.now().toString()
        prefs.edit()
            .putInt("quiz_$today", prefs.getInt("quiz_$today", 0) + 1)
            .putInt("xp", prefs.getInt("xp", 0) + if (correct) 10 else 4)
            .putInt("quiz_${question.id}", if (correct) 1 else -1)
            .apply()
    }

    fun resetProgress() {
        prefs.edit().clear().apply()
    }

    private fun touchDay() {
        val today = LocalDate.now()
        val lastDay = prefs.getString("lastDay", null)
        val currentStreak = prefs.getInt("streak", 0)
        val nextStreak = when {
            lastDay == today.toString() -> currentStreak.coerceAtLeast(1)
            lastDay == null -> 1
            ChronoUnit.DAYS.between(LocalDate.parse(lastDay), today) == 1L -> currentStreak + 1
            else -> 1
        }
        prefs.edit().putString("lastDay", today.toString()).putInt("streak", nextStreak).apply()
    }

    private fun weakestTopic(content: StudyContent): String? {
        return content.topics.minByOrNull { topic ->
            val topicCards = content.flashcards.filter { it.topicId == topic.id }
            if (topicCards.isEmpty()) return@minByOrNull 100
            topicCards.sumOf { prefs.getInt("ok_${it.id}", 0) - prefs.getInt("miss_${it.id}", 0) }
        }?.id
    }
}

data class ProgressSnapshot(
    val xp: Int,
    val streak: Int,
    val reviewedToday: Int,
    val quizToday: Int,
    val fixedErrorsToday: Int,
    val weakTopicId: String?,
    val errorCardIds: Set<String>,
    val masteredCardIds: Set<String>
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudyRepository(application)
    val content: StudyContent = repository.loadContent()
    var progress by mutableStateOf(repository.snapshot(content))
        private set

    fun recordCard(card: Flashcard, confidence: Confidence, correct: Boolean) {
        repository.recordCard(card, confidence, correct)
        progress = repository.snapshot(content)
    }

    fun recordQuiz(question: QuizQuestion, correct: Boolean) {
        repository.recordQuiz(question, correct)
        progress = repository.snapshot(content)
    }

    fun resetProgress() {
        repository.resetProgress()
        progress = repository.snapshot(content)
    }
}

@Composable
fun MetodologiaApp(viewModel: StudyViewModel = viewModel()) {
    var screen by remember { mutableStateOf(Screen.Home) }
    var source by remember { mutableStateOf<SourceReference?>(null) }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF1F6F68),
            secondary = Color(0xFF8A5A2B),
            surface = Color(0xFFFFFBF7),
            background = Color(0xFFF8F4EE)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (source != null) {
                SourceScreen(source = source!!, onBack = { source = null })
            } else {
                Scaffold(
                    bottomBar = {
                        Surface(color = MaterialTheme.colorScheme.surface) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                            Screen.entries.forEach { item ->
                                NavigationBarItem(
                                    selected = screen == item,
                                    onClick = { screen = item },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label, maxLines = 1) },
                                        modifier = Modifier.width(96.dp)
                                )
                            }
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (screen) {
                            Screen.Home -> HomeScreen(viewModel, onStartCards = { screen = Screen.Cards }, onStartQuiz = { screen = Screen.Quiz })
                            Screen.Study -> MaterialStudyScreen(viewModel, onSource = { source = it })
                            Screen.Cards -> FlashcardScreen(viewModel, onlyErrors = false, onSource = { source = it })
                            Screen.Quiz -> QuizScreen(viewModel, onSource = { source = it })
                            Screen.Errors -> FlashcardScreen(viewModel, onlyErrors = true, onSource = { source = it })
                            Screen.Relations -> RelationsScreen(viewModel, onSource = { source = it })
                            Screen.Glossary -> GlossaryScreen(viewModel, onSource = { source = it })
                            Screen.Exam -> ExamPracticeScreen(viewModel, onSource = { source = it })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: StudyViewModel, onStartCards: () -> Unit, onStartQuiz: () -> Unit) {
    val content = viewModel.content
    val progress = viewModel.progress
    val weakTopic = content.topics.firstOrNull { it.id == progress.weakTopicId }
    val mastered = progress.masteredCardIds.size
    val total = content.flashcards.size.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Metodologia Cards", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Estudo offline com fonte em PDF para cada item.", style = MaterialTheme.typography.bodyMedium)
        }
        item {
            Panel {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Metric("XP", progress.xp.toString(), Modifier.weight(1f))
                    Metric("Streak", "${progress.streak}d", Modifier.weight(1f))
                    Metric("Erros", progress.errorCardIds.size.toString(), Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text("Domínio geral", fontWeight = FontWeight.SemiBold)
                LinearProgressIndicator(
                    progress = { mastered / total.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )
                Text("$mastered de $total cards consolidados")
            }
        }
        item {
            Panel {
                Text("Missão de hoje", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                MissionRow("Revisar 15 cards", progress.reviewedToday, 15)
                MissionRow("Responder 10 questões", progress.quizToday, 10)
                MissionRow("Corrigir 5 erros", progress.fixedErrorsToday, 5)
                Text(
                    "Tópico fraco: ${weakTopic?.name ?: "inicie uma sessão para descobrir"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onStartCards, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.MenuBook, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Cards")
                    }
                    OutlinedButton(onClick = onStartQuiz, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Quiz, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Quiz")
                    }
                }
            }
        }
        item {
            Panel {
                Text("Dividir e conquistar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Escolha um assunto ou um PDF nas telas de estudo para avançar por partes.")
                Spacer(Modifier.height(8.dp))
                Text("Materiais: ${content.studyGuides.size} | Assuntos: ${content.topics.size} | Termos: ${content.glossary.size} | Treinos discursivos: ${content.examPractice.size}")
            }
        }
        item {
            Panel {
                Text("Estudo antes dos jogos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("A aba Estudo mostra cada PDF com árvore de conceitos, fluxo, diagrama e perguntas metacognitivas.")
            }
        }
        item {
            Panel {
                Text("Treino discursivo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("A prova passada cobra comparação, explicação de correntes e diferenciação de pesquisas com suas palavras.")
                Spacer(Modifier.height(8.dp))
                Text("Use a aba Treino para praticar combinações e aprofundamentos possíveis.")
            }
        }
        item {
            Panel {
                Text("Tópicos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                content.topics.forEach { topic ->
                    val topicCards = content.flashcards.count { it.topicId == topic.id }
                    Text("${topic.name} - $topicCards cards", fontWeight = FontWeight.SemiBold)
                    Text(topic.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        item {
            OutlinedButton(onClick = viewModel::resetProgress, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Reiniciar progresso local")
            }
        }
    }
}

@Composable
fun FlashcardScreen(viewModel: StudyViewModel, onlyErrors: Boolean, onSource: (SourceReference) -> Unit) {
    var selectedTopicId by remember(onlyErrors) { mutableStateOf<String?>(null) }
    var selectedPdf by remember(onlyErrors) { mutableStateOf<String?>(null) }
    val baseCards = if (onlyErrors) {
        viewModel.content.flashcards.filter { it.id in viewModel.progress.errorCardIds }
    } else {
        viewModel.content.flashcards.sortedWith(compareBy<Flashcard> { it.id in viewModel.progress.masteredCardIds }.thenBy { it.difficulty })
    }
    val cards = baseCards.filter { card ->
        (selectedTopicId == null || card.topicId == selectedTopicId) &&
            (selectedPdf == null || card.source.pdf == selectedPdf)
    }
    var index by remember(onlyErrors, selectedTopicId, selectedPdf, viewModel.progress.errorCardIds) { mutableIntStateOf(0) }
    var confidence by remember(index, onlyErrors, selectedTopicId, selectedPdf) { mutableStateOf<Confidence?>(null) }
    var revealed by remember(index, onlyErrors, selectedTopicId, selectedPdf) { mutableStateOf(false) }
    val card = cards.getOrNull(index.coerceAtMost((cards.size - 1).coerceAtLeast(0)))

    if (card == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(if (onlyErrors) "Revisão de erros" else "Flashcards", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            item {
                StudyFilterPanel(
                    content = viewModel.content,
                    selectedTopicId = selectedTopicId,
                    onTopicSelected = { selectedTopicId = it },
                    selectedPdf = selectedPdf,
                    onPdfSelected = { selectedPdf = it }
                )
            }
            item {
                EmptyStatePanel(
                    title = if (onlyErrors) "Sem erros pendentes" else "Sem cards disponíveis",
                    message = if (onlyErrors) "Quando você errar ou marcar baixa confiança, o card aparece aqui." else "Não há cards para o recorte escolhido."
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(if (onlyErrors) "Revisão de erros" else "Flashcards", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${index + 1} de ${cards.size} - ${card.type}", style = MaterialTheme.typography.bodyMedium)
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        item {
            Panel {
                Text(card.front, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                Text("Antes de revelar, marque sua confiança:", fontWeight = FontWeight.SemiBold)
                ConfidencePicker(confidence = confidence, onSelected = { confidence = it })
                Spacer(Modifier.height(10.dp))
                Button(onClick = { revealed = true }, enabled = confidence != null, modifier = Modifier.fillMaxWidth()) {
                    Text("Revelar resposta")
                }
            }
        }
        if (revealed) {
            item {
                Panel {
                    Text("Resposta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(card.back)
                    Spacer(Modifier.height(12.dp))
                    SourceButton(card.source, onSource)
                    Spacer(Modifier.height(12.dp))
                    Text("Como foi sua recuperação?", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.recordCard(card, confidence ?: Confidence.None, false)
                                index = ((index + 1) % cards.size.coerceAtLeast(1))
                                revealed = false
                                confidence = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Errei")
                        }
                        Button(
                            onClick = {
                                viewModel.recordCard(card, confidence ?: Confidence.None, true)
                                index = ((index + 1) % cards.size.coerceAtLeast(1))
                                revealed = false
                                confidence = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Acertei")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfidencePicker(confidence: Confidence?, onSelected: (Confidence) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Confidence.entries.forEach { item ->
            FilterChip(
                selected = confidence == item,
                onClick = { onSelected(item) },
                label = { Text(item.label) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MaterialStudyScreen(viewModel: StudyViewModel, onSource: (SourceReference) -> Unit) {
    val guides = viewModel.content.studyGuides
    var selectedPdf by remember { mutableStateOf(guides.firstOrNull()?.pdf) }
    val guide = guides.firstOrNull { it.pdf == selectedPdf } ?: guides.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Estudo por material", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Entenda o conteúdo antes de partir para cards, quiz ou prova.")
        }
        item {
            Panel {
                Text("Material", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                guides.forEach { item ->
                    FilterChip(
                        selected = item.pdf == selectedPdf,
                        onClick = { selectedPdf = item.pdf },
                        label = { Text(item.pdf) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (guide == null) {
            item {
                EmptyStatePanel(
                    title = "Sem guias de estudo",
                    message = "Inclua um guia para cada PDF em study_content.json."
                )
            }
            return@LazyColumn
        }
        item {
            Panel {
                Text(guide.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(guide.purpose)
                Spacer(Modifier.height(10.dp))
                SourceButton(guide.source, onSource)
            }
        }
        item {
            StudyVisualOverview(guide)
        }
        item {
            VisualBlock(
                title = "Árvore de conceitos",
                items = guide.visualTree,
                prefix = "├ "
            )
        }
        item {
            VisualBlock(
                title = "Fluxo de raciocínio",
                items = guide.flow,
                prefix = "→ "
            )
        }
        item {
            VisualBlock(
                title = "Diagrama textual",
                items = guide.diagram,
                prefix = "□ "
            )
        }
        item {
            VisualBlock(
                title = "Metacognição",
                items = guide.metacognition,
                prefix = "? "
            )
        }
        item {
            VisualBlock(
                title = "Estratégias de estudo",
                items = guide.strategies,
                prefix = "✓ "
            )
        }
    }
}

@Composable
fun StudyVisualOverview(guide: MaterialStudyGuide) {
    Panel {
        Text("Mapa visual rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(guide.title, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        guide.visualTree.take(6).forEachIndexed { index, item ->
            val marker = when (index) {
                0 -> "●"
                1, 2 -> "├"
                else -> "└"
            }
            Text("$marker $item")
        }
        Spacer(Modifier.height(10.dp))
        Text("Caminho mental", fontWeight = FontWeight.SemiBold)
        Text(guide.flow.take(4).joinToString(" → "))
    }
}

@Composable
fun VisualBlock(title: String, items: List<String>, prefix: String) {
    Panel {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            Text("$prefix$item")
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun QuizScreen(
    viewModel: StudyViewModel,
    onSource: (SourceReference) -> Unit
) {
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedPdf by remember { mutableStateOf<String?>(null) }
    val quizzes = viewModel.content.quizzes.filter { quiz ->
        (selectedTopicId == null || quiz.topicId == selectedTopicId) &&
            (selectedPdf == null || quiz.source.pdf == selectedPdf)
    }
    var index by remember(selectedTopicId, selectedPdf) { mutableIntStateOf(0) }
    var selected by remember(index, selectedTopicId, selectedPdf) { mutableStateOf<Int?>(null) }
    var answered by remember(index, selectedTopicId, selectedPdf) { mutableStateOf(false) }
    var cardOffsetX by remember(index, selectedTopicId, selectedPdf) { mutableStateOf(0f) }
    val question = quizzes.getOrNull(index.coerceAtMost((quizzes.size - 1).coerceAtLeast(0)))
    fun nextQuestion() {
        index = (index + 1) % quizzes.size
        selected = null
        answered = false
        cardOffsetX = 0f
    }
    val cardDragState = rememberDraggableState { delta ->
        if (answered) {
            cardOffsetX = (cardOffsetX + delta).coerceIn(-220f, 0f)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Quiz", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(if (question == null) "Sem questões neste recorte" else "${index + 1} de ${quizzes.size}")
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        if (question == null) {
            item {
                EmptyStatePanel(
                    title = "Sem questões disponíveis",
                    message = "Escolha outro assunto ou PDF para continuar praticando."
                )
            }
            return@LazyColumn
        }
        item {
            Panel(
                modifier = Modifier
                    .offset { IntOffset(cardOffsetX.roundToInt(), 0) }
                    .draggable(
                        enabled = answered,
                        orientation = Orientation.Horizontal,
                        state = cardDragState,
                        onDragStopped = {
                            if (cardOffsetX <= -130f) {
                                nextQuestion()
                            } else {
                                cardOffsetX = 0f
                            }
                        }
                    )
            ) {
                Text(question.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                SourceButton(question.source, onSource)
                Spacer(Modifier.height(12.dp))
                question.options.forEachIndexed { optionIndex, option ->
                    val isCorrect = optionIndex == question.correctOptionIndex
                    val color = when {
                        !answered -> MaterialTheme.colorScheme.surface
                        isCorrect -> Color(0xFFE2F4EA)
                        selected == optionIndex -> Color(0xFFF8DCDC)
                        else -> MaterialTheme.colorScheme.surface
                    }
                    OutlinedButton(
                        onClick = {
                            selected = optionIndex
                            answered = true
                            viewModel.recordQuiz(question, optionIndex == question.correctOptionIndex)
                        },
                        enabled = !answered,
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = color),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(option, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (answered) {
                    val correct = selected == question.correctOptionIndex
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (correct) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = if (correct) Color(0xFF1F7A43) else Color(0xFFB3261E)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(if (correct) "Acerto consolidado" else "Erro bom: agora ele entra no radar", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    SwipeNextControl(
                        onSwiped = { nextQuestion() }
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeNextControl(onSwiped: () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    val dragState = rememberDraggableState { delta ->
        offsetX = (offsetX + delta).coerceIn(-180f, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFE8F1EF))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Arraste o botão para a esquerda")
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .width(132.dp)
                .height(44.dp)
                .background(MaterialTheme.colorScheme.primary)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = dragState,
                    onDragStopped = {
                        if (offsetX <= -110f) {
                            onSwiped()
                        }
                        offsetX = 0f
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Próxima", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = onSwiped, modifier = Modifier.fillMaxWidth()) {
        Text("Próxima")
    }
}

@Composable
fun QuizScreenOld(viewModel: StudyViewModel, onSource: (SourceReference) -> Unit) {
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedPdf by remember { mutableStateOf<String?>(null) }
    val quizzes = viewModel.content.quizzes.filter { quiz ->
        (selectedTopicId == null || quiz.topicId == selectedTopicId) &&
            (selectedPdf == null || quiz.source.pdf == selectedPdf)
    }
    var index by remember(selectedTopicId, selectedPdf) { mutableIntStateOf(0) }
    var selected by remember(index, selectedTopicId, selectedPdf) { mutableStateOf<Int?>(null) }
    var answered by remember(index, selectedTopicId, selectedPdf) { mutableStateOf(false) }
    val question = quizzes.getOrNull(index.coerceAtMost((quizzes.size - 1).coerceAtLeast(0)))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Quiz", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(if (question == null) "Sem questões neste recorte" else "${index + 1} de ${quizzes.size}")
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        if (question == null) {
            item {
                EmptyStatePanel(
                    title = "Sem questões disponíveis",
                    message = "Escolha outro assunto ou PDF para continuar praticando."
                )
            }
            return@LazyColumn
        }
        item {
            Panel {
                Text(question.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                question.options.forEachIndexed { optionIndex, option ->
                    val isCorrect = optionIndex == question.correctOptionIndex
                    val color = when {
                        !answered -> MaterialTheme.colorScheme.surface
                        isCorrect -> Color(0xFFE2F4EA)
                        selected == optionIndex -> Color(0xFFF8DCDC)
                        else -> MaterialTheme.colorScheme.surface
                    }
                    OutlinedButton(
                        onClick = {
                            selected = optionIndex
                            answered = true
                            viewModel.recordQuiz(question, optionIndex == question.correctOptionIndex)
                        },
                        enabled = !answered,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color)
                    ) {
                        Text(option, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (answered) {
            item {
                Panel {
                    val correct = selected == question.correctOptionIndex
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (correct) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = if (correct) Color(0xFF1F7A43) else Color(0xFFB3261E)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(if (correct) "Acerto consolidado" else "Erro bom: agora ele entra no radar", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(question.explanation)
                    Spacer(Modifier.height(12.dp))
                    SourceButton(question.source, onSource)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            index = (index + 1) % quizzes.size
                            selected = null
                            answered = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Próxima questão")
                    }
                }
            }
        }
    }
}

@Composable
fun RelationsScreen(viewModel: StudyViewModel, onSource: (SourceReference) -> Unit) {
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedPdf by remember { mutableStateOf<String?>(null) }
    val relations = viewModel.content.relations.filter { relation ->
        (selectedTopicId == null || relation.topicId == selectedTopicId) &&
            (selectedPdf == null || relation.source.pdf == selectedPdf)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Relações conceituais", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Use esta tela para sair da memorização solta e ligar ideias.", style = MaterialTheme.typography.bodyMedium)
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        if (relations.isEmpty()) {
            item {
                EmptyStatePanel(
                    title = "Sem relações neste recorte",
                    message = "Escolha outro assunto ou PDF para encontrar conexões conceituais."
                )
            }
        }
        items(relations) { relation ->
            Panel {
                Text("${relation.from} -> ${relation.to}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(relation.description)
                Spacer(Modifier.height(10.dp))
                SourceButton(relation.source, onSource)
            }
        }
    }
}

@Composable
fun GlossaryScreen(viewModel: StudyViewModel, onSource: (SourceReference) -> Unit) {
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedPdf by remember { mutableStateOf<String?>(null) }
    val terms = viewModel.content.glossary
        .filter { term ->
            (selectedTopicId == null || term.topicId == selectedTopicId) &&
                (selectedPdf == null || term.source.pdf == selectedPdf)
        }
        .sortedBy { it.term }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Termos e conceitos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Consulte significados rápidos antes de avançar para cards e quizzes.")
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        if (terms.isEmpty()) {
            item {
                EmptyStatePanel(
                    title = "Sem termos neste recorte",
                    message = "Limpe os filtros ou escolha outro assunto para consultar conceitos."
                )
            }
        }
        items(terms) { term ->
            Panel {
                Text(term.term, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(term.definition)
                Spacer(Modifier.height(10.dp))
                SourceButton(term.source, onSource)
            }
        }
    }
}

@Composable
fun ExamPracticeScreen(viewModel: StudyViewModel, onSource: (SourceReference) -> Unit) {
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedPdf by remember { mutableStateOf<String?>(null) }
    val questions = viewModel.content.examPractice.filter { question ->
        (selectedTopicId == null || question.topicId == selectedTopicId) &&
            (selectedPdf == null || question.source.pdf == selectedPdf)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Treino discursivo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Pratique combinações de conceitos, aprofundamento e respostas articuladas.")
        }
        item {
            Text("Modelos de prova", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(viewModel.content.examModels) { model ->
            Panel {
                Text(model.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(model.focus)
                Text(model.duration, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                model.questions.forEachIndexed { index, question ->
                    Text("${index + 1}. $question")
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Como corrigir", fontWeight = FontWeight.SemiBold)
                model.rubric.forEach { item ->
                    Text("- $item")
                }
                Spacer(Modifier.height(10.dp))
                SourceButton(model.source, onSource)
            }
        }
        item {
            Text("Treinos por competência", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        item {
            Panel {
                Text("Como responder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Comece definindo os termos, compare ideias, explique relações e feche com uma frase conclusiva.")
                Spacer(Modifier.height(8.dp))
                Text("A prova passada orienta o padrão: comparar, explicar, relacionar e justificar com clareza.")
            }
        }
        item {
            StudyFilterPanel(
                content = viewModel.content,
                selectedTopicId = selectedTopicId,
                onTopicSelected = { selectedTopicId = it },
                selectedPdf = selectedPdf,
                onPdfSelected = { selectedPdf = it }
            )
        }
        if (questions.isEmpty()) {
            item {
                EmptyStatePanel(
                    title = "Sem treinos neste recorte",
                    message = "Limpe os filtros para ver exercícios discursivos mais amplos."
                )
            }
        }
        items(questions) { question ->
            Panel {
                Text(question.weight, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(question.prompt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text("Estratégia de resposta", fontWeight = FontWeight.SemiBold)
                Text(question.approach)
                Spacer(Modifier.height(10.dp))
                Text("Checklist", fontWeight = FontWeight.SemiBold)
                question.checklist.forEach { item ->
                    Text("- $item")
                }
                Spacer(Modifier.height(10.dp))
                SourceButton(question.source, onSource)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyFilterPanel(
    content: StudyContent,
    selectedTopicId: String?,
    onTopicSelected: (String?) -> Unit,
    selectedPdf: String?,
    onPdfSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTopic = content.topics.firstOrNull { it.id == selectedTopicId }?.name ?: "Todos os assuntos"
    val selectedDocument = selectedPdf ?: "Todos os PDFs"

    Panel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Recorte de estudo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$selectedTopic | $selectedDocument", style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Recolher" else "Filtros")
            }
        }
        if (!expanded) {
            return@Panel
        }
        Spacer(Modifier.height(10.dp))
        Text("Assunto", fontWeight = FontWeight.SemiBold)
        FilterChip(
            selected = selectedTopicId == null,
            onClick = { onTopicSelected(null) },
            label = { Text("Todos os assuntos") },
            modifier = Modifier.fillMaxWidth()
        )
        content.topics.forEach { topic ->
            FilterChip(
                selected = selectedTopicId == topic.id,
                onClick = { onTopicSelected(topic.id) },
                label = { Text(topic.name) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(10.dp))
        Text("Documento", fontWeight = FontWeight.SemiBold)
        FilterChip(
            selected = selectedPdf == null,
            onClick = { onPdfSelected(null) },
            label = { Text("Todos os PDFs") },
            modifier = Modifier.fillMaxWidth()
        )
        content.availablePdfs().forEach { pdf ->
            FilterChip(
                selected = selectedPdf == pdf,
                onClick = { onPdfSelected(pdf) },
                label = { Text(pdf) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceScreen(source: SourceReference, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fonte do PDF", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Panel {
                Text(source.pdf, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Página ${source.page}")
                Spacer(Modifier.height(12.dp))
                Text(source.excerpt)
            }
        }
    }
}

@Composable
fun MissionRow(label: String, value: Int, target: Int) {
    val progress = (value.toFloat() / target).coerceIn(0f, 1f)
    Spacer(Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Text("${value.coerceAtMost(target)}/$target")
    }
    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
}

@Composable
fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SourceButton(source: SourceReference, onSource: (SourceReference) -> Unit) {
    OutlinedButton(onClick = { onSource(source) }, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.Source, contentDescription = null)
        Spacer(Modifier.size(8.dp))
        Text("${source.pdf}, p. ${source.page}", maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun EmptyState(title: String, message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF1F6F68))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(message)
        }
    }
}

@Composable
fun EmptyStatePanel(title: String, message: String) {
    Panel {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(message)
    }
}

fun StudyContent.availablePdfs(): List<String> {
    return (flashcards.map { it.source.pdf } +
        quizzes.map { it.source.pdf } +
        relations.map { it.source.pdf } +
        glossary.map { it.source.pdf } +
        examPractice.map { it.source.pdf } +
        examModels.map { it.source.pdf } +
        studyGuides.map { it.pdf })
        .distinct()
        .sorted()
}

fun JSONObject.toSource(): SourceReference {
    return SourceReference(
        pdf = getString("pdf"),
        page = getInt("page"),
        excerpt = getString("excerpt")
    )
}

fun JSONArray.mapStrings(): List<String> {
    return (0 until length()).map { getString(it) }
}

fun <T> JSONArray.mapObjects(block: JSONObject.() -> T): List<T> {
    return (0 until length()).map { getJSONObject(it).block() }
}
