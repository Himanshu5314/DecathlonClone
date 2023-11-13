package com.example.decathlon.utils

import android.content.Context
import coil.request.ImageRequest

fun Context.getImageRequestBuilder(data: Any?) =
    ImageRequest.Builder(context = this).data(data = data).allowHardware(enable = false).build()