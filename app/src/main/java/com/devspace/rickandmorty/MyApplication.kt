package com.devspace.rickandmorty

import android.app.Application
import com.devspace.rickandmorty.core.data.local.AppDatabase
import com.devspace.rickandmorty.core.data.local.CharacterRepository
import com.devspace.rickandmorty.core.data.local.LocalDataSourceImpl
import com.devspace.rickandmorty.core.data.networking.RetrofitClient
import com.devspace.rickandmorty.core.data.remote.RemoteDataSourceImpl
import com.devspace.rickandmorty.data.networking.ApiService


class MyApplication : Application() {
    val repository: CharacterRepository by lazy {
        val db = AppDatabase.getDatabase(this)
        val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)
        val localDataSource = LocalDataSourceImpl(db.characterDao())
        val remoteDataSource = RemoteDataSourceImpl(apiService)
        CharacterRepository(localDataSource, remoteDataSource)
    }
}