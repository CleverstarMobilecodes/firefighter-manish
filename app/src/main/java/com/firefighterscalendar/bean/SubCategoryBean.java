package com.firefighterscalendar.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubCategoryBean implements Serializable {

    @SerializedName("iSubCategoryId")
    public String subCategoryId;

    @SerializedName("vSubCategoryName")
    public String subCategoryName;

    @SerializedName("txSubCategoryImage")
    public String subCategoryImage;

}