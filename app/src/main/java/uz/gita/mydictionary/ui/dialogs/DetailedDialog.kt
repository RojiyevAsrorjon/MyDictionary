package uz.gita.mydictionary.ui.dialogs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import uz.gita.mydictionary.data.entities.DictionaryData
import uz.gita.mydictionary.R
import uz.gita.mydictionary.databinding.DialogFragmentBinding
import java.util.*

class DetailedDialog : DialogFragment(R.layout.dialog_fragment), TextToSpeech.OnInitListener{
    private var _dialogFragmentBinding: DialogFragmentBinding? = null
    private val dialogFragmentBinding get() = _dialogFragmentBinding!!
    private var starListener: (() -> Unit)? = null
    private lateinit var textToSpeech: TextToSpeech
    private var isSelected = false
    override fun onResume() {
        super.onResume()
        val paragm : ViewGroup.LayoutParams = dialog!!.window!!.attributes
        paragm.width = ViewGroup.LayoutParams.MATCH_PARENT
        paragm.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = paragm as WindowManager.LayoutParams
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _dialogFragmentBinding = DialogFragmentBinding.bind(view)
        val newWord = dialogFragmentBinding.detailedNewWordText
        val newWordType = dialogFragmentBinding.detailedWordTypeText
        val newWordDesc = dialogFragmentBinding.detailedDescription
        val volumeButton = dialogFragmentBinding.volumeButton
        val starButton = dialogFragmentBinding.starButton
        val closeButton = dialogFragmentBinding.closeButton
        textToSpeech = TextToSpeech(requireContext(), this)
        var data: DictionaryData? = null
        arguments?.let {
            data = it.getSerializable("DETAILED_DATA") as DictionaryData
            newWord.text = data?.word
            newWordDesc.text = data?.definition
            newWordType.text = data?.wordtype
            isSelected = data?.isRemember == 1
            if (isSelected) {
                starButton.setBackgroundResource(R.drawable.ic_star_checked)
            }
        }
        closeButton.setOnClickListener {
            dismiss()
        }
        starButton.setOnClickListener {
            starListener?.invoke()
            isSelected = !isSelected
            if (isSelected) {
                starButton.setBackgroundResource(R.drawable.ic_star_checked)
            }
            else starButton.setBackgroundResource(R.drawable.ic_baseline_star_border_24)
        }

        volumeButton.setOnClickListener {
            data?.word?.let { it1 -> speakOut(it1) }
        }


    }
    private fun speakOut(word : String) {
        val textForSpeech = word
        textToSpeech.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _dialogFragmentBinding = null
    }

    fun setStarListener(f: () -> Unit) {
        starListener = f
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val res = textToSpeech.setLanguage(Locale.US)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(requireContext(), "Lang is not supported", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(requireContext(), "Failed to initialize", Toast.LENGTH_SHORT).show()
        }
    }
}