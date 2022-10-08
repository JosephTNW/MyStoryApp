package com.example.mystoryapp.ui.upload

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.response.UsualResponse
import com.example.mystoryapp.utils.timeStamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class UploadViewModel(application: Application, context: Context) : AndroidViewModel(application) {

    val usualResponse = MutableLiveData<UsualResponse>()
    val errorMessage = MutableLiveData<String>()
    private val pref = SharedPref(context)

    private var storyDao: StoryDao?
    private var storyDb: StoryDatabase?

    init {
        storyDb = StoryDatabase.getInstance(application)
        storyDao = storyDb?.StoryDao()
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    fun uriToFile(img: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTemp(context)

        val inputStream = contentResolver.openInputStream(img) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileSize(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun createTemp(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun sendStory(imgMultipart: MultipartBody.Part, desc: RequestBody, context: Context) {
        val service = Client(context).instanceApi().addStory(imgMultipart, desc)
        service.enqueue(object : Callback<UsualResponse> {
            override fun onResponse(call: Call<UsualResponse>, response: Response<UsualResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = UsualResponse(
                            responseBody.error,
                            responseBody.message
                        )
                        usualResponse.postValue(result)
                    }
                }
            }

            override fun onFailure(call: Call<UsualResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    fun resetLocalStory() {
        CoroutineScope(Dispatchers.IO).launch {
            storyDao?.clearStory()
        }
    }

    fun clearPrefs() {
        pref.clearLoginInfo()
    }
}