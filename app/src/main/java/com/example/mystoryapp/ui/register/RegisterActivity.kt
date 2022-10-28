package com.example.mystoryapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.story.StoryActivity
import com.example.mystoryapp.utils.Constants.NO_TOKEN
import com.example.mystoryapp.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val registerViewModel : RegisterViewModel by viewModels{
            factory
        }

        val token = registerViewModel.getLoginInfo()

        if (token != null) {
            if (token != NO_TOKEN) {
                Intent(this@RegisterActivity, StoryActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        binding.apply {
            buttonSwitch(edRegisterName)
            buttonSwitch(edRegisterPassword)
            buttonSwitch(edRegisterEmail)
            btnRegister.setOnClickListener {
                registerViewModel.sendRegistration(
                    edRegisterName.text.toString(),
                    edRegisterEmail.text.toString(),
                    edRegisterPassword.text.toString()
                )

                registerViewModel.getRegResult().observe(this@RegisterActivity) {
                    when (it) {
                        is Result.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(
                                this@RegisterActivity,
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(this@RegisterActivity, LoginActivity::class.java).run {
                                startActivity(this)
                            }
                        }
                        is Result.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            Toast.makeText(this@RegisterActivity, it.error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            btnLogin.setOnClickListener {
                Intent(this@RegisterActivity, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
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