package com.example.mystoryapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.UserInformation
import com.example.mystoryapp.databinding.ActivityLoginBinding
import com.example.mystoryapp.ui.ViewModelFactory
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.register.RegisterActivity
import com.example.mystoryapp.ui.register.RegisterViewModel
import com.example.mystoryapp.ui.story.StoryActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(application, this))[LoginViewModel::class.java]

        binding.apply {
            edLoginEmail.buttonSwitch()
            edLoginPassword.buttonSwitch()

            btnLogin.setOnClickListener {
                manifestLoading(true)
                loginViewModel.login(
                    edLoginEmail.text.toString(),
                    edLoginPassword.text.toString(),
                    this@LoginActivity
                )

                loginViewModel.getLoginResult().observe(this@LoginActivity) {
                        val token = loginViewModel.getSavedToken()
                        Toast.makeText(this@LoginActivity, token, Toast.LENGTH_SHORT).show()
                    if (!it.error && it.message == "success") {
                        manifestLoading(false)
                        Intent(this@LoginActivity, StoryActivity::class.java).run {
                            startActivity(this)
                        }
                    }
                }
            }

            btnRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun manifestLoading(status: Boolean) {
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }

    private fun CustomEditText.buttonSwitch() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                    btnLogin.isEnabled =
                        edLoginEmail.textValid == true && edLoginPassword.textValid == true
                }
            }
        })
    }
}