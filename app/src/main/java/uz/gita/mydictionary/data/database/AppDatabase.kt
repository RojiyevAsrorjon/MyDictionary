package uz.gita.mydictionary.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import uz.gita.dictionaryapp.App.App
import uz.gita.mydictionary.data.entities.DictionaryData

class AppDatabase private constructor(context: Context) : DBHelper(context, "Dictionary.db", 1) {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance : AppDatabase

        fun getAppDatabase() : AppDatabase {
            if (!Companion::instance.isInitialized) {
                instance = AppDatabase(App.instance)
            }
            return instance
        }
    }

    fun getAllDictionaryData(query: String): Cursor {
        val query = "SELECT * FROM entries WHERE entries.word LIKE '%$query%'"
        return instance.database().rawQuery(query, null)
    }

    fun getAllDictionary(): Cursor {
        val query = "SELECT * FROM entries"
        return instance.database().rawQuery(query, null)
    }

    fun update(data: DictionaryData) {
        val cv = ContentValues()
        if (data.isRemember == 0) cv.put("isRemember", 1)
        else cv.put("isRemember", 0)
        instance.database().update("entries", cv, "entries.id = ${data.id}", null)
    }

    fun getAllFavouriteData(): Cursor {
        val query = "SELECT * FROM entries WHERE entries.isRemember = 1"
        return instance.database().rawQuery(query, null)
    }

}