// FavoritesScreen.kt
package com.devspace.rickandmorty.presentation

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devspace.rickandmorty.data.models.CharacterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, application: Application) {
    // Inicializar o ViewModel
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(application)
    )

    // Coleta os estados do ViewModel
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            // Exibir Mensagem de Nenhum Favorito
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Nenhum personagem favoritado.")
            }
        } else {
            // Exibir Lista de Favoritos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(favorites) { character ->
                    FavoriteCharacterItem(
                        character = character,
                        onClick = {
                            // Navega para a tela de detalhe passando o ID do personagem
                            navController.navigate("detail/${character.id}")
                        },
                        onUnfavoriteClick = { characterToUnfavorite ->
                            viewModel.toggleFavorite(characterToUnfavorite)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteCharacterItem(
    character: CharacterEntity,
    onClick: () -> Unit,
    onUnfavoriteClick: (CharacterEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Imagem do Personagem
            Image(
                painter = rememberImagePainter(data = character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            // Informações do Personagem
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // Ícone de Unfavorite Clicável
            Icon(
                imageVector = Icons.Outlined.StarBorder,
                contentDescription = "Desfavoritar",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onUnfavoriteClick(character) } // Tornar o ícone clicável
            )
        }
    }
}
