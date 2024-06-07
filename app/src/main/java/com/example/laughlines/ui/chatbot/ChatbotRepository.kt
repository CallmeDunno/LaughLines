package com.example.laughlines.ui.chatbot

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatbotRepository {

    private val key = "AIzaSyA4wX5ziTSF5zSh7ymwwpdQnYLSfIJYb1E"

    suspend fun getResponse(messages: String) : Chatbot {
        val generativeModel = GenerativeModel(modelName = "gemini-pro", apiKey = key)

        return try{
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(messages)
            }
            Log.i("Dunno", response.text ?: "aaa")
            Chatbot(response.text ?: "Error", null, false)
        } catch (exc: ResponseStoppedException) {
            Log.i("Dunno", exc.message ?: "aaa")
            Chatbot(exc.message ?: "Error", null, false)
        }
    }

    suspend fun getResponse(image: Bitmap) : Chatbot {
        val generativeModel = GenerativeModel(modelName = "gemini-pro-vision", apiKey = key)

        return try{
            val inputContent = content {
                image(image)
                text("Analyze this photo")
            }

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }

            Chatbot(response.text ?: "Error", null, false)
        } catch (exc: ResponseStoppedException) {
            Chatbot(exc.message ?: "Error", null, false)
        }
    }

}