package com.alekseykostyunin.hw15_room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<Word>>

    @Insert
    suspend fun addNewWord(word: Word)

    @Query("SELECT * FROM words WHERE word LIKE :searchWord")
    suspend fun getSearchWord(searchWord: String): Word?

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("UPDATE words SET count = count+1 WHERE word LIKE :newWord")
    suspend fun update(newWord: String)


}