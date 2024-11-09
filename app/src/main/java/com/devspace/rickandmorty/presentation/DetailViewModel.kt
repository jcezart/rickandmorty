package com.devspace.rickandmorty.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devspace.rickandmorty.core.data.local.CharacterRepository
import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    application: Application,
    private val characterId: Int,
    private val repository: CharacterRepository
) : AndroidViewModel(application) {

    private val _character = MutableStateFlow<CharacterEntity?>(null)
    val character: StateFlow<CharacterEntity?> get() = _character

    init {
        fetchCharacter()
    }

    private fun fetchCharacter() {
        viewModelScope.launch {
            repository.getCharacterByIdFlow(characterId).collect { char ->
                _character.value = char
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _character.value?.let { character ->
                repository.toggleFavoriteStatus(character)
            }
        }
    }
}

class DetailViewModelFactory(
    private val application: Application,
    private val characterId: Int,
    private val repository: CharacterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(application, characterId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
