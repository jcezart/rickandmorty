package com.devspace.rickandmorty.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devspace.rickandmorty.MyApplication
import com.devspace.rickandmorty.core.data.local.CharacterRepository
import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> get() = _characters

    private val _speciesList = MutableStateFlow<List<String>>(emptyList())
    val speciesList: StateFlow<List<String>> get() = _speciesList

    private val _selectedSpecies = MutableStateFlow<String?>(null)
    val selectedSpecies: StateFlow<String?> get() = _selectedSpecies

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    init {
        // Carrega os personagens
        fetchCharacters()
    }

    private fun fetchCharacters() {
        viewModelScope.launch {
            repository.getAllCharacters().collectLatest { charactersList ->
                _characters.value = charactersList
                // Extrai espécies únicas para o filtro
                val species = charactersList.map { it.species }.distinct().sorted()
                _speciesList.value = species
            }
        }
    }

    fun filterBySpecies(species: String?) {
        viewModelScope.launch {
            try {
                if (species.isNullOrEmpty()) {
                    // Mostra todos os personagens
                    repository.getAllCharacters().collectLatest { list ->
                        _characters.value = list
                    }
                } else {
                    // Mostra personagens filtrados por espécie
                    val filteredList = repository.getCharactersBySpecies(species)
                    _characters.value = filteredList
                }
                _selectedSpecies.value = species
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao filtrar personagens. Tente novamente."
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch(query, _selectedSpecies.value)
    }

    private fun performSearch(query: String, species: String?) {
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    // Se a busca está vazia, mostrar todos ou aplicar o filtro de espécie
                    if (species.isNullOrEmpty()) {
                        repository.getAllCharacters().collectLatest { list ->
                            _characters.value = list
                        }
                    } else {
                        val filteredList = repository.getCharactersBySpecies(species)
                        _characters.value = filteredList
                    }
                } else {
                    // Executa a busca pelo nome
                    val searchResults = repository.searchCharactersByName(query)
                    // Opcional: se desejar combinar com o filtro de espécie
                    val finalResults = if (!species.isNullOrEmpty()) {
                        searchResults.filter { it.species.equals(species, ignoreCase = true) }
                    } else {
                        searchResults
                    }
                    _characters.value = finalResults
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao buscar personagens. Tente novamente."
            }
        }
    }
}

class HomeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Obtém o CharacterRepository da instância de MyApplication
            val repository = (application as MyApplication).repository
            return HomeViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
