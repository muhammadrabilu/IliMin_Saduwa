package com.ayyukana.iliminsaduwa.Respond

import android.os.Parcel
import android.os.Parcelable

data class Respond(
    val etag: String,
    var items: List<Item>,
    val kind: String?,
    val pageInfo: PageInfo
)
