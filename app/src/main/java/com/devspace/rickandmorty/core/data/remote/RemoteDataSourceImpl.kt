package com.devspace.rickandmorty.core.data.remote

import com.devspace.rickandmorty.data.models.CharacterEntity
import com.devspace.rickandmorty.data.models.CharacterResponse
import com.devspace.rickandmorty.data.networking.ApiService

class RemoteDataSourceImpl(private val apiService: ApiService) : RemoteDataSource {
    override suspend fun fetchAllCharacters(): CharacterResponse = apiService.getAllCharactersByPage(1)

    override suspend fun fetchCharactersBySpecies(species: String, page: Int): CharacterResponse =
        apiService.getCharactersBySpecies(species, page)

    override suspend fun searchCharactersByName(name: String, page: Int): CharacterResponse =
        apiService.searchCharactersByName(name, page)

    override suspend fun fetchCharacterById(id: Int): CharacterEntity = apiService.getCharacterById(id)
}