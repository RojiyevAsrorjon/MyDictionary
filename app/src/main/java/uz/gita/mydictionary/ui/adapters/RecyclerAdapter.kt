package uz.gita.mydictionary.ui.adapters

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.gita.mydictionary.R
import uz.gita.mydictionary.data.entities.DictionaryData

class RecyclerAdapter(var cursor: Cursor, var query: String) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var favouriteListener: ((DictionaryData) -> Unit)? = null
    private var listener: ((DictionaryData) -> Unit)? = null

    

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val newWord = view.findViewById<TextView>(R.id.idTextNewWord)
        private val newWordType = view.findViewById<TextView>(R.id.idTextTextType)
        private val isFavourite = view.findViewById<ImageView>(R.id.idFavouriteButton)

        init {
            itemView.setOnClickListener {
                getDataByPosFromCursor(absoluteAdapterPosition)?.let { it1 -> listener?.invoke(it1) }
            }
            isFavourite.setOnClickListener {
                Log.d("TTT", "$absoluteAdapterPosition")
                getDataByPosFromCursor(absoluteAdapterPosition)?.let { it1 -> favouriteListener?.invoke(it1) }
            }
        }

        fun bind(data: DictionaryData) {
            val spanSt = SpannableString(data.word)
            val foreGroundColor = ForegroundColorSpan(Color.RED)
            val startIndex = data.word.indexOf(query, 0, true)
            val endIndex = startIndex + query.length
            spanSt.setSpan(
                foreGroundColor,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            newWord.text = spanSt
            newWordType.text = data.wordtype
            isFavourite.setImageResource(
                if (data.isRemember == 1) R.drawable.ic_star_checked
                else R.drawable.ic_baseline_star_border_24
            )


        }
    }

    @SuppressLint("Range")
    private fun getDataByPosFromCursor(pos: Int): DictionaryData? {
        if (pos < 0) return null
        cursor.moveToPosition(pos)
        return DictionaryData(
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getString(cursor.getColumnIndex("word")),
            cursor.getString(cursor.getColumnIndex("wordtype")),
            cursor.getString(cursor.getColumnIndex("definition")),
            cursor.getInt(cursor.getColumnIndex("isRemember"))
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_word, parent, false)
        return ViewHolder(view)
    }

    fun setFavouriteListener(f: (DictionaryData) -> Unit) {
        favouriteListener = f
    }

    fun setListener(f: (DictionaryData) -> Unit) {
        listener = f
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getDataByPosFromCursor(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = cursor.count

}