package com.devspace.rickandmorty.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.devspace.rickandmorty.core.data.local.AppDatabase
import com.devspace.rickandmorty.core.data.local.CharacterRepository
import com.devspace.rickandmorty.core.data.local.LocalDataSourceImpl
import com.devspace.rickandmorty.core.data.networking.RetrofitClient
import com.devspace.rickandmorty.core.data.remote.RemoteDataSourceImpl
import com.devspace.rickandmorty.data.networking.ApiService
import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CharacterRepository

    private val _favorites = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val favorites: StateFlow<List<CharacterEntity>> get() = _favorites

    init {
        // Inicializa o repositório com data sources locais e remotos
        val db = AppDatabase.getDatabase(application)
        val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)
        val localDataSource = LocalDataSourceImpl(db.characterDao())
        val remoteDataSource = RemoteDataSourceImpl(apiService)
        repository = CharacterRepository(localDataSource, remoteDataSource)

        // Carrega os favoritos
        fetchFavorites()
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            repository.getAllFavoritesFlow().collectLatest { favList ->
                _favorites.value = favList
            }
        }
    }

    fun toggleFavorite(character: CharacterEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteStatus(character)
            //fetchFavorites() // Atualiza a lista de favoritos após a alteração
        }
    }
}

class FavoritesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
