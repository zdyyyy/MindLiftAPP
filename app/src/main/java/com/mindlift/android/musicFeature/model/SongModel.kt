package com.example.music.model

data class SongModel(
    val title : String,
    val id : String,
    val artist : String,
    val genre : String,
    val url : String,
    val coverUrl : String,
){
    constructor() : this("","","","","","")
}
