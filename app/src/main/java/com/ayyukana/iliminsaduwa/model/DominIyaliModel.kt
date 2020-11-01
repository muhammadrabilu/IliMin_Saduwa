package com.ayyukana.iliminsaduwa.model

import android.os.Parcel
import android.os.Parcelable


class DominIyaliModel() : Parcelable {
    var id: String? = null
    var title: String? = null
    var videoURL: String? = null
    var videoName: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        title = parcel.readString()
        videoURL = parcel.readString()
        videoName = parcel.readString()
    }

    constructor(id: String?, title: String?, videoURL: String?, videoName: String?) : this() {
        this.id = id
        this.title = title
        this.videoURL = videoURL
        this.videoName = videoName
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(videoURL)
        parcel.writeString(videoName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DominIyaliModel> {
        override fun createFromParcel(parcel: Parcel): DominIyaliModel {
            return DominIyaliModel(parcel)
        }

        override fun newArray(size: Int): Array<DominIyaliModel?> {
            return arrayOfNulls(size)
        }
    }

}