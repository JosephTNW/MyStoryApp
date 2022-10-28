package com.example.mystoryapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.databinding.ActivityLoginBinding
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.register.RegisterActivity
import com.example.mystoryapp.ui.story.StoryActivity
import com.example.mystoryapp.ui.upload.UploadViewModel
import com.example.mystoryapp.utils.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel : LoginViewModel by viewModels{
            factory
        }

        binding.apply {
            edLoginEmail.buttonSwitch()
            edLoginPassword.buttonSwitch()

            btnLogin.setOnClickListener {
                loginViewModel.login(
                    edLoginEmail.text.toString(),
                    edLoginPassword.text.toString()
                )

                loginViewModel.getLoginResult().observe(this@LoginActivity) {
                    when(it){
                        is Result.Success ->{
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, it.data.message, Toast.LENGTH_SHORT).show()
                            Intent(this@LoginActivity, StoryActivity::class.java).run {
                                startActivity(this)
                                finishAffinity()
                            }
                        }
                        is Result.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
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

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
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