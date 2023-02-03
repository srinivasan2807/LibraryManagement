package com.example.librarymanagement.utils

import android.graphics.Bitmap

object Utils {
    fun resizeImage(image: Bitmap): Bitmap {

        val width = image.width
        val height = image.height

        val scaleWidth = width / 10
        val scaleHeight = height / 10

        if (image.byteCount <= 1000000)
            return image

        return Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
    }
}