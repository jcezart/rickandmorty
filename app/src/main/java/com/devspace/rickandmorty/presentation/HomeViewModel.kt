package com.devspace.rickandmorty.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devspace.rickandmorty.MyApplication
import com.devspace.rickandmorty.core.data.local.CharacterRepository
import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    // Estados de busca e filtro
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _selectedSpecies = MutableStateFlow<String?>(null)
    val selectedSpecies: StateFlow<String?> get() = _selectedSpecies

    // Lista de espécies para o filtro
    private val _speciesList = MutableStateFlow<List<String>>(emptyList())
    val speciesList: StateFlow<List<String>> get() = _speciesList

    // Mensagem de erro
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Combinação dos estados de busca e filtro
    val characters: StateFlow<List<CharacterEntity>> = combine(
        _searchQuery,
        _selectedSpecies,
        repository.getAllCharacters()
    ) { query, species, characterList ->
        Triple(query, species, characterList)
    }
        .debounce(300) // Evita buscas excessivas enquanto o usuário digita
        .map { (query, species, characterList) ->
            val filteredList = when {
                query.isNotBlank() && !species.isNullOrEmpty() ->
                    characterList.filter {
                        it.name.contains(query, ignoreCase = true) &&
                                it.species.equals(species, ignoreCase = true)
                    }
                query.isNotBlank() ->
                    characterList.filter { it.name.contains(query, ignoreCase = true) }
                !species.isNullOrEmpty() ->
                    characterList.filter { it.species.equals(species, ignoreCase = true) }
                else -> characterList
            }
            _errorMessage.value = null
            filteredList
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        // Inicializa a lista de espécies
        viewModelScope.launch {
            repository.getAllCharacters().collect { charactersList ->
                _speciesList.value = charactersList.map { it.species }.distinct().sorted()
            }
        }
    }

    // Atualiza a query de busca
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Atualiza o filtro de espécie
    fun filterBySpecies(species: String?) {
        _selectedSpecies.value = species
    }
}

// Fábrica para o HomeViewModel
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
