package com.firefighterscalendar.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryBean implements Serializable {

    @SerializedName("iCategoryId")
    public String categoryId;
    @SerializedName("vCategoryName")
    public String categoryName;
    @SerializedName("txCategoryIcon")
    public String categoryIcon;

    public List<SubCategoryBean> listSubCategory;
}
