package com.mvhub.callBacks

import com.mvhub.baseCollection.baseCategoryModel.BaseCategory

interface EnveuCallBacks{

    fun success(status: Boolean, baseCategory : List <BaseCategory>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}