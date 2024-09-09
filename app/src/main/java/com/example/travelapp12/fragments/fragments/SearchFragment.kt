package com.example.travelapp12.fragments.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentSearchBinding
import com.example.travelapp12.databinding.FragmentSightDetailsBinding
import com.example.travelapp12.fragments.adapters.BaseCategoryAdapter
import com.example.travelapp12.fragments.data.ActivityType
import com.example.travelapp12.fragments.data.Sights
import com.example.travelapp12.fragments.util.hideBottomNavigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: BaseCategoryAdapter
    private lateinit var sightsList: ArrayList<Sights>
    private lateinit var activityTypesMap: HashMap<String, String>
    private lateinit var dbref: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)

        val searchEditText = binding.searchEditText
        val rvSearch = binding.rvSearch

        rvSearch.layoutManager = LinearLayoutManager(requireContext())

        sightsList = ArrayList()
        activityTypesMap = HashMap()

        adapter = BaseCategoryAdapter(sightsList, activityTypesMap)
        rvSearch.adapter = adapter

        adapter.onClick = { currentitem ->
            val action = SearchFragmentDirections.actionSearchFragmentToSightDetailFragment(currentitem)
            findNavController().navigate(action)
        }

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEditText.requestFocus()
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        rvSearch.visibility = View.INVISIBLE

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                searchByName(searchText)

                if (searchText.isNotEmpty()) {
                    rvSearch.visibility = View.VISIBLE
                } else {
                    rvSearch.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        getActivityTypesData()
        getSightsData()
    }

    private fun searchByName(name: String) {
        val searchedItemList: ArrayList<Sights> = ArrayList()

        for (item in sightsList) {
            if (item.name?.contains(name, ignoreCase = true) == true) {
                searchedItemList.add(item)
            }
        }

        adapter.updateData(searchedItemList)
    }

    private fun getSightsData() {
        dbref = FirebaseDatabase.getInstance().getReference("4/data")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    sightsList.clear()

                    for (sightsSnapshot in snapshot.children) {
                        val sights = sightsSnapshot.getValue(Sights::class.java)
                        sights?.let { sightsList.add(it) }
                    }

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }

    private fun getActivityTypesData() {
        dbref = FirebaseDatabase.getInstance().getReference("2/data")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    activityTypesMap.clear()

                    for (activitySnapshot in snapshot.children) {
                        val activityType = activitySnapshot.getValue(ActivityType::class.java)
                        val idActivType = activityType?.id_activtype
                        val actType = activityType?.acttype

                        if (idActivType != null && actType != null) {
                            activityTypesMap[idActivType] = actType
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }
}




