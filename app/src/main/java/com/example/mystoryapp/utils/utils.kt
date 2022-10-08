package com.example.mystoryapp.utils

import java.text.SimpleDateFormat
import java.util.*

private val FILENAME = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME,
    Locale.US
).format(System.currentTimeMillis())