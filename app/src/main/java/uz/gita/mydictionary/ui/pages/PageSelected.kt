package uz.gita.mydictionary.ui.pages

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.gita.mydictionary.R
import uz.gita.mydictionary.data.database.AppDatabase
import uz.gita.mydictionary.databinding.PageSelectedWordsBinding
import uz.gita.mydictionary.ui.adapters.RecyclerAdapter
import uz.gita.mydictionary.ui.dialogs.DetailedDialog

class PageSelected : Fragment(R.layout.page_selected_words) {
    private val database = AppDatabase.getAppDatabase()
    private var updatePageSearchListener: (() -> Unit)? = null
    private lateinit var imageEmpty : ImageView
    private val adapter by lazy { RecyclerAdapter(database.getAllFavouriteData(), "") }
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageEmpty = view.findViewById(R.id.imageSelected)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerView)
        loadImage(database.getAllFavouriteData())
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter.setFavouriteListener {
            database.update(it)
            adapter.cursor = database.getAllFavouriteData()
            adapter.notifyDataSetChanged()
            updatePageSearchListener?.invoke()
            loadImage(database.getAllFavouriteData())
        }
        adapter.setListener {
            val dialog = DetailedDialog()
            val bundle = Bundle()
            bundle.putSerializable("DETAILED_DATA", it)
            dialog.arguments = bundle
            dialog.setStarListener {
                database.update(it)
                adapter.notifyDataSetChanged()
                adapter.cursor = database.getAllFavouriteData()
                updatePageSearchListener?.invoke()
            }
            activity?.supportFragmentManager?.let { it1 -> dialog.show(it1, "DETAILED_DIALOG") }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadData(){
        adapter.cursor = database.getAllFavouriteData()
        loadImage(database.getAllFavouriteData())
        adapter.notifyDataSetChanged()
    }
    private fun loadImage(cursor: Cursor) {
        if (cursor.count == 0) {
            view?.findViewById<ImageView>(R.id.imageSelected)?.visibility  = View.VISIBLE
        }
        else view?.findViewById<ImageView>(R.id.imageSelected)?.visibility  = View.GONE
    }
    fun setUpdatePageSearchListener(f: () -> Unit) {
        updatePageSearchListener = f
    }
}