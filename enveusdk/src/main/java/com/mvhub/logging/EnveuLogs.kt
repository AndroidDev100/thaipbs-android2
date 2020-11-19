package com.mvhub.logging

import android.util.Log

object EnveuLogs {

    fun printWarning(value : String){
        Log.w("EnveuWarning-->>",value)
    }

    fun printError(value : String){
        Log.w("EnveuError-->>",value)
    }
}