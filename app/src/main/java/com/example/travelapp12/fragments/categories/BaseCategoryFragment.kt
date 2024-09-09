package com.example.travelapp12.fragments.categories

import androidx.fragment.app.Fragment
import com.example.travelapp12.R
import com.example.travelapp12.fragments.util.showBottomNavigation

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {

    override fun onResume(){
        super.onResume()

        showBottomNavigation()
    }

}






