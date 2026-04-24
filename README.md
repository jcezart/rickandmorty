<div align="center">

# 🛸 Rick and Morty App

**A native Android app to explore Rick and Morty characters, built with Kotlin and Jetpack Compose**

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-FF6F00?style=for-the-badge)
![Clean Architecture](https://img.shields.io/badge/Clean_Architecture-✓-blueviolet?style=for-the-badge)
![Retrofit](https://img.shields.io/badge/Networking-Retrofit_+_OkHttp3-48B983?style=for-the-badge)

</div>

---

## 📱 Screenshots

<img src="https://github.com/jcezart/rickandmorty/blob/master/telainicial.png" width=260/> <img src="https://github.com/jcezart/rickandmorty/blob/master/filtroespecieaplicado.png" width=260/> <img src="https://github.com/jcezart/rickandmorty/blob/master/filtroespecieaplicado.png" width=260/>

---

## 📖 About

This app was built as part of the **Devspace Android Developer Certification** challenge. The goal was to consume the public [Rick and Morty REST API](https://rickandmortyapi.com) and build a fully functional character browser — with search, filtering, a detail screen, and a favorites system — using modern Android development tools and architecture best practices.

---

## ✨ Features

- 🔍 **Character Search** — Search characters by name in real time
- 🧬 **Species Filter** — Filter the character list by species type
- 🧑‍🚀 **Character Detail Screen** — Tap any character to see their full info
- ⭐ **Favorites Screen** — Save and manage your favorite characters locally
- 🖼️ **Async Image Loading** — Smooth remote image rendering with `AsyncImage` (Coil)
- 🔁 **REST API Integration** — Live data fetched from the Rick and Morty API via Retrofit

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin (100%) |
| UI Framework | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| Navigation | NavHostController (Navigation Compose) |
| Networking | Retrofit + OkHttp3 |
| Image Loading | Coil (AsyncImage) |
| API | [Rick and Morty API](https://rickandmortyapi.com) |
| Build System | Gradle (Kotlin DSL) |

---

## 🌐 API

This app consumes the **[Rick and Morty REST API](https://rickandmortyapi.com/documentation)** — a free, public API with paginated character data.

**Main endpoint used:**
```
GET https://rickandmortyapi.com/api/character
GET https://rickandmortyapi.com/api/character?name={query}
GET https://rickandmortyapi.com/api/character?species={species}
GET https://rickandmortyapi.com/api/character/{id}
```

**Response model (Kotlin data class):**
```kotlin
data class CharacterResponse(
    val results: List<Character>
)

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val origin: Origin,
    val location: Location
)
```

---

## 🏗️ Project Structure

```
rickandmorty/
├── data/
│   ├── model/
│   │   └── Character.kt           # API response data classes
│   ├── remote/
│   │   ├── RickAndMortyApi.kt     # Retrofit interface
│   │   └── RetrofitInstance.kt    # Retrofit + OkHttp3 setup
│   └── repository/
│       └── CharacterRepository.kt # Data layer abstraction
├── domain/
│   └── usecase/
│       └── GetCharactersUseCase.kt
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt          # Character list + search + filter
│   │   ├── DetailScreen.kt        # Character detail view
│   │   └── FavoritesScreen.kt     # Favorites list
│   ├── components/
│   │   └── CharacterCard.kt       # Reusable card component
│   ├── viewmodel/
│   │   └── CharacterViewModel.kt  # UI state & business logic
│   └── navigation/
│       └── NavGraph.kt            # NavHostController routes
└── MainActivity.kt
```

---

## 🔍 Key Implementation Details

### MVVM + Clean Architecture

The app separates concerns into three layers: `data` (API + repository), `domain` (use cases), and `ui` (ViewModel + Compose screens). The ViewModel exposes state via `StateFlow` and the UI reacts declaratively.

```kotlin
class CharacterViewModel(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun searchCharacters(name: String) {
        _searchQuery.value = name
        viewModelScope.launch {
            _characters.value = getCharactersUseCase(name)
        }
    }

    fun filterBySpecies(species: String) {
        viewModelScope.launch {
            _characters.value = getCharactersUseCase(species = species)
        }
    }
}
```

### Navigation with NavHostController

Each screen is registered as a composable route. The character `id` is passed as a navigation argument to the detail screen.

```kotlin
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("detail/{characterId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("characterId")
            DetailScreen(navController, characterId = id)
        }
        composable("favorites") {
            FavoritesScreen(navController)
        }
    }
}
```

### Async Image Loading with Coil

Character images are loaded from remote URLs using Coil's `AsyncImage` composable, which handles caching and placeholder states natively.

```kotlin
AsyncImage(
    model = character.image,
    contentDescription = character.name,
    modifier = Modifier
        .size(100.dp)
        .clip(CircleShape),
    contentScale = ContentScale.Crop
)
```

### Lazy List with Compose Components

The character grid uses `LazyColumn` with `Row` and `Spacer` to build a responsive, performant list — no RecyclerView or adapter boilerplate.

```kotlin
LazyColumn {
    items(characters) { character ->
        CharacterCard(
            character = character,
            onClick = { navController.navigate("detail/${character.id}") }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android device or emulator running API 26+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/jcezart/rickandmorty.git
```

2. **Open in Android Studio**

Open the cloned folder in Android Studio and wait for Gradle sync to complete.

3. **Run the app**

Select an emulator or connected device and press `▶ Run`.

> ✅ No API key required — the Rick and Morty API is fully public and open.

---

## 📚 Concepts Practiced

- Declarative UI with **Jetpack Compose** (Column, Row, LazyColumn, Modifier, Spacer)
- **MVVM + Clean Architecture** with use cases separating business logic
- **REST API** consumption with `Retrofit` + `OkHttp3`
- In-app navigation with **NavHostController** and typed route arguments
- Reactive state management with `StateFlow` and `viewModelScope`
- Remote image loading with **Coil** (`AsyncImage`)
- Composable **previews** with `@Preview` during development
- Real-time **search and filter** logic driven by ViewModel state
- **Favorites** management with local state persistence

---

## 📄 License

```
The MIT License (MIT)

Copyright (c) 2024 Julio Cezar Grassi Teixeira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

<div align="center">
  Made with ❤️ and Kotlin · <a href="https://github.com/jcezart">@jcezart</a>
</div>
