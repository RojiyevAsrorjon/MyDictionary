package uz.gita.mydictionary.ui.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import uz.gita.mydictionary.R
import uz.gita.mydictionary.data.database.AppDatabase
import uz.gita.mydictionary.databinding.PageAllNewWordBinding
import uz.gita.mydictionary.ui.adapters.RecyclerAdapter
import uz.gita.mydictionary.ui.dialogs.DetailedDialog
import java.util.*

class PageSearch : Fragment(R.layout.page_all_new_word) {
    private var updatePageSelected: (() -> Unit)? = null

    private var _pageAllNewWordBinding: PageAllNewWordBinding? = null
    private val pageAllNewWordBinding get() = _pageAllNewWordBinding!!
    private val database = AppDatabase.getAppDatabase()
    private var querySt = ""
    private lateinit var handler: Handler
    private val adapter by lazy { RecyclerAdapter(database.getAllDictionaryData(querySt), querySt) }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _pageAllNewWordBinding = PageAllNewWordBinding.bind(view)
        val recycler = pageAllNewWordBinding.recyclerView
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
        loadImage(database.getAllDictionaryData(querySt))
        val speechImage = pageAllNewWordBinding.searchVoiceBtn
        speechImage.setOnClickListener {
            speechImage.setOnClickListener {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

                intent.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt)
                )

                try {
                    activityResultLauncher.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(),
                        "Sorry! Your device does\\`t support speech input",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult? ->
                if (result!!.resultCode == Activity.RESULT_OK && result!!.data != null) {
                    val speechText =
                        result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<*>
                    pageAllNewWordBinding.searchView.setQuery(speechText[0] as CharSequence?, false)
                }
            }
        adapter.setFavouriteListener {
            database.update(it)
            adapter.cursor = database.getAllDictionaryData(querySt)
            adapter.notifyDataSetChanged()
            updatePageSelected?.invoke()
        }
        handler = Handler(Looper.getMainLooper())
        pageAllNewWordBinding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                handler.removeCallbacksAndMessages(null)
                query?.let {
                    querySt = it.trim()
                    adapter.cursor = database.getAllDictionaryData(querySt)
                    adapter.query = querySt
                    adapter.notifyDataSetChanged()
                    pageAllNewWordBinding.searchView.setQuery(querySt, false)
                }
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length != 0) {
                    pageAllNewWordBinding.searchVoiceBtn.visibility = View.GONE
                } else pageAllNewWordBinding.searchVoiceBtn.visibility = View.VISIBLE
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    newText?.let {
                        querySt = it.trim()
                        adapter.cursor = database.getAllDictionaryData(querySt)
                        loadImage(database.getAllDictionaryData(querySt))
                        adapter.query = querySt
                        adapter.notifyDataSetChanged()
                        pageAllNewWordBinding.searchView.setQuery(querySt, false)
                    }
                }, 500)
                return true
            }
        })
        adapter.setListener {
            val dialog = DetailedDialog()
            val bundle = Bundle()
            bundle.putSerializable("DETAILED_DATA", it)
            dialog.arguments = bundle
            dialog.setStarListener {
                database.update(it)
                adapter.notifyDataSetChanged()
                adapter.cursor = database.getAllDictionaryData(querySt)
                updatePageSelected?.invoke()
            }
            activity?.supportFragmentManager?.let { it1 -> dialog.show(it1, "DETAILED_DIALOG") }
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun loadData() {
        adapter.cursor = database.getAllDictionaryData("")
        adapter.notifyDataSetChanged()
    }

    private fun loadImage(cursor: Cursor) {
        if (cursor.count == 0) {
            pageAllNewWordBinding.searchNo.visibility = View.VISIBLE
        } else pageAllNewWordBinding.searchNo.visibility = View.GONE
    }

    fun setUpdatePageSelectedListener(f: () -> Unit) {
        updatePageSelected = f
    }

}