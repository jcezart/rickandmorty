// DetailScreen.kt
package com.devspace.rickandmorty.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devspace.rickandmorty.MyApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, characterId: Int) {
    val application = LocalContext.current.applicationContext as MyApplication
    val repository = application.repository

    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModelFactory(application, characterId, repository)
    )

    val character by viewModel.character.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    character?.let { char ->
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (char.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (char.isFavorite) "Desfavoritar" else "Favoritar",
                                tint = if (char.isFavorite) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        character?.let { char ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Imagem do Personagem
                Image(
                    painter = rememberImagePainter(data = char.image),
                    contentDescription = char.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )
                // Informações do Personagem
                Text(text = "Nome: ${char.name}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Espécie: ${char.species}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Status: ${char.status}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Gênero: ${char.gender}", style = MaterialTheme.typography.bodyLarge)

                // Outras Informações
                Text(text = "Origem: ${char.origin.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Localização: ${char.location.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Data de Criação: ${char.created}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))


            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
