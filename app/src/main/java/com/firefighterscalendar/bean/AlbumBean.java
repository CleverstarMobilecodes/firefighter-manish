package com.firefighterscalendar.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AlbumBean implements Serializable {

    @SerializedName("iAlbumId")
    public String albumId;
    @SerializedName("vAlbumName")
    public String albumName;
    @SerializedName("yAlbumYear")
    public String albumYear;
    @SerializedName("dAlbumPrice")
    public String albumPrice;
    @SerializedName("txCoverPicture")
    public String coverPicture;
    @SerializedName("eAlbumType")
    public String albumType;
//    @SerializedName("IsUnlock")
//    public String isUnlock;

}
