package com.example.music.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SongsViewModel : ViewModel() {

    private val _songs = mutableStateOf<List<SongModel>>(emptyList())
    val songs: MutableState<List<SongModel>> = _songs

    // State for storing all genres fetched from Firestore
    private val _genres = mutableStateOf<List<GenreModel>>(emptyList())
    val genres: MutableState<List<GenreModel>> = _genres

    // State for the currently playing song
    private val _currentSong = mutableStateOf<SongModel?>(null)
    val currentSong: MutableState<SongModel?> = _currentSong

    // State for storing the latest mood fetched from Firestore
    private val _latestMood = mutableStateOf<String?>(null)
    val latestMood: State<String?> = _latestMood

    // State for storing the mapping of moods to genre IDs
    private val _moodGenreMap = mutableStateOf<Map<String, List<String>>>(emptyMap())

    init {
        fetchGenres()
    }
    // Fetch all genres from Firestore
    fun fetchGenres() {
        viewModelScope.launch {
            try {
                val genreList = FirebaseFirestore.getInstance().collection("genre").get().await()
                    .documents.mapNotNull { it.toObject(GenreModel::class.java)?.copy(id = it.id) }
                _genres.value = genreList
            } catch (e: Exception) {
            }
        }
    }

    // Fetch songs for a specific genre
    fun fetchSongsForGenre(genreId: String) {
        viewModelScope.launch {
            try {
                val songList = FirebaseFirestore.getInstance().collection("songs")
                    .whereEqualTo("genre", genreId).get().await()
                    .documents.mapNotNull { it.toObject(SongModel::class.java)?.copy(id = it.id) }
                _songs.value = songList
            } catch (e: Exception) {
            }
        }
    }

    // Function to set the current song
    fun setCurrentSong(song: SongModel) {
        _currentSong.value = song
        Log.d("SongsViewModel", "Current song set: ${song.title}")
    }

//    fun fetchMoodForUser(username: String) {
//        viewModelScope.launch {
//            val userDocRef = FirebaseFirestore.getInstance().collection("Users").document(username)
//            try {
//                val userData = userDocRef.get().await()
//                val mood = userData.getString("Mood")
//                _latestMood.value = mood ?: "No mood detected"
//            } catch (e: Exception) {
//                _latestMood.value = null
//                Log.e("ViewModel", "Error fetching mood for user $username", e)
//            }
//        }
//    }


//    Update the map after fetchGenre is called
//    fun createMoodToGenreIdMap() {
//        val moodToGenreNameMap = mapOf(
//            "happy" to "Classical",
//            "sad" to "Hip Hop",
//            "fear" to "Country",
//            "angry" to "Electronic"
//        )
//        val newMap = moodToGenreNameMap.mapValues { entry ->
//            genres.value.filter { it.name == entry.value }.map { it.id }
//        }
//        _moodGenreMap.value = newMap
//    }


//    fun setCurrentGenre(genreId: String) {
//        viewModelScope.launch {
//            // Find the genre by ID and update the selectedGenre state
//            val genre = _genres.value.find { it.id == genreId }
//            _selectedGenre.value = genre
//        }
//    }
}


// define view model for navigation within music screens
class MusicNavViewModel : ViewModel() {
    var genreId: String = ""
    var genreName: String = ""
    var genreCoverUrl: String = ""
    var songCoverUrl: String = ""
    lateinit var viewModel: SongsViewModel
    var songUrl: String = ""
    var songTitle: String = ""
    var songArtist: String = ""
}