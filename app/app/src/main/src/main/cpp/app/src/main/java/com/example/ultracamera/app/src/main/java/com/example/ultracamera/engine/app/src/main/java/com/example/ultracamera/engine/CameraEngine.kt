package com.example.ultracamera.engine

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.util.Log

class CameraEngine(private val context: Context) {

    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun getCameraList(): Array<String> {
        return try {
            cameraManager.cameraIdList
        } catch (e: Exception) {
            Log.e("CameraEngine", "Erreur lecture caméras", e)
            emptyArray()
        }
    }
}

