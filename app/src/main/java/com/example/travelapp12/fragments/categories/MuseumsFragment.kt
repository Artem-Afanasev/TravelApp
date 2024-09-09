package com.example.travelapp12.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp12.databinding.FragmentBaseCategoryBinding
import com.example.travelapp12.fragments.SearchListener
import com.example.travelapp12.fragments.adapters.BaseCategoryAdapter
import com.example.travelapp12.fragments.data.ActivityType
import com.example.travelapp12.fragments.data.Sights
import com.example.travelapp12.fragments.fragments.HomeFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MuseumsFragment : BaseCategoryFragment(){
    private lateinit var dbref: DatabaseReference
    private lateinit var baseRecyclerView: RecyclerView
    private lateinit var baseArrayList: ArrayList<Sights>
    private lateinit var activityTypesMap: HashMap<String, String>
    private lateinit var binding: FragmentBaseCategoryBinding
    private lateinit var adapter: BaseCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseRecyclerView = binding.baseCategory
        baseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        baseRecyclerView.setHasFixedSize(true)

        baseArrayList = arrayListOf()
        activityTypesMap = HashMap()
        getActivityTypesData()

        adapter = BaseCategoryAdapter(baseArrayList, activityTypesMap)
        adapter.onClick = { currentitem ->
            val action = HomeFragmentDirections.actionHomeFragmentToSightDetailFragment(currentitem)
            findNavController().navigate(action)
        }

        baseRecyclerView.adapter = adapter
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

                    val category = activityTypesMap.entries.find { it.value == "Музей" }?.key

                    if (category != null) {
                        getSightsData(category)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })

    }

    private fun getSightsData(category: String) {
        dbref = FirebaseDatabase.getInstance().getReference("4/data")

        dbref.orderByChild("id_activtype").equalTo(category).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    baseArrayList.clear()

                    for (sightsSnapshot in snapshot.children) {
                        val sights = sightsSnapshot.getValue(Sights::class.java)
                        sights?.let { baseArrayList.add(it) }
                    }

                    baseRecyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }
}




