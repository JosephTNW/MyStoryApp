package com.example.mystoryapp.ui.upload

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
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
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.databinding.ActivityUploadBinding
import com.example.mystoryapp.ui.camera.CameraActivity
import com.example.mystoryapp.ui.customview.CustomEditText
import com.example.mystoryapp.ui.login.LoginActivity
import com.example.mystoryapp.ui.map.MapsActivity
import com.example.mystoryapp.ui.story.StoryActivity
import com.example.mystoryapp.utils.Utils.reduceFileSize
import com.example.mystoryapp.utils.Utils.rotateBitmap
import com.example.mystoryapp.utils.Utils.uriToFile
import com.example.mystoryapp.utils.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels()
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
                    val lat: RequestBody?
                    val lon: RequestBody?
                    if (location != null){
                        lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    } else{
                        Toast.makeText(this@UploadActivity, resources.getString(R.string.no_location), Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    uploadViewModel.sendStory(imgMultiPart, desc, lat, lon)
                    uploadViewModel.addStory().observe(this@UploadActivity){
                        when (it) {
                            is Result.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.pbLoading.visibility = View.GONE
                                Toast.makeText(
                                    this@UploadActivity,
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Intent(this@UploadActivity, StoryActivity::class.java).run {
                                    startActivity(this)
                                }
                            }
                            is Result.Error -> {
                                binding.pbLoading.visibility = View.GONE
                                Toast.makeText(this@UploadActivity, it.error, Toast.LENGTH_SHORT)
                                    .show()
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

    override fun onStart() {
        super.onStart()
        getLocation()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    this.location = loc
                } else {
                    Toast.makeText(
                        this, getString(R.string.GPS_off), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 0

        private val REQUESTED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}