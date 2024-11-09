package com.devspace.rickandmorty.core.data.local

import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllCharacters(): Flow<List<CharacterEntity>>
    suspend fun insertCharacters(characters: List<CharacterEntity>)
    suspend fun getCharacterById(id: Int): CharacterEntity?
    fun getCharacterByIdFlow(id: Int): Flow<CharacterEntity?>
    suspend fun searchCharactersByName(name: String): List<CharacterEntity>
    suspend fun getCharactersBySpecies(species: String): List<CharacterEntity>
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
    suspend fun getAllFavorites(): List<CharacterEntity>
    fun getAllFavoritesFlow(): Flow<List<CharacterEntity>>
}