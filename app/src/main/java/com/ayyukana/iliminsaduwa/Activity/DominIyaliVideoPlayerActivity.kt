package com.ayyukana.iliminsaduwa.Activity

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayyukana.iliminsaduwa.Adapters.DominIyaliMiniAdapter
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.`interface`.OnItemClickListener
import com.ayyukana.iliminsaduwa.model.Constant
import com.ayyukana.iliminsaduwa.model.DominIyaliModel
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_domin_iyali_video_player.*
import kotlinx.android.synthetic.main.player_controls.*

class DominIyaliVideoPlayerActivity : AppCompatActivity(), OnItemClickListener {

    private var player: SimpleExoPlayer? = null
    var flag = false
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private val dominIyaliModelArrayList = ArrayList<DominIyaliModel>()

    private val TAG = DominIyaliVideoPlayerActivity::class.java.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_domin_iyali_video_player)

        firebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = firebaseDatabase.reference.child("video")
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManager
    }

    override fun onStart() {
        super.onStart()

        mDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val dominIyaliModel = snapshot.getValue(DominIyaliModel::class.java)
                    dominIyaliModelArrayList.add(dominIyaliModel!!)
                    Log.d(TAG, "onDataChange: DataBaseLoader${dominIyaliModel.videoURL}")
                    val adapter = DominIyaliMiniAdapter(
                        dominIyaliModelArrayList,
                        this@DominIyaliVideoPlayerActivity
                    )
                    mRecyclerView.adapter = adapter
                    m_shimmer_view_container.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toException())
            }
        })

        val video = intent.getParcelableExtra<DominIyaliModel>(Constant.SELECTED_ITEMS)
        player = ExoPlayerFactory.newSimpleInstance(
            this@DominIyaliVideoPlayerActivity,
            DefaultTrackSelector()
        )
        player_view.player = player
        preparePlayer(video?.videoURL.toString())
        player?.playWhenReady = true
        player?.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                when (error.type) {
                    ExoPlaybackException.TYPE_SOURCE -> Log.e(
                        TAG,
                        "TYPE_SOURCE: " + error.sourceException.localizedMessage
                    )

                    ExoPlaybackException.TYPE_RENDERER -> Log.e(
                        TAG,
                        "TYPE_RENDERER: " + error.rendererException.localizedMessage
                    )
                    ExoPlaybackException.TYPE_UNEXPECTED -> Log.e(
                        TAG,
                        "TYPE_UNEXPECTED: " + error.unexpectedException.localizedMessage
                    )
                }
            }


            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        })

        exo_fullscreen.setOnClickListener {
            if (flag) {
                exo_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DominIyaliVideoPlayerActivity,
                        R.drawable.exo_controls_fullscreen_enter
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag = false
                player_view.layoutParams.height = 500

            } else {
                exo_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DominIyaliVideoPlayerActivity,
                        R.drawable.exo_controls_fullscreen_exit
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                player_view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                flag = true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.playWhenReady = false
        player?.release()
        player?.playbackState
    }

    override fun onItemClick(position: Int) {
        val video = dominIyaliModelArrayList[position]
        preparePlayer(video.videoURL.toString())
        player?.playWhenReady = true
    }


    fun preparePlayer(uri: String) {
        val dataSourceFactory =
            DefaultDataSourceFactory(this@DominIyaliVideoPlayerActivity, "ExoPlayer")
        val mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                Uri.parse(uri)
            )
        player?.prepare(mediaSource)
    }

}