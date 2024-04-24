package com.example.music.model

data class GenreModel(
    val name: String,
    val coverUrl: String,
    val songs : List<String>,
    val id: String
){

    constructor() : this("", "", listOf(),"")
}