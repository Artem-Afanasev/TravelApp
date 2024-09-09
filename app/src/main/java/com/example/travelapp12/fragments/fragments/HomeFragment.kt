package com.example.travelapp12.fragments.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentHomeBinding
import com.example.travelapp12.fragments.adapters.BaseCategoryAdapter
import com.example.travelapp12.fragments.adapters.HomeViewPagerAdapter
import com.example.travelapp12.fragments.categories.ActiveFragment
import com.example.travelapp12.fragments.categories.ArchitecturFragment
import com.example.travelapp12.fragments.categories.CastlesFragment
import com.example.travelapp12.fragments.categories.MainCategoryFragment
import com.example.travelapp12.fragments.categories.MuseumsFragment
import com.example.travelapp12.fragments.data.Sights
import com.google.android.material.tabs.TabLayoutMediator
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp12.fragments.SearchListener


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager2Adapter: HomeViewPagerAdapter
    private var currentFragmentPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            MuseumsFragment(),
            ArchitecturFragment(),
            ActiveFragment(),
            CastlesFragment()
        )
        viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        binding.viewpagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentFragmentPosition = position
            }
        })

        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Весь список"
                1 -> tab.text = "Музеи"
                2 -> tab.text = "Архитектурные сооружения"
                3 -> tab.text = "Активный отдых"
                4 -> tab.text = "Замки"
            }
        }
        tabLayoutMediator.attach()

        // Обработчик нажатия на строку поиска
        // Обработчик нажатия на строку поиска
        binding.etSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)

            // Установка фокуса и открытие клавиатуры
            binding.etSearch.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

    }

    override fun onResume() {
        super.onResume()
        viewPager2Adapter.notifyDataSetChanged()
    }
}














