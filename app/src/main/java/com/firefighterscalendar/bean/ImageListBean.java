package com.firefighterscalendar.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageListBean implements Serializable {

    @SerializedName("iImageId")
    public String imageId;
    @SerializedName("txImage")
    public String imageUrl;
    @SerializedName("eImageType")
    public String imageType;
    @SerializedName("iImageHeight")
    public String imageHeight;
    @SerializedName("iImageWidth")
    public String imageWidth;

    public String title;
}
