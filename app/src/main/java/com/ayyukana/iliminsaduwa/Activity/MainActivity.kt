package com.ayyukana.iliminsaduwa.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayyukana.iliminsaduwa.Adapters.YoutubeAdapter
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.Respond.Respond
import com.ayyukana.iliminsaduwa.Respond.Snippet
import com.ayyukana.iliminsaduwa.model.Constant
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.rommansabbir.networkx.NetworkX
import com.rommansabbir.networkx.NetworkXObservingStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.no_internet_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    YoutubeAdapter.OnItemClickListener {

    companion object {
        val TAG = MainActivity::class.java.name
        lateinit var shimmerFrameLayout: ShimmerFrameLayout
        lateinit var mRecyclerView: RecyclerView
    }

    lateinit var auth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkX.startObserving(this.application, NetworkXObservingStrategy.HIGH)
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {

            val header = nav_view.getHeaderView(0)
            userEmail = auth.currentUser!!.email.toString()

            header.navHeaderUserEmail.text = userEmail
        }

        setSupportActionBar(toolbar)

        val taggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        taggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        shimmerFrameLayout = shimmer_view_container
        mRecyclerView = recyclerView

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = layoutManager

        NetworkX.isConnected().let {
            when (it) {
                true -> {
                    fetchJson()
                }

                else -> {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    noInternetLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    shimmer_view_container.visibility = View.GONE
                }
            }

        }

        btnTryAgain.setOnClickListener {
            val intent = intent
            finish()
            startActivity(intent)
        }

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
                    recyclerView.adapter = YoutubeAdapter(mainJson, this@MainActivity)
                    shimmer_view_container.visibility = View.GONE
                }
                Log.d(TAG, "mainJson: $mainJson  ")

            }

        })

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_drawer_domin_iyali -> startActivity(
                Intent(
                    this,
                    DominIyaliActivity::class.java
                )
            )
        }
        return true
    }

    override fun onItemclick(position: Int, snippet: Snippet?) {
        val intent = Intent(this, YoutubePlayerActivity::class.java)
        intent.putExtra(Constant.SELECTED_ITEMS, snippet?.resourceId?.videoId)
        startActivity(intent)
    }

}

