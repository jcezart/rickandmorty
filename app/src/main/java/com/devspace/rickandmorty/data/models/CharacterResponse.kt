package com.devspace.rickandmorty.data.models


data class CharacterResponse(
    val info: Info,
    val results: List<CharacterEntity>
)

data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

