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
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_key")
    private lateinit var checkToken: String
    private var checked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SharedPref.getInstance(dataStore)

        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref, application))[LoginViewModel::class.java]

        binding.apply {
            edLoginEmail.buttonSwitch()
            edLoginPassword.buttonSwitch()

            btnLogin.setOnClickListener {
                manifestLoading(true)
                loginViewModel.login(
                    edLoginEmail.text.toString(),
                    edLoginPassword.text.toString()
                )

                loginViewModel.getSavedToken().observe(this@LoginActivity) { token: String ->
                    if (token.isNotEmpty()) {
                        checkToken = token
                        checked = true
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Token data not saved yet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                loginViewModel.getLoginResult().observe(this@LoginActivity) {
                    Toast.makeText(this@LoginActivity, checkToken, Toast.LENGTH_SHORT).show()
                    if (!it.error && it.message == "success" && checked) {
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