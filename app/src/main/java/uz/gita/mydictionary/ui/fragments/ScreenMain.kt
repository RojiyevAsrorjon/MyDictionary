package uz.gita.mydictionary.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import uz.gita.mydictionary.R
import uz.gita.mydictionary.databinding.ScreenMainBinding
import uz.gita.mydictionary.liveDatas.MyLiveData
import uz.gita.mydictionary.ui.adapters.PageAdapter
import uz.gita.mydictionary.ui.pages.PageSearch
import uz.gita.mydictionary.ui.pages.PageSelected

class ScreenMain : Fragment(R.layout.screen_main) {
    private var _screenMainBinding: ScreenMainBinding? = null
    private val screenMainBinding get() = _screenMainBinding!!

    private var _pageSearch : PageSearch? = PageSearch()
    private val pageSearch get() = _pageSearch!!

    private var _pageSelected : PageSelected? = PageSelected()
    private val pageSelected get() = _pageSelected!!

    private val viewModel by viewModels<MyLiveData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _screenMainBinding = ScreenMainBinding.bind(view)
        val adapter = PageAdapter(childFragmentManager, lifecycle, pageSearch, pageSelected)
        screenMainBinding.viewPager.adapter = adapter

        screenMainBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                viewModel.changePos(position)
            }
        })

        viewModel.allWordLiveData.observe(viewLifecycleOwner, pageAllObserver)
        viewModel.selectedWordsLiveData.observe(viewLifecycleOwner, pageSelectedObserver)
        screenMainBinding.bottomNavigationMain.setOnItemSelectedListener {
            if (it.itemId == R.id.allWords) {
                viewModel.changePos(0)
            }
            else viewModel.changePos(1)
            return@setOnItemSelectedListener true
        }

        pageSearch.setUpdatePageSelectedListener {
            pageSelected.loadData()
        }
        pageSelected.setUpdatePageSearchListener {
            pageSearch.loadData()
        }
    }

    private val pageAllObserver = Observer<Unit>{
        screenMainBinding.viewPager.currentItem = 0
        screenMainBinding.bottomNavigationMain.selectedItemId = R.id.allWords
    }

    private val pageSelectedObserver = Observer<Unit>{
        screenMainBinding.viewPager.currentItem = 1
        screenMainBinding.bottomNavigationMain.selectedItemId = R.id.savedButton
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _pageSelected = null
        _pageSearch = null
        _screenMainBinding = null
    }
}