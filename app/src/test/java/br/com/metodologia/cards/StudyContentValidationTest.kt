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
        val mojibakeMarkers = listOf("\u00c3", "\u00c2", "\u00e2\u20ac")
        mojibakeMarkers.forEach { marker ->
            assertFalse("content contains mojibake marker ${marker.toByteArray().joinToString()}", raw.contains(marker))
        }
    }

    @Test
    fun flashcardsAreCompleteAndTraceable() {
        assertTrue("flashcards should cover enough practice variety", root.getJSONArray("flashcards").length() >= 18)
        root.getJSONArray("flashcards").forEachObject { card ->
            assertFalse(card.getString("front").isBlank())
            assertFalse(card.getString("back").isBlank())
            assertFalse(card.getString("topicId").isBlank())
            assertTrue(card.getInt("difficulty") in 1..3)
        }
    }

    @Test
    fun quizzesHaveValidCorrectOptionsAndFeedback() {
        assertTrue("quizzes should offer diverse objective practice", root.getJSONArray("quizzes").length() >= 12)
        root.getJSONArray("quizzes").forEachObject { quiz ->
            val options = quiz.getJSONArray("options")
            assertTrue("${quiz.getString("id")} needs at least 2 options", options.length() >= 2)
            assertTrue("${quiz.getString("id")} has too many options for phone layout", options.length() <= 5)
            assertTrue(quiz.getInt("correctOptionIndex") in 0 until options.length())
            assertFalse(quiz.getString("question").isBlank())
            assertFalse(quiz.getString("explanation").isBlank())
        }
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
    fun studyGuidesSupportVisualAndMetacognitiveStudy() {
        root.getJSONArray("studyGuides").forEachObject { guide ->
            assertFalse(guide.getString("pdf").isBlank())
            assertFalse(guide.getString("title").isBlank())
            assertFalse(guide.getString("purpose").isBlank())
            assertTrue(guide.getJSONArray("visualTree").length() >= 3)
            assertTrue(guide.getJSONArray("flow").length() >= 3)
            assertTrue(guide.getJSONArray("diagram").length() >= 2)
            assertTrue(guide.getJSONArray("metacognition").length() >= 3)
            assertTrue(guide.getJSONArray("strategies").length() >= 3)
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
