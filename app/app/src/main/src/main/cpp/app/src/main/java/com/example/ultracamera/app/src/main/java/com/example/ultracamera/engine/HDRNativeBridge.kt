package com.example.ultracamera.engine

import android.util.Log
import org.opencv.core.Mat

class HDRNativeBridge {

    companion object {
        private const val TAG = "HDRNativeBridge"
        private var isLibraryLoaded = false

        init {
            try {
                System.loadLibrary("ultracamera_native")
                isLibraryLoaded = true
                Log.i(TAG, "Bibliothèque native chargée")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Erreur chargement lib native", e)
                isLibraryLoaded = false
            }
        }
    }

    private external fun processHDRFusionNative(matAddresses: LongArray, resultAddress: Long): Boolean

    fun processBurst(frames: List<Mat>): Mat? {
        if (!isLibraryLoaded || frames.size < 2) return null
        val addresses = LongArray(frames.size) { i -> frames[i].nativeObjAddr }
        val resultMat = Mat()
        val success = processHDRFusionNative(addresses, resultMat.nativeObjAddr)
        return if (success && !resultMat.empty()) resultMat else null
    }
}

