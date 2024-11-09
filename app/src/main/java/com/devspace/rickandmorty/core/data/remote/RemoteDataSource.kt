package com.devspace.rickandmorty.core.data.remote

import com.devspace.rickandmorty.data.models.CharacterEntity
import com.devspace.rickandmorty.data.models.CharacterResponse

interface RemoteDataSource {
    suspend fun fetchAllCharacters(): CharacterResponse
    suspend fun fetchCharactersBySpecies(species: String, page: Int = 1): CharacterResponse
    suspend fun searchCharactersByName(name: String, page: Int = 1): CharacterResponse
    suspend fun fetchCharacterById(id: Int): CharacterEntity
}

