package com.gustate.mcga.utils

import java.io.BufferedReader
import java.io.InputStreamReader

object RootUtils {
    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c echo root_check")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val output = bufferedReader.readLine()
            process.waitFor()
            output != null && output.contains("root_check")
        } catch (_: Exception) {
            false
        }
    }
}