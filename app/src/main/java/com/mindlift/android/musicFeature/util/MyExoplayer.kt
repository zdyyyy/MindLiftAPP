package com.example.music.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.music.model.SongModel

object MyExoplayer {
    private var exoPlayer: ExoPlayer? = null
    var currentSong by mutableStateOf<SongModel?>(null)
        private set

    fun getInstance(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().also { player ->
                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                    }
                })
            }
        }
        return exoPlayer!!
    }

    fun playSong(context: Context, song: SongModel) {
        if (currentSong != song) {
            // Play new song
            currentSong = song
            val playerInstance = getInstance(context)
            song.url?.let { url ->
                val mediaItem = MediaItem.fromUri(url)
                playerInstance.setMediaItem(mediaItem)
                playerInstance.prepare()
                playerInstance.play()
            }
        }
    }
}


//object MyExoplayer {
//    private var exoPlayer : ExoPlayer? = null
//    private var currentSong : SongModel? = null
//
//    fun getCurrentSong() : SongModel?{
//        return currentSong
//    }
//
//    fun getInstance() : ExoPlayer?{
//        return exoPlayer
//    }
//    fun playSong(context: Context, song: SongModel){
//        if(exoPlayer == null)
//            exoPlayer = ExoPlayer.Builder(context).build()
//
//        if(currentSong != song){
//            // Play new song
//            currentSong = song
//            currentSong?.url?.apply{
//                val media = MediaItem.fromUri(this)
//                exoPlayer?.setMediaItem(media)
//                exoPlayer?.prepare()
//                exoPlayer?.play()
//            }
//        }
//    }
//}