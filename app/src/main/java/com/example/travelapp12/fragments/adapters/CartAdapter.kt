package com.example.travelapp12.fragments.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp12.R
import com.example.travelapp12.fragments.data.CartSight
import com.example.travelapp12.fragments.data.Sights
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class CartAdapter(
    private var cartList: ArrayList<CartSight>,
    private var sightsList: ArrayList<Sights>,
    private val onDeleteClickListener: ((CartSight) -> Unit)? = null
) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_cart, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartAdapter.MyViewHolder, position: Int) {
        val currentItem = cartList[position]

        holder.name.text = currentItem.name
        holder.cost.text = "${currentItem.cost?.toString() ?: "0"}£"
        Log.d("AdapterDebug", "CurrentItem cost: ${currentItem.cost}")
        holder.quantity.text = currentItem.count.toString()

        Picasso.get().load(currentItem.image).into(holder.imageView)

        // Обработчики кнопок для изменения количества
        holder.plus.setOnClickListener {
            val currentCount = currentItem.count?.toIntOrNull() ?: 0
            currentItem.count = (currentCount + 1).toString()
            notifyItemChanged(position)
            // Обновление данных в Firebase
            updateFirebaseData(currentItem)
        }

        holder.minus.setOnClickListener {
            val currentCount = currentItem.count?.toIntOrNull() ?: 0
            if (currentCount > 1) {
                currentItem.count = (currentCount - 1).toString()
                notifyItemChanged(position)
                updateFirebaseData(currentItem)
            }
        }


        holder.deleteButton.setOnClickListener {
            onDeleteClickListener?.invoke(currentItem)
        }
    }

    fun updateData(newList: ArrayList<CartSight>) {
        cartList = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvname)
        val cost: TextView = itemView.findViewById(R.id.tvPrice)
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val deleteButton: ImageView = itemView.findViewById(R.id.imageDelete)
        val minus : ImageView = itemView.findViewById(R.id.imageMinus)
        val plus : ImageView = itemView.findViewById(R.id.imagePlus)
        val quantity : TextView = itemView.findViewById(R.id.tvCartProductQuantity)
    }

    private fun updateFirebaseData(cartSight: CartSight) {
        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("3/data")

        cartRef.orderByChild("sights_id").equalTo(cartSight.sights_id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.setValue(cartSight)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Данные успешно обновлены")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseError", "Ошибка обновления данных: ${e.message}")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Ошибка при поиске записи: ${databaseError.message}")
            }
        })
    }

}







