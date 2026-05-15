package com.zygisk.kavya_kanaja.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel
import com.zygisk.kavya_kanaja.data.model.QuizQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    poemViewModel: PoemViewModel,
    onBack: () -> Unit
) {
    val poems by poemViewModel.poems.collectAsState()
    val poetBios by poemViewModel.poetBios.collectAsState()

    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var hasStarted by remember { mutableStateOf(false) }

    fun generateQuestions() {
        val generatedQuestions = mutableListOf<QuizQuestion>()
        
        // Meaning Questions
        val allMeanings = mutableListOf<Pair<String, String>>()
        poems.forEach { poem ->
            poem.meanings.forEach { (kannada, english) ->
                allMeanings.add(Pair(kannada, english))
            }
        }
        val meaningOptionsPool = allMeanings.map { it.second }.distinct()

        allMeanings.shuffled().take(20).forEach { (kannada, english) ->
            val wrongOptions = meaningOptionsPool.filter { it != english }.shuffled().take(3)
            val options = (wrongOptions + english).shuffled()
            generatedQuestions.add(
                QuizQuestion.MeaningQuestion(
                    questionText = "What is the English meaning of '$kannada'?",
                    options = options,
                    correctAnswer = english,
                    kannadaWord = kannada,
                    poemTitle = ""
                )
            )
        }

        // Poet Identification Questions
        val allPoets = poetBios.filter { it.imageRes.isNotEmpty() }
        val poetNamesPool = poetBios.map { it.name }

        allPoets.shuffled().take(10).forEach { bio ->
            val wrongOptions = poetNamesPool.filter { it != bio.name }.shuffled().take(3)
            val options = (wrongOptions + bio.name).shuffled()
            generatedQuestions.add(
                QuizQuestion.PoetIdentificationQuestion(
                    questionText = "Identify the poet in the picture:",
                    options = options,
                    correctAnswer = bio.name,
                    imageRes = bio.imageRes
                )
            )
        }

        questions = generatedQuestions.shuffled().take(30)
        currentQuestionIndex = 0
        score = 0
        isFinished = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Literary Quizzes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (!hasStarted) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Test Your Knowledge!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Identify Kannada meanings and poet biographies. Earn badges and improve your literary skills.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { 
                            generateQuestions()
                            hasStarted = true 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Start Quiz", fontSize = 18.sp)
                    }
                }
            } else if (isFinished) {
                // Result screen
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Quiz Finished!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val percentage = if (questions.isNotEmpty()) (score.toFloat() / questions.size) * 100 else 0f
                    val badge = when {
                        percentage >= 90 -> "🏆 Literary Master"
                        percentage >= 70 -> "🥇 Scholar"
                        percentage >= 50 -> "🥈 Enthusiast"
                        else -> "🥉 Beginner"
                    }
                    
                    Text(
                        text = "Your Score: $score / ${questions.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { generateQuestions() },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Play Again", fontSize = 18.sp)
                    }
                }
            } else {
                // Active Quiz Screen
                val currentQuestion = questions.getOrNull(currentQuestionIndex)
                if (currentQuestion != null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        if (currentQuestion is QuizQuestion.PoetIdentificationQuestion) {
                            val context = LocalContext.current
                            val resourceId = context.resources.getIdentifier(currentQuestion.imageRes, "drawable", context.packageName)
                            if (resourceId != 0) {
                                Image(
                                    painter = painterResource(id = resourceId),
                                    contentDescription = "Poet",
                                    modifier = Modifier.size(150.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        currentQuestion.options.forEach { option ->
                            OutlinedButton(
                                onClick = {
                                    if (option == currentQuestion.correctAnswer) {
                                        score++
                                    }
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                    } else {
                                        isFinished = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = option, fontSize = 16.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
