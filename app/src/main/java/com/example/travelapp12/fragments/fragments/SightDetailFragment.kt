package com.example.travelapp12.fragments.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentSightDetailsBinding
import com.example.travelapp12.fragments.adapters.ViewPager2Images
import com.example.travelapp12.fragments.data.ActivityType
import com.example.travelapp12.fragments.data.Cart
import com.example.travelapp12.fragments.data.CartSight
import com.example.travelapp12.fragments.util.hideBottomNavigation
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class SightDetailFragment : Fragment() {
    private val args: SightDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentSightDetailsBinding
    private lateinit var viewPagerAdapter: ViewPager2Images
    private lateinit var viewPager: ViewPager2
    private lateinit var activityTypesMap: HashMap<String, ActivityType>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        binding = FragmentSightDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = view.findViewById<Button>(R.id.addToCart)

        val selectedSight = args.sights

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        selectedSight?.let {
            binding.apply {
                sightName.text = selectedSight.name ?: ""
                description.text = selectedSight.description ?: ""
                address.text = selectedSight.address ?: ""
                time.text = selectedSight.worktime?.replace("_n","\n") ?: ""
                cost.text = "£ ${selectedSight.cost ?: ""}"
            }

            val imageList = listOf(selectedSight?.image, selectedSight?.image2).filterNotNull()
            viewPagerAdapter = ViewPager2Images(imageList)

            viewPager = binding.viewPagerSightImages
            viewPager.adapter = viewPagerAdapter
        }

        loadActivityTypes()

        selectedSight?.id_sights?.let {
            checkItemInCart(it, addButton, requireContext())
        }

        addButton.setOnClickListener {
            val userId = getUserIdFromSharedPreferences()
            selectedSight?.id_sights?.let { sightId ->
                addToCart(sightId, userId, addButton, requireContext())
            }
        }

    }

    private fun loadActivityTypes() {
        val activityTypesRef = Firebase.database.reference.child("2/data")
        activityTypesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityTypesMap = HashMap()
                for (activitySnapshot in snapshot.children) {
                    val idActivType =
                        activitySnapshot.child("id_activtype").getValue(String::class.java)
                    val actType = activitySnapshot.child("acttype").getValue(String::class.java)
                    idActivType?.let {
                        activityTypesMap[it] = ActivityType(actType, idActivType)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки базы данных
            }
        })
    }

    private fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", null) != null &&
                sharedPreferences.getString("user_login", null) != null &&
                sharedPreferences.getString("user_email", null) != null
    }

    private fun checkItemInCart(sightId: String, addButton: Button, context: Context) {
        if (!isUserLoggedIn(context)) {
            addButton.isEnabled = false
            addButton.setTextColor(ContextCompat.getColor(addButton.context, R.color.g_gray500))
            return
        }

        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("3/data")
        val currentUserId = getUserIdFromSharedPreferences()

        cartRef.orderByChild("sights_id").equalTo(sightId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var itemAddedByCurrentUser = false
                    for (snapshot in dataSnapshot.children) {
                        val cart = snapshot.getValue(CartSight::class.java)
                        if (cart?.users_id == currentUserId) {
                            itemAddedByCurrentUser = true
                            break
                        }
                    }

                    updateAddButtonState(itemAddedByCurrentUser, addButton)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Обработка ошибки при чтении данных
                }
            })
    }

    private fun updateAddButtonState(itemAddedByCurrentUser: Boolean, addButton: Button) {
        if (itemAddedByCurrentUser) {
            addButton.text = "Добавлено"
            addButton.isEnabled = false
        } else {
            addButton.text = "Добавить"
            addButton.isEnabled = true
        }
    }


    private fun addToCart(sightId: String, userId: String?, addToCartButton: Button, context: Context) {
        if (!isUserLoggedIn(context)) {
            addToCartButton.isEnabled = false
            addToCartButton.setTextColor(ContextCompat.getColor(addToCartButton.context, R.color.g_gray500))
            return
        }

        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("3/data")

        val newCartItem = Cart("1", sightId, null, userId) // Передаем userId в конструктор Cart
        val cartItemRef = cartRef.push()

        cartItemRef.setValue(newCartItem)
            .addOnSuccessListener {
                // Обновляем состояние кнопки "Добавить"
                checkItemInCart(sightId, addToCartButton, context)
            }
            .addOnFailureListener {
                // Обработка ошибок при добавлении элемента в корзину
            }
    }


    private fun getUserIdFromSharedPreferences(): String? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", null)
    }
}






