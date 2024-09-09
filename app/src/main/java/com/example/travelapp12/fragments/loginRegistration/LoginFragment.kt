package com.example.travelapp12.fragments.loginRegistration

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentLoginBinding
import com.example.travelapp12.fragments.data.Users
import com.example.travelapp12.fragments.util.AuthManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        database = FirebaseDatabase.getInstance()
        binding.regText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString().trim()

                // Проверка наличия данных
                if (email.isEmpty() || password.isEmpty()) {
                    // Обработка ошибки, если поля пустые
                    return@setOnClickListener
                }

                // Авторизация пользователя
                signInUser(email, password)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        database.reference.child("5/data")
            .orderByChild("emailAddress")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            if (user != null) {
                                if (checkPassword(password, user.password.toString())) {
                                    // Авторизация прошла успешно
                                    AuthManager.setUserLoggedIn(true)
                                    saveUserInfo(user.user_id.toString(), user.login, user.emailAddress)
                                    navController.navigate(R.id.action_loginFragment_to_homeFragment)
                                    // Здесь вы можете выполнить действия после успешной авторизации
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Неверный пароль",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Пользователь с таким email не найден",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Обработка ошибок при чтении данных из базы данных
                }
            })
    }

    private fun saveUserInfo(userId: String?, login: String?, email: String?) {
        if (userId != null && login != null && email != null) {
            val sharedPreferences =
                requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("user_id", userId)
            editor.putString("user_login", login)
            editor.putString("user_email", email) // Сохраняем электронную почту пользователя
            editor.apply()
        } else {
            Log.e("LoginFragment", "User ID, login, or email is null")
        }
    }

    private fun checkPassword(password: String, hashedPassword: String): Boolean {
        val bcrypt = BCrypt.verifyer()
        return bcrypt.verify(password.toCharArray(), hashedPassword).verified
    }
}
