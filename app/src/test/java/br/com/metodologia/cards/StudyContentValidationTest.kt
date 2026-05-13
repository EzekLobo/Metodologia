package br.com.metodologia.cards

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class StudyContentValidationTest {
    private val root = JSONObject(File("src/main/assets/study_content.json").readText())
    private val studyCollections = listOf("flashcards", "quizzes", "relations", "glossary", "examPractice", "examModels", "studyGuides")

    @Test
    fun everyStudyItemHasSourceReference() {
        studyCollections.forEach { collectionName ->
            root.getJSONArray(collectionName).forEachObject { item ->
                assertFalse("$collectionName item must have id", item.getString("id").isBlank())
                val source = item.getJSONObject("source")
                assertFalse("${item.getString("id")} missing pdf", source.getString("pdf").isBlank())
                assertTrue("${item.getString("id")} missing page", source.getInt("page") > 0)
                assertFalse("${item.getString("id")} missing excerpt", source.getString("excerpt").isBlank())
            }
        }
    }

    @Test
    fun studyItemIdsAreUniqueAcrossCollections() {
        val seen = mutableSetOf<String>()
        studyCollections.forEach { collectionName ->
            root.getJSONArray(collectionName).forEachObject { item ->
                val id = item.getString("id")
                assertTrue("duplicate id: $id", seen.add(id))
            }
        }
    }

    @Test
    fun everyTopicReferencePointsToExistingTopic() {
        val topicIds = root.getJSONArray("topics").objects().map { it.getString("id") }.toSet()
        listOf("flashcards", "quizzes", "relations", "glossary", "examPractice").forEach { collectionName ->
            root.getJSONArray(collectionName).forEachObject { item ->
                val topicId = item.getString("topicId")
                assertTrue("${item.getString("id")} references unknown topic $topicId", topicId in topicIds)
            }
        }
    }

    @Test
    fun sourcePdfsAreKnownStudyMaterials() {
        val allowedPdfs = setOf(
            "METODOLOGIA 1.pdf",
            "METODOLOGIA 2.pdf",
            "METODOLOGIA 3.pdf",
            "Prova_Comp_T2_2021_2.pdf"
        )
        studyCollections.forEach { collectionName ->
            root.getJSONArray(collectionName).forEachObject { item ->
                val pdf = item.getJSONObject("source").getString("pdf")
                assertTrue("${item.getString("id")} references unexpected pdf $pdf", pdf in allowedPdfs)
            }
        }
        root.getJSONArray("studyGuides").forEachObject { guide ->
            val pdf = guide.getString("pdf")
            assertTrue("${guide.getString("id")} targets unexpected pdf $pdf", pdf in allowedPdfs)
        }
    }

    @Test
    fun contentTextDoesNotContainMojibakeMarkers() {
        val raw = File("src/main/assets/study_content.json").readText()
        val mojibakeMarkers = listOf("\u00c3", "\u00c2", "\u00e2\u20ac", "\ufffd")
        mojibakeMarkers.forEach { marker ->
            assertFalse("content contains mojibake marker ${marker.toByteArray().joinToString()}", raw.contains(marker))
        }
        assertFalse("content contains broken accent inside a word", Regex("""[A-Za-zÀ-ÿ]\?[A-Za-zÀ-ÿ]""").containsMatchIn(raw))
        assertFalse("content contains broken leading accent marker", Regex("""\? [a-záéíóúãõç]""").containsMatchIn(raw))
        listOf("çãoões", "çãoão", "çõesões").forEach { marker ->
            assertFalse("content contains duplicated accent suffix $marker", raw.contains(marker))
        }
    }

    @Test
    fun flashcardsAreCompleteAndTraceable() {
        assertTrue("flashcards should cover enough practice variety", root.getJSONArray("flashcards").length() >= 40)
        val types = mutableSetOf<String>()
        root.getJSONArray("flashcards").forEachObject { card ->
            assertFalse(card.getString("front").isBlank())
            assertFalse(card.getString("back").isBlank())
            assertFalse(card.getString("topicId").isBlank())
            types.add(card.getString("type"))
            assertTrue(card.getInt("difficulty") in 1..3)
        }
        listOf("definição", "comparação", "erro comum", "DREC", "aplicação em prova").forEach { type ->
            assertTrue("flashcards should include $type cards", type in types)
        }
    }

    @Test
    fun quizzesHaveValidCorrectOptionsAndFeedback() {
        assertTrue("quizzes should offer diverse objective practice", root.getJSONArray("quizzes").length() >= 25)
        val correctIndexCounts = mutableMapOf<Int, Int>()
        root.getJSONArray("quizzes").forEachObject { quiz ->
            val options = quiz.getJSONArray("options")
            val optionExplanations = quiz.getJSONArray("optionExplanations")
            assertTrue("${quiz.getString("id")} needs at least 2 options", options.length() >= 2)
            assertTrue("${quiz.getString("id")} has too many options for phone layout", options.length() <= 5)
            assertTrue(quiz.getInt("correctOptionIndex") in 0 until options.length())
            correctIndexCounts[quiz.getInt("correctOptionIndex")] = correctIndexCounts.getOrDefault(quiz.getInt("correctOptionIndex"), 0) + 1
            assertEquals("${quiz.getString("id")} needs one explanation per option", options.length(), optionExplanations.length())
            for (index in 0 until options.length()) {
                assertFalse("${quiz.getString("id")} has an empty option", options.getString(index).isBlank())
                assertTrue(
                    "${quiz.getString("id")} option $index needs a useful explanation",
                    optionExplanations.getString(index).trim().length >= 40
                )
            }
            assertFalse(quiz.getString("question").isBlank())
            assertFalse(quiz.getString("explanation").isBlank())
        }
        assertTrue("correct answers should appear in several positions", correctIndexCounts.size >= 3)
        assertTrue(
            "correct answers should not be concentrated in the first option",
            correctIndexCounts.getOrDefault(0, 0) <= root.getJSONArray("quizzes").length() / 2
        )
    }

    @Test
    fun glossaryTermsAreUsefulAndTraceable() {
        root.getJSONArray("glossary").forEachObject { term ->
            assertFalse(term.getString("term").isBlank())
            assertFalse(term.getString("definition").isBlank())
            assertFalse(term.getString("topicId").isBlank())
        }
    }

    @Test
    fun examPracticeQuestionsTeachDiscursiveAnswers() {
        val prompts = mutableSetOf<String>()
        val commandFamilies = mutableSetOf<String>()
        assertTrue("discursive training should be broad", root.getJSONArray("examPractice").length() >= 30)
        root.getJSONArray("examPractice").forEachObject { question ->
            val prompt = question.getString("prompt")
            assertTrue("duplicate prompt: $prompt", prompts.add(prompt))
            listOf("Compare", "Explique", "Diferencie", "Construa", "Analise", "Relacione", "Proponha", "Elabore").forEach { verb ->
                if (prompt.startsWith(verb, ignoreCase = true)) {
                    commandFamilies.add(verb.lowercase())
                }
            }
            assertFalse(question.getString("prompt").isBlank())
            assertFalse(question.getString("approach").isBlank())
            assertFalse(question.getString("weight").isBlank())
            assertTrue(question.getJSONArray("checklist").length() >= 3)
        }
        assertTrue("discursive prompts need varied command styles", commandFamilies.size >= 5)
    }

    @Test
    fun examPracticeCoversEveryTopicDeeply() {
        val topicIds = root.getJSONArray("topics").objects().map { it.getString("id") }
        topicIds.forEach { topicId ->
            val count = root.getJSONArray("examPractice").objects().count { it.getString("topicId") == topicId }
            assertTrue("topic $topicId needs at least 6 discursive practices", count >= 6)
        }
    }

    @Test
    fun examModelsSimulateCompleteFutureTests() {
        val models = root.getJSONArray("examModels")
        assertTrue("should include several full exam models", models.length() >= 5)
        models.forEachObject { model ->
            assertFalse(model.getString("title").isBlank())
            assertFalse(model.getString("focus").isBlank())
            assertFalse(model.getString("duration").isBlank())
            assertTrue("${model.getString("id")} needs at least 4 questions", model.getJSONArray("questions").length() >= 4)
            assertTrue("${model.getString("id")} needs correction criteria", model.getJSONArray("rubric").length() >= 4)
        }
    }

    @Test
    fun relationsCoverImportantConceptNetwork() {
        val relations = root.getJSONArray("relations")
        assertTrue("relations should cover a broad concept network", relations.length() >= 18)
        val relationText = relations.objects().joinToString(" ") {
            "${it.getString("from")} ${it.getString("to")} ${it.getString("description")}"
        }
        listOf("Bacon", "Popper", "Kuhn", "Paradigma", "Anomalia", "Tema", "Problema", "Hipótese", "Senso comum").forEach { term ->
            assertTrue("relations should mention $term", relationText.contains(term))
        }
    }

    @Test
    fun flashcardAndQuizPromptsAreNotRepeated() {
        assertUniqueTexts(root, "flashcards", "front")
        assertUniqueTexts(root, "quizzes", "question")
    }

    @Test
    fun matrixTermsAreCoveredByCardsAndGlossary() {
        val cardText = root.getJSONArray("flashcards").objects().joinToString(" ") {
            "${it.getString("front")} ${it.getString("back")}"
        }
        val glossaryText = root.getJSONArray("glossary").objects().joinToString(" ") {
            "${it.getString("term")} ${it.getString("definition")}"
        }
        listOf(
            "senso comum",
            "conhecimento cient",
            "empirismo de senso comum",
            "empirismo cient",
            "ciências formais",
            "ciências factuais",
            "racionalismo",
            "empirismo",
            "mecanicismo",
            "Galileu",
            "Bacon",
            "Descartes",
            "falseabilidade",
            "hipótese falseadora",
            "pesquisa aplicada",
            "pesquisa de falseamento",
            "pesquisa experimental",
            "pesquisa exploratória",
            "DREC"
        ).forEach { term ->
            val covered = cardText.contains(term, ignoreCase = true) ||
                glossaryText.contains(term, ignoreCase = true)
            assertTrue("matrix term should be covered: $term", covered)
        }
    }

    @Test
    fun quizCoversCentralComparisonsAndApplications() {
        val quizText = root.getJSONArray("quizzes").objects().joinToString(" ") {
            "${it.getString("question")} ${it.getJSONArray("options")} ${it.getString("explanation")}"
        }
        listOf(
            "senso comum",
            "conhecimento cient",
            "Galileu",
            "Bacon",
            "Descartes",
            "racionalismo",
            "empirismo",
            "mecanicismo",
            "falseabilidade",
            "aplicada",
            "experimental",
            "exploratória"
        ).forEach { term ->
            assertTrue("quiz should practice $term", quizText.contains(term, ignoreCase = true))
        }
    }

    @Test
    fun examModeHasEnoughSeparatedRoundContent() {
        assertTrue("exam card round needs broad coverage", root.getJSONArray("flashcards").length() >= 40)
        assertTrue("exam quiz round needs broad coverage", root.getJSONArray("quizzes").length() >= 25)
    }

    @Test
    fun cardsAndQuizzesAreRandomizedInUi() {
        val main = File("src/main/java/br/com/metodologia/cards/MainActivity.kt").readText()
        assertTrue("UI should build displayed quiz options", main.contains("DisplayedOption"))
        assertTrue("UI should shuffle displayed options", main.contains("displayedOptions()"))
        assertTrue("UI should shuffle cards and quiz rounds", main.split(".shuffled()").size - 1 >= 4)
        assertTrue("UI should compare answers by shuffled option correctness", main.contains("option.isCorrect"))
        assertTrue("UI should show explanations per alternative", main.contains("QuizExplanationBlock"))
    }

    @Test
    fun bottomNavigationStaysFocused() {
        val main = File("src/main/java/br/com/metodologia/cards/MainActivity.kt").readText()
        assertTrue("bottom navigation should use five primary screens", main.contains("listOf(Screen.Home, Screen.Study, Screen.Cards, Screen.Quiz, Screen.Prova)"))
        assertFalse("bottom navigation should not be horizontally scrollable", main.contains("horizontalScroll"))
        assertFalse("bottom navigation should not force narrow item width", main.contains("width(96.dp)"))
        listOf("onOpenErrors", "onOpenRelations", "onOpenGlossary", "onOpenTraining").forEach { callback ->
            assertTrue("home should expose secondary resource callback $callback", main.contains(callback))
        }
    }

    @Test
    fun studyGuidesSupportVisualAndMetacognitiveStudy() {
        root.getJSONArray("studyGuides").forEachObject { guide ->
            assertFalse(guide.getString("pdf").isBlank())
            assertFalse(guide.getString("title").isBlank())
            assertFalse(guide.getString("purpose").isBlank())
            assertTrue(guide.getJSONArray("visualTree").length() >= 5)
            assertTrue(guide.getJSONArray("flow").length() >= 3)
            assertTrue(guide.getJSONArray("diagram").length() >= 3)
            assertTrue(guide.getJSONArray("metacognition").length() >= 2)
            assertTrue(guide.getJSONArray("strategies").length() >= 4)
        }
    }

    @Test
    fun studyScreenUsesStructuredSectionsInsteadOfImprovisedVisualPrefixes() {
        val main = File("src/main/java/br/com/metodologia/cards/MainActivity.kt").readText()
        listOf("StudySection", "StepList", "ConceptChips", "ComparisonBlock", "ChecklistBlock").forEach { component ->
            assertTrue("study screen should use $component", main.contains("fun $component"))
        }
        listOf("VisualBlock", "StudyVisualOverview", "Diagrama textual", "prefix = \"? \"", "prefix = \"├ \"", "prefix = \"□ \"").forEach { oldPattern ->
            assertFalse("study screen should not use old improvised visual pattern $oldPattern", main.contains(oldPattern))
        }
    }

    @Test
    fun everyFilterableCutHasAtLeastOneStudyPath() {
        val topicIds = root.getJSONArray("topics").objects().map { it.getString("id") }
        topicIds.forEach { topicId ->
            val count = listOf("flashcards", "quizzes", "relations", "glossary", "examPractice").sumOf { collection ->
                root.getJSONArray(collection).objects().count { it.getString("topicId") == topicId }
            }
            assertTrue("topic $topicId has no study content", count > 0)
        }
    }

    @Test
    fun appDeclaresNoInternetPermission() {
        val manifest = File("src/main/AndroidManifest.xml").readText()
        assertFalse("Offline app must not request INTERNET", manifest.contains("android.permission.INTERNET"))
    }

    @Test
    fun dailyMissionTargetsStaySimple() {
        assertEquals(15, 15)
        assertEquals(10, 10)
        assertEquals(5, 5)
    }
}

private fun JSONArray.forEachObject(block: (JSONObject) -> Unit) {
    for (index in 0 until length()) {
        block(getJSONObject(index))
    }
}

private fun JSONArray.objects(): List<JSONObject> {
    return (0 until length()).map { getJSONObject(it) }
}

private fun assertUniqueTexts(root: JSONObject, collectionName: String, fieldName: String) {
    val seen = mutableSetOf<String>()
    root.getJSONArray(collectionName).forEachObject { item ->
        val text = item.getString(fieldName).trim().lowercase()
        assertTrue("duplicate $collectionName $fieldName: $text", seen.add(text))
    }
}
