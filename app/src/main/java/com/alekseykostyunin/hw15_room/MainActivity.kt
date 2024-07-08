package com.alekseykostyunin.hw15_room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alekseykostyunin.hw15_room.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val wordDao = (applicationContext as App).db.wordDao()
                return MainViewModel(wordDao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.getAllWords.collect { list ->
                var text = ""
                list.sortedByDescending { it.count }
                    .take(5)
                    .forEachIndexed { index, word ->
                        text += "${index + 1}. \"${word.word}\", " +
                                    "количество повторений: ${word.count}\n"
                    }
                binding.resultRatingWords.text = text
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    State.ERROR_ADD_WORD -> {
                        binding.button.isEnabled = false
                        binding.addWordLayout.isErrorEnabled = true
                        binding.addWordLayout.error = resources.getString(R.string.error_text)
                    }

                    State.ERROR_SEARCH_WORD -> {
                        binding.searchBtn.isEnabled = false
                        binding.searchWordLayout.isErrorEnabled = true
                        binding.searchWordLayout.error = resources.getString(R.string.error_text)
                    }

                    State.SUCCESS_ADD_WORD -> {
                        binding.button.isEnabled = true
                        binding.addWordLayout.error = ""
                    }

                    State.SUCCESS_SEARCH_WORD -> {
                        binding.searchBtn.isEnabled = true
                        binding.searchWordLayout.error = ""
                    }

                    State.START -> {
                        binding.button.isEnabled = false
                        binding.searchBtn.isEnabled = false
                    }
                }
            }
        }

        binding.button.setOnClickListener {
            viewModel.onAdd(binding.addWord.text.toString())
        }

        binding.clearBtn.setOnClickListener {
            viewModel.onDelete()
        }

        binding.addWord.doOnTextChanged { text, _, _, _ ->
            viewModel.changeTextAddWord(text)
        }

        binding.searchWord.doOnTextChanged { text, _, _, _ ->
            viewModel.changeTextSearchWord(text)
        }

        binding.searchBtn.setOnClickListener  {
            viewModel.onSearchWord(binding.searchWord.text.toString())
        }

        lifecycleScope.launch  {
            viewModel.resultSearch.collect  {
                binding.resultSearchWords.text = it
            }
        }


    }


}