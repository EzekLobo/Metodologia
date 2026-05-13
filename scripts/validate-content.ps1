$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$contentPath = Join-Path $root "app/src/main/assets/study_content.json"
$manifestPath = Join-Path $root "app/src/main/AndroidManifest.xml"

if (!(Test-Path $contentPath)) {
    throw "Missing study_content.json"
}

$json = Get-Content -Raw $contentPath | ConvertFrom-Json

foreach ($collectionName in @("flashcards", "quizzes", "relations")) {
    foreach ($item in $json.$collectionName) {
        if ([string]::IsNullOrWhiteSpace($item.id)) {
            throw "$collectionName has item without id"
        }
        if ($null -eq $item.source) {
            throw "$collectionName $($item.id) has no source"
        }
        if ([string]::IsNullOrWhiteSpace($item.source.pdf)) {
            throw "$collectionName $($item.id) has no source pdf"
        }
        if ($item.source.page -le 0) {
            throw "$collectionName $($item.id) has invalid source page"
        }
        if ([string]::IsNullOrWhiteSpace($item.source.excerpt)) {
            throw "$collectionName $($item.id) has no source excerpt"
        }
    }
}

foreach ($card in $json.flashcards) {
    if ([string]::IsNullOrWhiteSpace($card.front) -or [string]::IsNullOrWhiteSpace($card.back)) {
        throw "Flashcard $($card.id) is incomplete"
    }
    if ($card.difficulty -lt 1 -or $card.difficulty -gt 3) {
        throw "Flashcard $($card.id) has invalid difficulty"
    }
}

foreach ($quiz in $json.quizzes) {
    if ($quiz.options.Count -lt 2) {
        throw "Quiz $($quiz.id) needs at least two options"
    }
    if ($quiz.correctOptionIndex -lt 0 -or $quiz.correctOptionIndex -ge $quiz.options.Count) {
        throw "Quiz $($quiz.id) has invalid correctOptionIndex"
    }
    if ([string]::IsNullOrWhiteSpace($quiz.explanation)) {
        throw "Quiz $($quiz.id) has no explanation"
    }
}

$manifest = Get-Content -Raw $manifestPath
if ($manifest.Contains("android.permission.INTERNET")) {
    throw "Offline app must not request INTERNET permission"
}

Write-Output "OK: topics=$($json.topics.Count), flashcards=$($json.flashcards.Count), quizzes=$($json.quizzes.Count), relations=$($json.relations.Count), offline=true"
