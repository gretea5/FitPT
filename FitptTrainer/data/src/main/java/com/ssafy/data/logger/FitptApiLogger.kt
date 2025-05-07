package com.ssafy.locket.data.network.logger

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor

class FitptApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "Locket"
        if(message.startsWith("{") || message.startsWith("[")){
            try{
                val prettyPrintJson = GsonBuilder().setPrettyPrinting().create().toJson(JsonParser().parse(message))

                Log.v(logName, prettyPrintJson)
            }catch (e: JsonSyntaxException){
                Log.v(logName, message)
            }
        }
        else Log.v(logName, message)
    }
}