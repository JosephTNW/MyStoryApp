package com.example.mystoryapp.ui.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.UserInformation
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.ui.ViewModelFactory
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.story.StoryActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val Context.dataStore: DataStore<Preferences> ?by preferencesDataStore(name = "token_key")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registerViewModel =
                ViewModelProvider(this, ViewModelFactory(SharedPref.getInstance(dataStore!!), application))[RegisterViewModel::class.java]

            registerViewModel.getLoginInfo().observe(this) { token: String ->
                if (token.isNotEmpty()) {
                    manifestLoading(true)
                    Intent(this@RegisterActivity, StoryActivity::class.java).also {
                        startActivity(it)
                    }
                    manifestLoading(false)
                }
            }

        binding.apply {
            buttonSwitch(edRegisterName)
            buttonSwitch(edRegisterPassword)
            buttonSwitch(edRegisterEmail)
            btnRegister.setOnClickListener {
                manifestLoading(true)
                registerViewModel.sendRegistration(
                    edRegisterName.text.toString(),
                    edRegisterEmail.text.toString(),
                    edRegisterPassword.text.toString()
                )

                registerViewModel.getRegResult().observe(this@RegisterActivity) {
                    Toast.makeText(
                        this@RegisterActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (!it.error && it.message == "User Created") {
                        registerViewModel.login(
                            edRegisterEmail.text.toString(),
                            edRegisterPassword.text.toString()
                        )
                        Intent(this@RegisterActivity, StoryActivity::class.java).run {
                            startActivity(this)
                        }
                    }
                }
                manifestLoading(false)
            }

            btnLogin.setOnClickListener {
                Intent(this@RegisterActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun manifestLoading(status: Boolean) {
        binding.pbLoading.visibility = if (status) View.VISIBLE else View.GONE
    }

    private fun buttonSwitch(et: CustomEditText) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                    btnRegister.isEnabled =
                        edRegisterEmail.textValid == true && edRegisterPassword.textValid == true && edRegisterName.textValid == true
                }
            }
        })
    }
}