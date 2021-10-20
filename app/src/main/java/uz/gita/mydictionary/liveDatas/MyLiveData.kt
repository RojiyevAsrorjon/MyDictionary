package uz.gita.mydictionary.liveDatas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.gita.mydictionary.data.database.AppDatabase

class MyLiveData : ViewModel() {
    private val _allWordLiveData = MutableLiveData<Unit>()
    val allWordLiveData get() = _allWordLiveData


    private val _selectedWordsLiveData = MutableLiveData<Unit>()
    val selectedWordsLiveData : LiveData<Unit> get() = _selectedWordsLiveData

    private var position = 0

    fun changePos(pos: Int) {
        if (pos == position) return

        if (pos == 0) _allWordLiveData.value = Unit
        else _selectedWordsLiveData.value = Unit
        position = pos
    }
}