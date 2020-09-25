package com.ayyukana.iliminsaduwa.model

import com.ayyukana.iliminsaduwa.Respond.Respond
import kotlin.random.Random
class Constant{
    companion object Contants {
        private val PLAY_LIST_ID = "PLVMpvKjcWR4MI97F06J8A20ywKM_HsZIJ&key="
        private val BASE_URL =
            "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId="

        private  val ARRAY_OF_API_KEY = arrayOf(
            "AIzaSyAbofuxCryg5F8aba2YLkWR4GXETERXlf8", "AIzaSyDxHXosCUBzv6XCvfkQavyQKEPuZLjhMjg"
        )

        private val randomIndex = Random.nextInt(ARRAY_OF_API_KEY.size)
        var  API_KEY = ARRAY_OF_API_KEY[randomIndex]
        val FULL_URL = BASE_URL + PLAY_LIST_ID + API_KEY

        val SELECTED_ITEMS = "selected_items"
    }
}
