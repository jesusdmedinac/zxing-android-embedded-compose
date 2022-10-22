/*
 * Copyright (C) 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.client.android.camera.open

import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.util.Log

object OpenCamera {
    private val TAG = OpenCamera::class.java.name

    /**
     * For [.open], means no preference for which camera to open.
     */
    const val NO_REQUESTED_CAMERA = -1

    @JvmStatic
    fun getCameraId(requestedId: Int): Int {
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) return noCameras()
        var cameraId = requestedId
        val explicitRequest = cameraId >= 0
        if (!explicitRequest) {
            cameraId = selectACameraIfNoExplicitCameraRequested(numCameras)
        }
        return returnsCameraIdOrExplicitRequest(cameraId, numCameras, explicitRequest)
    }

    private fun returnsCameraIdOrExplicitRequest(
        cameraId: Int,
        numCameras: Int,
        explicitRequest: Boolean
    ) = if (cameraId < numCameras) {
        cameraId
    } else if (explicitRequest) {
        -1
    } else {
        0
    }

    private fun noCameras(): Int {
        Log.w(TAG, "No cameras!")
        return -1
    }

    private fun selectACameraIfNoExplicitCameraRequested(
        numCameras: Int,
    ): Int = (0..numCameras)
        .first { index ->
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(index, cameraInfo)
            cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK
        }

    /**
     * Opens the requested camera with [Camera.open], if one exists.
     *
     * @param requestedId camera ID of the camera to use. A negative value
     * or [.NO_REQUESTED_CAMERA] means "no preference"
     * @return handle to [Camera] that was opened
     */
    @JvmStatic
    fun open(requestedId: Int): Camera? {
        val cameraId = getCameraId(requestedId)
        return if (cameraId == -1) {
            null
        } else {
            Camera.open(cameraId)
        }
    }
}