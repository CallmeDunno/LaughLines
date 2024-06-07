package com.example.laughlines.model

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.util.*

class MorseCode constructor(private val context: Context) {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var morseCode: String = ""
    private var currentIndex = 0
    private var isFlashOn = false
    private var scope = CoroutineScope(Job() + Dispatchers.Default)

    private val morseCodeMap = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.",
        'D' to "-..", 'E' to ".", 'F' to "..-.",
        'G' to "--.", 'H' to "....", 'I' to "..",
        'J' to ".---", 'K' to "-.-", 'L' to ".-..",
        'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
        'S' to "...", 'T' to "-", 'U' to "..-",
        'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..",
        '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....",
        '7' to "--...", '8' to "---..", '9' to "----.",
        '0' to "-----", ' ' to "/"
    )

    private var _isCompleted = MutableLiveData<Boolean>()
    val isCompleted
        get() = _isCompleted

    init {
        if (checkCamera()) {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                cameraId = cameraManager.cameraIdList[0]
            } catch (e: CameraAccessException) {
                e.printStackTrace()
                Log.e("Dunno", e.message.toString())
            }
        }
    }

    fun textToMorse(text: String): String {
        val stringBuilder = StringBuilder()
        currentIndex = 0
        this.morseCode = ""
        for (char in text.uppercase()) {
            if (char == ' ') {
                stringBuilder.append(" ")
            } else {
                morseCodeMap[char]?.let {
                    stringBuilder.append("$it ")
                }
            }
        }
        this.morseCode = stringBuilder.toString().trim()
        return stringBuilder.toString().trim()
    }

    fun getMorseCodeFromText(text: String): String {
        val stringBuilder = StringBuilder()
        for (char in text.uppercase(Locale.getDefault())) {
            if (char == ' ') {
                stringBuilder.append(" ")
            } else {
                morseCodeMap[char]?.let {
                    stringBuilder.append("$it ")
                }
            }
        }
        return stringBuilder.toString().trim()
    }

    fun flashMorseCode() {
        Log.d("Dunno", this.morseCode)
        scope.launch {
            while (true) {
                if (currentIndex < morseCode.length) {
                    when (morseCode[currentIndex]) {
                        '.' -> flashLightOn(100)
                        '-' -> flashLightOn(300)
                        ' ' -> flashLightOff(300)
                        '/' -> flashLightOff(700)
                    }
                    currentIndex += 1
                } else break
                delay(300)
            }
            _isCompleted.postValue(true)
        }
    }

    fun cancelFlashMorseCode() {
        this.morseCode = ""
        currentIndex = 0
        isFlashOn = false
        scope.cancel()
        flashLightOff()
        scope = CoroutineScope(Job() + Dispatchers.Default)
    }

    private fun flashLightOff() {
        try {
            cameraManager.setTorchMode(cameraId!!, false)
            isFlashOn = false
        } catch (e: Exception){
            Log.d("Dunno", e.message.toString() + " off")
        }

    }

    fun setMorseCode(morseCode: String) {
        currentIndex = 0
        this.morseCode = morseCode
    }

    private suspend fun flashLightOn(time: Long) {
        cameraManager.setTorchMode(cameraId!!, true)
        isFlashOn = true
        delay(time)
        flashLightOff()
    }

    private suspend fun flashLightOff(time: Long) {
        cameraManager.setTorchMode(cameraId!!, false)
        isFlashOn = false
        delay(time)
    }

    private fun checkCamera(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

}
