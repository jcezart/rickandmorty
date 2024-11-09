package com.devspace.rickandmorty.core.data.local

import com.devspace.rickandmorty.data.models.CharacterEntity
import com.devspace.rickandmorty.data.networking.CharacterDao
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val characterDao: CharacterDao) : LocalDataSource {

    override fun getAllCharacters(): Flow<List<CharacterEntity>> = characterDao.getAllFlow()

    override suspend fun insertCharacters(characters: List<CharacterEntity>) {
        characterDao.insertAll(characters)
    }

    override suspend fun getCharacterById(id: Int): CharacterEntity? = characterDao.getCharacterById(id)

    override fun getCharacterByIdFlow(id: Int): Flow<CharacterEntity?> = characterDao.getCharacterByIdFlow(id)

    override suspend fun searchCharactersByName(name: String): List<CharacterEntity> = characterDao.searchCharactersByName(name)

    override suspend fun getCharactersBySpecies(species: String): List<CharacterEntity> = characterDao.getCharactersBySpecies(species)

    override suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean) = characterDao.updateFavoriteStatus(id, isFavorite)

    override suspend fun getAllFavorites(): List<CharacterEntity> = characterDao.getAllFavorites()

    override fun getAllFavoritesFlow(): Flow<List<CharacterEntity>> = characterDao.getAllFavoritesFlow()
}