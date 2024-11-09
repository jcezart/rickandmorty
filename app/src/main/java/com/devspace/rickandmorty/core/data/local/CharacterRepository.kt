package com.devspace.rickandmorty.core.data.local


import com.devspace.rickandmorty.core.data.remote.RemoteDataSource
import com.devspace.rickandmorty.data.models.CharacterEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CharacterRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    // Recupera todos os personagens, priorizando o local
    fun getAllCharacters(): Flow<List<CharacterEntity>> = flow {
        // Emite os dados locais primeiro
        localDataSource.getAllCharacters().collect { localCharacters ->
            emit(localCharacters)
        }
        // Tenta atualizar com dados remotos
        try {
            val remoteResponse = remoteDataSource.fetchAllCharacters()
            localDataSource.insertCharacters(remoteResponse.results)
        } catch (e: Exception) {
            // Handle exception (log, etc.)
        }
    }

    // Busca personagens por nome
    suspend fun searchCharactersByName(name: String): List<CharacterEntity> {
        return try {
            val remoteResponse = remoteDataSource.searchCharactersByName(name)
            localDataSource.insertCharacters(remoteResponse.results)
            remoteResponse.results
        } catch (e: Exception) {
            // Em caso de erro, retorna os dados locais
            localDataSource.searchCharactersByName(name)
        }
    }

    // Busca personagens por espécie
    suspend fun getCharactersBySpecies(species: String): List<CharacterEntity> {
        return try {
            val remoteResponse = remoteDataSource.fetchCharactersBySpecies(species)
            localDataSource.insertCharacters(remoteResponse.results)
            remoteResponse.results
        } catch (e: Exception) {
            // Em caso de erro, retorna os dados locais
            localDataSource.getCharactersBySpecies(species)
        }
    }

    // Recupera personagem por ID
    suspend fun getCharacterById(id: Int): CharacterEntity? {
        return try {
            val remoteCharacter = remoteDataSource.fetchCharacterById(id)
            localDataSource.insertCharacters(listOf(remoteCharacter))
            remoteCharacter
        } catch (e: Exception) {
            // Em caso de erro, retorna os dados locais
            localDataSource.getCharacterById(id)
        }
    }

    fun getCharacterByIdFlow(id: Int): Flow<CharacterEntity?> {
        return localDataSource.getCharacterByIdFlow(id)
    }

    // Favorita/Desfavorita um personagem
    suspend fun toggleFavoriteStatus(character: CharacterEntity) {
        val newStatus = !character.isFavorite
        localDataSource.updateFavoriteStatus(character.id, newStatus)
    }

    // Recupera todos os favoritos
    suspend fun getAllFavorites(): List<CharacterEntity> = localDataSource.getAllFavorites()

    // Recupera Flow de favoritos
    fun getAllFavoritesFlow(): Flow<List<CharacterEntity>> = localDataSource.getAllFavoritesFlow()
}
