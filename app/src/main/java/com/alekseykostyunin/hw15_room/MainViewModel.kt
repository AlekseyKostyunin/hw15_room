package com.alekseykostyunin.hw15_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class MainViewModel(private val wordDao: WordDao) : ViewModel() {

    private val _state = MutableStateFlow(State.START)
    val state = _state.asStateFlow()

    private val _resultSearch = MutableStateFlow("")
    val resultSearch  = _resultSearch.asStateFlow()

    val getAllWords = this.wordDao.getAllWords().shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        replay = 1
    )

    fun onAdd(newWord: String) {
        viewModelScope.launch {
            when (wordDao.getSearchWord(newWord)) {
                null -> wordDao.addNewWord(Word(newWord, 1))
                else -> wordDao.update(newWord)
            }
        }
    }

    fun onSearchWord(word: String) {
        viewModelScope.launch {
            when (wordDao.getSearchWord(word)) {
                null -> _resultSearch.value = "Слово \"$word\" не найдено!"
                else -> _resultSearch.value =
                    "Повторений слова \"$word\" - ${wordDao.getSearchWord(word)!!.count}"
            }
        }
    }

    fun changeTextAddWord(text: CharSequence?) {
        if (changeTest(text)) _state.value = State.SUCCESS_ADD_WORD
        else _state.value = State.ERROR_ADD_WORD
    }

    fun changeTextSearchWord(text: CharSequence?) {
        if (changeTest(text)) _state.value = State.SUCCESS_SEARCH_WORD
        else _state.value = State.ERROR_SEARCH_WORD
    }

    private fun changeTest(text: CharSequence?) : Boolean {
        return !text.isNullOrEmpty() && text.matches(Regex("""^[А-Яа-яA-Za-z\-]{3,}${'$'}"""))
    }

    fun onDelete() {
        viewModelScope.launch {
            wordDao.deleteAll()
        }
    }

}