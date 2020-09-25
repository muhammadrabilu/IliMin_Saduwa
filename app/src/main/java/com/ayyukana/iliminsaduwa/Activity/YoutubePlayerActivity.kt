package com.ayyukana.iliminsaduwa.Activity

import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayyukana.iliminsaduwa.Activity.MainActivity.Companion.TAG
import com.ayyukana.iliminsaduwa.Adapters.MiniAdapter
import com.ayyukana.iliminsaduwa.Adapters.YoutubeAdapter
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.Respond.Respond
import com.ayyukana.iliminsaduwa.Respond.Snippet
import com.ayyukana.iliminsaduwa.model.Constant
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_youtube_player.*
import kotlinx.android.synthetic.main.activity_youtube_player.recyclerView
import okhttp3.*
import java.io.IOException


class YoutubePlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener,
    MiniAdapter.OnItemClickListener {

    private var player: YouTubePlayer? = null
     var videoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        youtube_player.initialize(Constant.API_KEY, this);

         videoId = intent.getStringExtra(Constant.SELECTED_ITEMS)

        val layoutManager = LinearLayoutManager(this)

        layoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = layoutManager



        fetchJson()

    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer,
        b: Boolean
    ) {
        player = youTubePlayer
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener)
        youTubePlayer.setPlaybackEventListener(playbackEventListener)
        youTubePlayer.cueVideo(videoId)
    }


    private val playbackEventListener: PlaybackEventListener = object : PlaybackEventListener {
        override fun onPlaying() {}
        override fun onPaused() {}
        override fun onStopped() {}
        override fun onBuffering(b: Boolean) {}
        override fun onSeekTo(i: Int) {}
    }

    private val playerStateChangeListener: PlayerStateChangeListener =
        object : PlayerStateChangeListener {
            override fun onLoading() {
                Toast.makeText(this@YoutubePlayerActivity, "Please wait your video is loading", Toast.LENGTH_SHORT).show()
            }
            override fun onLoaded(s: String) {
                player?.play()
            }
            override fun onAdStarted() {}
            override fun onVideoStarted() {}
            override fun onVideoEnded() {

            }
            override fun onError(errorReason: YouTubePlayer.ErrorReason) {
                Log.d(TAG, "onError: $errorReason")
            }
        }


    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Log.d(TAG, "onInitializationFailure: ${p1?.isUserRecoverableError}")
    }

    private fun fetchJson() {

        val request = Request.Builder().url(Constant.FULL_URL).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                Log.d(TAG, "onResponse: $body")

                val gson = GsonBuilder().create()

                val mainJson = gson.fromJson(body, Respond::class.java)

                runOnUiThread {
                    recyclerView.adapter = MiniAdapter(mainJson, this@YoutubePlayerActivity)
                    m_shimmer_view_container.visibility = View.GONE
                }

                Log.d(TAG, "mainJson: $mainJson  ")

            }

        })

    }

    override fun onClick(position: Int, snippet: Snippet?) {
        player?.cueVideo(snippet?.resourceId?.videoId)
        player?.play()
    }

}