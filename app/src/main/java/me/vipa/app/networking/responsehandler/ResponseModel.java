package me.vipa.app.networking.responsehandler;

import com.vipa.baseCollection.baseCategoryModel.BaseCategory;
import me.vipa.app.networking.errormodel.ApiErrorModel;

import java.util.List;

import me.vipa.baseCollection.baseCategoryModel.BaseCategory;

public class ResponseModel<T> {

    String status;
    T baseCategory;
    List<BaseCategory> baseCategoriesList;
    ApiErrorModel errorModel;

    public ResponseModel(String status,List<BaseCategory> list, ApiErrorModel errorModel){
        this.status=status;
        this.baseCategoriesList=list;
        this.errorModel=errorModel;
    }

    public ResponseModel(String status,T model, ApiErrorModel errorModel){
        this.status=status;
        this.baseCategory=model;
        this.errorModel=errorModel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setBaseCategory(T baseCategory) {
        this.baseCategory = baseCategory;
    }

    public T getBaseCategory() {
        return baseCategory;
    }

    public ApiErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ApiErrorModel errorModel) {
        this.errorModel = errorModel;
    }

    public void setBaseCategoriesList(List<BaseCategory> baseCategoriesList) {
        this.baseCategoriesList = baseCategoriesList;
    }

    public List<BaseCategory> getBaseCategoriesList() {
        return baseCategoriesList;
    }
}
