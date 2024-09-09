package com.example.travelapp12.fragments.loginRegistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.travelapp12.R
import com.example.travelapp12.databinding.FragmentRegistrationBinding
import com.example.travelapp12.fragments.data.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()

        binding.authText.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val login = edLogin.text.toString().trim()
                val email = edEmailRegister.text.toString().trim()
                val password = edPasswordRegister.text.toString().trim()

                if (password.length < 6) {
                    showToast("Пароль должен состоять минимум из 6 символов")
                    return@setOnClickListener
                }

                // Проверка наличия данных
                if (login.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showToast("Пожалуйста, заполните все поля")
                    return@setOnClickListener
                }

                checkLoginExists(login) { loginExists ->
                    checkEmailExists(email) { emailExists ->
                        if (loginExists) {
                            showToast("Этот логин уже существует")
                        } else if (emailExists) {
                            showToast("Эта электронная почта уже существует")
                        } else {
                            // Продолжение регистрации
                            val hashedPassword = hashPassword(password)
                            val userId = database.reference.push().key ?: ""
                            val user = Users(userId, login, hashedPassword, email)

                            database.reference.child("5/data").child(userId)
                                .setValue(user)
                                .addOnSuccessListener {
                                    showToast("Регистрация прошла успешно")
                                    // Дополнительные действия после успешной регистрации
                                }
                                .addOnFailureListener { e ->
                                    showToast("Ошибка регистрации: ${e.message}")
                                }
                        }
                    }
                }
            }
        }


    }

    private fun hashPassword(password: String): String {
        val bcrypt = BCrypt.withDefaults()
        return bcrypt.hashToString(12, password.toCharArray())
    }

    private fun checkLoginExists(login: String, callback: (Boolean) -> Unit) {
        val databaseRef = database.reference.child("5/data")
        databaseRef.orderByChild("login").equalTo(login).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
            }
        })
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        val databaseRef = database.reference.child("5/data")
        databaseRef.orderByChild("emailAddress").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}