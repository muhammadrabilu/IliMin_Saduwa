package com.ayyukana.iliminsaduwa.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayyukana.iliminsaduwa.Adapters.DominIyaliAdapter
import com.ayyukana.iliminsaduwa.R
import com.ayyukana.iliminsaduwa.`interface`.OnEditbtnClickListener
import com.ayyukana.iliminsaduwa.`interface`.OnItemClickListener
import com.ayyukana.iliminsaduwa.model.Constant
import com.ayyukana.iliminsaduwa.model.DominIyaliModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rommansabbir.networkx.NetworkX
import com.rommansabbir.networkx.NetworkXObservingStrategy
import kotlinx.android.synthetic.main.activity_domin_iyali.*
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.activity_main.shimmer_view_container
import kotlinx.android.synthetic.main.no_internet_layout.*
import kotlinx.android.synthetic.main.toolbar.*


class DominIyaliActivity : AppCompatActivity(), OnItemClickListener, OnEditbtnClickListener {

    private val TAG = DominIyaliActivity::class.java.name
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private val dominIyaliModelArrayList = ArrayList<DominIyaliModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_domin_iyali)
        NetworkX.startObserving(this.application, NetworkXObservingStrategy.HIGH)

        firebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = firebaseDatabase.reference.child("video")

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        NetworkX.isConnected().let {
            when (it) {
                true -> {
                    Toast.makeText(this, "Internet connection is good", Toast.LENGTH_SHORT).show()

                    mDatabaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val dominIyaliModel = snapshot.getValue(DominIyaliModel::class.java)
                                dominIyaliModelArrayList.add(dominIyaliModel!!)
                                Log.d(
                                    TAG,
                                    "onDataChange: DataBaseLoader${dominIyaliModel.videoURL}"
                                )
                                val adapter = DominIyaliAdapter(
                                    this@DominIyaliActivity,
                                    dominIyaliModelArrayList,
                                    this@DominIyaliActivity,
                                    this@DominIyaliActivity
                                )
                                recyclerView.layoutManager =
                                    LinearLayoutManager(this@DominIyaliActivity)
                                recyclerView.adapter = adapter
                                shimmer_view_container.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError.toException())
                        }
                    })
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

        if (auth.currentUser != null && auth.currentUser!!.email.equals("ayyukana@gmail.com")) {
            floating_action_button.visibility = View.VISIBLE
            floating_action_button.setOnClickListener {
                startActivity(Intent(this, UploadVideoActivity::class.java))
            }
        }

        // check for user if login or not
        signIn()
    }

    private fun signIn() {
        if (auth.currentUser == null) {
            finish()
            startActivity(Intent(this, LogInActivity::class.java))
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sign_out, menu)
        return true
    }

    override fun onItemClick(position: Int) {
        val video = dominIyaliModelArrayList[position]
        val intent = Intent(this, DominIyaliVideoPlayerActivity::class.java)
        intent.putExtra(Constant.SELECTED_ITEMS, video)
        startActivity(intent)
    }

    override fun onUpdateBtnClick(position: Int) {
        val video = dominIyaliModelArrayList[position]
        val intent = Intent(this, UploadVideoActivity::class.java)
        intent.putExtra(Constant.SELECTED_ITEMS, video)
        startActivity(intent)
    }

}