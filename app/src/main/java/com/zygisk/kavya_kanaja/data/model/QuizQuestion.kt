package com.zygisk.kavya_kanaja.data.model

sealed class QuizQuestion {
    abstract val questionText: String
    abstract val options: List<String>
    abstract val correctAnswer: String

    data class MeaningQuestion(
        override val questionText: String,
        override val options: List<String>,
        override val correctAnswer: String,
        val kannadaWord: String,
        val poemTitle: String
    ) : QuizQuestion()

    data class PoetIdentificationQuestion(
        override val questionText: String,
        override val options: List<String>,
        override val correctAnswer: String,
        val imageRes: String
    ) : QuizQuestion()
}
