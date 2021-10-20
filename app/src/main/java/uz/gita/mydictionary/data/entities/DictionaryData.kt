package uz.gita.mydictionary.data.entities

import java.io.Serializable

data class DictionaryData(
    val id: Int,
    val word: String,
    val wordtype: String,
    val definition: String,
    var isRemember: Int
) : Serializable
