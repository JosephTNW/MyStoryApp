package com.example.mystoryapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.story.StoryActivity
import com.example.mystoryapp.ui.upload.UploadViewModel
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
        manifestLoading(true)

        if (token != null) {
            if (token != NO_TOKEN) {
                Intent(this@RegisterActivity, StoryActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        manifestLoading(false)

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
                            finishAffinity()
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

    override fun onBackPressed() {
        super.onBackPressed()
        onDestroy()
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