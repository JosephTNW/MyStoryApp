package com.example.mystoryapp.ui.upload

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ActivityUploadBinding
import com.example.mystoryapp.ui.camera.CameraActivity
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.map.MapsActivity
import com.example.mystoryapp.ui.story.StoryActivity
import com.example.mystoryapp.utils.ViewModelFactory
import com.example.mystoryapp.utils.reduceFileSize
import com.example.mystoryapp.utils.rotateBitmap
import com.example.mystoryapp.utils.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels()
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val uploadViewModel : UploadViewModel by viewModels{
            factory
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUESTED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            edAddDescription.buttonSwitch()

            btnCamera.setOnClickListener {
                val intent = Intent(this@UploadActivity, CameraActivity::class.java)
                launcherIntentCamera.launch(intent)
            }

            btnGallery.setOnClickListener {
                val intent = Intent()
                intent.action = ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, getString(R.string.choose_img))
                launcherIntentGallery.launch(chooser)
            }

            buttonAdd.setOnClickListener {
                manifestLoading(true)
                if (getFile != null) {
                    val file = reduceFileSize(getFile as File)

                    val desc = "${edAddDescription.text}".toRequestBody("text/plain".toMediaType())
                    val reqImgFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imgMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        reqImgFile
                    )
                    uploadViewModel.sendStory(imgMultiPart, desc)
                    uploadViewModel.getResult().observe(this@UploadActivity){
                        Toast.makeText(this@UploadActivity, it.message, Toast.LENGTH_SHORT).show()
                        if (!it.error && it.message != "success"){
                            Intent(this@UploadActivity, StoryActivity::class.java).run {
                                runBlocking(Dispatchers.IO) {
                                    delay(5000L)
                                }
                                startActivity(this)
                                manifestLoading(false)
                                finishAffinity()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@UploadActivity,
                        resources.getString(R.string.no_file_yet),
                        Toast.LENGTH_SHORT
                    ).show()
                    manifestLoading(false)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_permission_camera),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                uploadViewModel.clearPrefs()
                uploadViewModel.resetLocalStory()
                Intent(this@UploadActivity, LoginActivity::class.java).also {
                    startActivity(it)
                    finishAffinity()
                }
            }
            R.id.menu_language -> {
                val lIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(lIntent)
            }
            R.id.menu_map -> {
                Intent(this@UploadActivity, MapsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return true
    }

    private fun allPermissionsGranted() = REQUESTED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("photo") as File
            val isBackCam = it.data?.getBooleanExtra("isBackCam", true) as Boolean
            getFile = myFile
            val result =
                rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCam)

            binding.ivPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val img: Uri = result.data?.data as Uri
            val file = uriToFile(img, this@UploadActivity)
            getFile = file
            binding.ivPreview.setImageURI(img)
        }
    }

    private fun CustomEditText.buttonSwitch() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                    buttonAdd.isEnabled =
                        edAddDescription.textValid == true
                }
            }
        })
    }

    private fun manifestLoading(state: Boolean){
        if (state) {
            binding.pbLoading.visibility = View.VISIBLE
            binding.ivPreview.visibility = View.INVISIBLE
        } else{
            binding.pbLoading.visibility = View.GONE
            binding.ivPreview.visibility = View.VISIBLE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 0

        private val REQUESTED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}