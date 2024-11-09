// HomeScreen.kt
package com.devspace.rickandmorty.presentation

//import androidx.compose.ui.text.input.KeyboardOptions
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.devspace.rickandmorty.data.models.CharacterEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, application: Application) {

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(application)
    )

    // Coleta os estados do ViewModel
    val characters by viewModel.characters.collectAsState()
    val speciesList by viewModel.speciesList.collectAsState()
    val selectedSpecies by viewModel.selectedSpecies.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Estado local para a barra de busca
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personagens") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Favoritos",
                            tint = Color.Yellow
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔍 **Barra de Busca**
            SearchBar(
                query = textState,
                onQueryChange = {
                    textState = it
                    viewModel.updateSearchQuery(it.text)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🗂️ **Filtro por Espécie**
            SpeciesFilter(
                speciesList = speciesList,
                selectedSpecies = selectedSpecies,
                onSpeciesSelected = { species ->
                    viewModel.filterBySpecies(species)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ⚠️ **Mensagem de Erro (se houver)**
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 📜 **Lista de Personagens ou Indicador de Carregamento**
            if (characters.isEmpty() && searchQuery.isNotBlank() && errorMessage == null) {
                // Exibir Mensagem de Nenhum Resultado Encontrado
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Nenhum personagem encontrado.")
                }
            } else if (characters.isEmpty()) {
                // Exibir Indicador de Carregamento
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Exibir Lista de Personagens
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(characters) { character ->
                        CharacterItem(character = character, onClick = {
                            // Navega para a Tela de Detalhe passando o ID do Personagem
                            navController.navigate("detail/${character.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        label = { Text("Buscar Personagem") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Ícone de Busca")
        },
        trailingIcon = {
            if (query.text.isNotEmpty()) {
                IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Limpar Busca")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        )
    )
}

@Composable
fun SpeciesFilter(
    speciesList: List<String>,
    selectedSpecies: String?,
    onSpeciesSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedSpecies ?: "Filtrar por Espécie")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("Todas as Espécies") },
                onClick = {
                    onSpeciesSelected(null) // Remove o filtro
                    expanded = false
                }
            )
            speciesList.forEach { species ->
                DropdownMenuItem(
                    text = { Text(species) },
                    onClick = {
                        onSpeciesSelected(species)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CharacterItem(character: CharacterEntity, onClick: () -> Unit) {
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
        }
    }
}
