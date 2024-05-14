package com.example.laughlines.utils

import android.Manifest

object Constant {

    val INTENT_KEY = "intent_key"

    const val ID_DEFAULT = "LaughLines"

    const val DateTimeFormat1 = "dd/MM/yyyy hh:mm"
    const val DateTimeFormat2 = "dd_MM_yyyy_hh_mm"
    const val DateTimeFormat3 = "dd-MM-yyyy hh:mm:ss"

    const val MESSAGE = 1
    const val IMAGE = 2
    const val LOCATION = 3

    var ORIENTATION_CAMERA : String = CameraStateEnum.CAMERA_BACK.name

    const val PERMISSION_CAMERA = Manifest.permission.CAMERA
    const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val PERMISSION_READ_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    const val PER_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val PER_STORAGE_CODE = 1
    const val PER_CAMERA_CODE = 2
    const val PER_LOCATION_CODE = 3
    const val GALLERY_REQUEST_CODE = 4

    enum class CameraStateEnum {
        CAMERA_BACK,
        CAMERA_FRONT
    }

    enum class Collection {
        User,
        Chats,
        Friends,
        Requests,
        Messages,
    }

    enum class Key {
        ID,

    }

}