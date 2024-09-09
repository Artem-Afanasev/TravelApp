package com.example.travelapp12.fragments.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentCartBinding
import com.example.travelapp12.databinding.FragmentRegistrationBinding
import com.example.travelapp12.fragments.adapters.CartAdapter
import com.example.travelapp12.fragments.data.Cart
import com.example.travelapp12.fragments.data.CartSight
import com.example.travelapp12.fragments.data.Sights
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.HashMap

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var firebaseDataList: ArrayList<Sights>
    private lateinit var cartDataMap: MutableMap<String, CartSight>
    private var totalCost: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        firebaseDataList = ArrayList()
        cartDataMap = HashMap()

        val sharedPreferences = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userLogin = sharedPreferences.getString("user_login", "")

        binding.profileText.text = userLogin

        val isLoggedIn = userLogin != null && userLogin.isNotEmpty()

        if (isLoggedIn) {
            binding.profileText.text = "Выйти из аккаунта"
            binding.profileText.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        } else {
            binding.profileText.setOnClickListener {
                findNavController().navigate(R.id.action_cartFragment_to_loginFragment)
            }
        }

        binding.profileText.text = userLogin

        cartAdapter = CartAdapter(ArrayList(), ArrayList()) { cartSight ->

            val database = FirebaseDatabase.getInstance()
            val cartRef = database.getReference("3/data")

            cartRef.orderByChild("sights_id").equalTo(cartSight.sights_id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {

                        snapshot.ref.removeValue().addOnSuccessListener {

                            Log.d("Firebase", "Запись успешно удалена")
                            Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show()
                            updateCartListAndTotalCost()
                        }.addOnFailureListener { e ->

                            Log.e("FirebaseError", "Ошибка удаления записи: ${e.message}")
                            Toast.makeText(requireContext(), "Ошибка удаления записи", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                    Log.e("FirebaseError", "Ошибка при поиске записи: ${databaseError.message}")
                    Toast.makeText(requireContext(), "Ошибка при поиске записи", Toast.LENGTH_SHORT).show()
                }
            })
        }
        recyclerView.adapter = cartAdapter

        val database = FirebaseDatabase.getInstance()
        val sightRef = database.getReference("4/data")
        val cartRef = database.getReference("3/data")

        sightRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<Sights>()

                for (snapshot in dataSnapshot.children) {
                    val firebaseData = snapshot.getValue(Sights::class.java)
                    firebaseData?.let {
                        tempList.add(it)
                    }
                }

                firebaseDataList.clear()
                firebaseDataList.addAll(tempList)

                updateCartListAndTotalCost()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Failed to read database: ${databaseError.message}")
            }
        })

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<CartSight>()
                for (snapshot in dataSnapshot.children) {
                    val cartData = snapshot.getValue(CartSight::class.java)
                    cartData?.let {
                        tempList.add(it)
                    }
                }
                cartDataMap.clear()
                tempList.forEach { cartData ->
                    cartDataMap[cartData.sights_id!!] = cartData
                }

                updateCartListAndTotalCost()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Failed to read database: ${databaseError.message}")
            }
        })
    }

    private fun updateCartListAndTotalCost() {

        if (!isAdded) {
            return
        }

        val cartList = ArrayList<CartSight>()
        totalCost = 0

        val sharedPreferences = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            val userCartData = cartDataMap.values.filter { it.users_id == userId }

            for (cartDataEntry in userCartData) {
                val sightData = firebaseDataList.find { it.id_sights == cartDataEntry.sights_id }

                val cartSight = CartSight(
                    name = sightData?.name,
                    image = sightData?.image,
                    id_sights = cartDataEntry.id_sights,
                    sights_id = cartDataEntry.sights_id,
                    count = cartDataEntry.count,
                    cost = sightData?.cost,
                    users_id = cartDataEntry.users_id
                )

                cartList.add(cartSight)

                val cost = sightData?.cost?.toInt() ?: 0
                totalCost += cost * cartDataEntry.count!!.toInt()
            }
        } else {
            Log.e("CartFragment", "User ID is null")
        }

        cartAdapter.updateData(cartList)
        updateTotalCost(totalCost)

        if (cartAdapter.itemCount == 0) {
            view?.findViewById<ConstraintLayout>(R.id.layout_cart_empty)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<ConstraintLayout>(R.id.layout_cart_empty)?.visibility = View.GONE
        }
    }


    private fun updateTotalCost(totalCost: Int) {
        val tvTotalCost = view?.findViewById<TextView>(R.id.tvTotalPrice)
        tvTotalCost?.text = "Общая стоимость: $totalCost £"
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выйти из аккаунта")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Да") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("user_login")
        editor.apply()

        findNavController().navigate(R.id.homeFragment)
    }


}







