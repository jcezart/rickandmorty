package com.devspace.rickandmorty.data.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromOrigin(value: Origin): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOrigin(value: String): Origin {
        return Gson().fromJson(value, Origin::class.java)
    }

    @TypeConverter
    fun fromLocation(value: Location): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLocation(value: String): Location {
        return Gson().fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun fromEpisodeList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toEpisodeList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
