package com.zygisk.kavya_kanaja.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.zygisk.kavya_kanaja.ui.viewmodel.PoemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsListScreen(
    poemViewModel: PoemViewModel,
    onAuthorClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val allPoems by poemViewModel.poems.collectAsState()
    val poemsByPoet = allPoems.groupBy { it.poet }

    val authorImages = remember {
        mapOf(
            "kuvempu" to com.zygisk.kavya_kanaja.R.drawable.author_kuvempu,
            "dvgundappa" to com.zygisk.kavya_kanaja.R.drawable.author_d_v_gundappa,
            "drbendre" to com.zygisk.kavya_kanaja.R.drawable.author_d_r_bendre,
            "basavanna" to com.zygisk.kavya_kanaja.R.drawable.author_basavanna,
            "akkamahadevi" to com.zygisk.kavya_kanaja.R.drawable.author_akka_mahadevi,
            "purandaradasa" to com.zygisk.kavya_kanaja.R.drawable.author_purandara_dasa,
            "gsshivarudrappa" to com.zygisk.kavya_kanaja.R.drawable.author_g_s_shivarudrappa,
            "ksnissarahmed" to com.zygisk.kavya_kanaja.R.drawable.author_k_s_nissar_ahmed,
            "gopalakrishnaadiga" to com.zygisk.kavya_kanaja.R.drawable.author_gopalakrishna_adiga,
            "jayantkaikini" to com.zygisk.kavya_kanaja.R.drawable.author_jayant_kaikini,
            "bksumitra" to com.zygisk.kavya_kanaja.R.drawable.author_b_k_sumitra,
            "shishunalasharif" to com.zygisk.kavya_kanaja.R.drawable.author_shishunala_sharif,
            "nslakshminarayanabhatta" to com.zygisk.kavya_kanaja.R.drawable.author_n_s_lakshminarayana_bhatta,
            "sathyanandapathrota" to com.zygisk.kavya_kanaja.R.drawable.author_sathyananda_pathrota,
            "channabasavanna" to com.zygisk.kavya_kanaja.R.drawable.author_channa_basavanna,
            "vanand" to com.zygisk.kavya_kanaja.R.drawable.author_v_anand,
            "chiudayashankar" to com.zygisk.kavya_kanaja.R.drawable.author_chi_udayashankar,
            "kanakadasa" to com.zygisk.kavya_kanaja.R.drawable.author_kanakadasa,
            "ksnarasimhaswamy" to com.zygisk.kavya_kanaja.R.drawable.author_k_s_narasimhaswamy,
            "kirankaverappa" to com.zygisk.kavya_kanaja.R.drawable.author_kiran_kaverappa,
            "folk" to com.zygisk.kavya_kanaja.R.drawable.author_folk
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Authors", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(poemsByPoet.keys.toList().sorted()) { poet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable { onAuthorClick(poet) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val normalizedPoet = poet.replace(".", "").replace(" ", "").lowercase()
                    val imageUrl = authorImages[normalizedPoet]
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = poet,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp).align(Alignment.Center),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Text(
                                    text = poet.take(1).uppercase(),
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = poet,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        val count = poemsByPoet[poet]?.size ?: 0
                        Text(
                            text = "$count ${if (count == 1) "poem" else "poems"}",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
