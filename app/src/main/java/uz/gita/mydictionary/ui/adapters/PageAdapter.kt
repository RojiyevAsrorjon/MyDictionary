package uz.gita.mydictionary.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.gita.mydictionary.ui.pages.PageSearch
import uz.gita.mydictionary.ui.pages.PageSelected

class PageAdapter(
    fragment: FragmentManager,
    lifecycle: Lifecycle,
    private val pageSearch: PageSearch,
    private val pageSelected: PageSelected
) : FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                pageSearch
            }
            else -> pageSelected
        }
    }

}