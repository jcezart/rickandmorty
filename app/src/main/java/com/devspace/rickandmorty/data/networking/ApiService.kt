package com.devspace.rickandmorty.data.networking


import com.devspace.rickandmorty.data.models.CharacterEntity
import com.devspace.rickandmorty.data.models.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getAllCharactersByPage(@Query("page") page: Int): CharacterResponse

    @GET("character")
    suspend fun getCharactersBySpecies(
        @Query("species") species: String,
        @Query("page") page: Int = 1
    ): CharacterResponse

    @GET("character")
    suspend fun searchCharactersByName(
        @Query("name") name: String,
        @Query("page") page: Int = 1
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterEntity
}