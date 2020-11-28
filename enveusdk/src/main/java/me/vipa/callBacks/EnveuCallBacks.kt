package me.vipa.callBacks

import me.vipa.baseCollection.baseCategoryModel.BaseCategory

interface EnveuCallBacks{

    fun success(status: Boolean, baseCategory : List <BaseCategory>){

    }
    fun failure(status: Boolean, errorCode : Int, message : String){

    }
}