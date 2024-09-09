package com.example.travelapp12.fragments.fragments

import android.app.AlertDialog
import com.example.travelapp12.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelapp12.databinding.FragmentProfileBinding
import com.example.travelapp12.databinding.FragmentRegistrationBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение информации о пользователе из SharedPreferences
        val sharedPreferences =
            requireActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userLogin = sharedPreferences.getString("user_login", null)
        val userEmail = sharedPreferences.getString("user_email", null)

        binding.logout.setOnClickListener {
            // Здесь показываем диалоговое окно для подтверждения выхода из аккаунта
            showLogoutConfirmationDialog()
        }

        binding.apply {
            if (userLogin != null && userEmail != null) {
                // Отображение имени пользователя и электронной почты
                tvUserName.text = userLogin
                tvEmail.text = userEmail
            } else {

            }
        }
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