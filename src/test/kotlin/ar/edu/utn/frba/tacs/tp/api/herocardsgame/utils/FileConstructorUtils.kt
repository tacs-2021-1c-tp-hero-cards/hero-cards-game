package ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils;

import com.google.gson.Gson
import java.io.File
import java.io.IOException

class FileConstructorUtils {
    companion object {
        fun <T> createFromFile(file: String, classReturn: Class<T>): T {
            try {
                val bufferedReader = File(file).bufferedReader()
                return Gson().fromJson(bufferedReader, classReturn)
            } catch (e: IOException) {
                throw RuntimeException(e);
            }
        }

        fun <T> createFromContent(content: String, classReturn: Class<T>): T {
            try {
                return Gson().fromJson(content, classReturn)
            } catch (e: IOException) {
                throw RuntimeException(e);
            }
        }
    }
}