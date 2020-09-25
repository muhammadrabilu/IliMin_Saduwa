package com.ayyukana.iliminsaduwa.model

import android.provider.MediaStore

class YoutubeModel{
    var title: String
    var thumbnail: String
    var videoID: String

    constructor(title: String, thumbnail: String, videoID: String) {
        this.title = title
        this.thumbnail = thumbnail
        this.videoID = videoID
    }
}