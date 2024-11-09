package com.devspace.rickandmorty.data.networking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.devspace.rickandmorty.data.models.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters")
    fun getAllFlow(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterByIdFlow(id: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE isFavorite = 1")
    suspend fun getAllFavorites(): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE isFavorite = 1")
    fun getAllFavoritesFlow(): Flow<List<CharacterEntity>>

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :name || '%'")
    suspend fun searchCharactersByName(name: String): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE species = :species")
    suspend fun getCharactersBySpecies(species: String): List<CharacterEntity>
}
