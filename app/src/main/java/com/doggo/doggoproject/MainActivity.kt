package com.doggo.doggoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.doggo.doggoproject.Cards.arrayAdapter
import com.doggo.doggoproject.Cards.cards
import com.doggo.doggoproject.Matches.MatchesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private val cards_data: Array<cards>? = null
    private var arrayAdapter: arrayAdapter? = null
    private val i = 0
    private var mAuth: FirebaseAuth? = null
    private var currentUId: String? = null
    private var usersDb: DatabaseReference? = null
    var listView: ListView? = null
    var rowItems: MutableList<cards>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        currentUId = mAuth?.getCurrentUser()!!.uid
        checkUserSex()
        rowItems = ArrayList()
        arrayAdapter = arrayAdapter(this, R.layout.item, rowItems)
        val flingContainer = findViewById<View>(R.id.frame) as SwipeFlingAdapterView
        flingContainer.adapter = arrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                rowItems?.removeAt(0)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                val obj = dataObject as cards
                val userId = obj.userId
                usersDb?.child(userId)?.child("connections")?.child("nope")?.child(currentUId!!)?.setValue(true)
                Toast.makeText(this@MainActivity, "Left", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                val obj = dataObject as cards
                val userId = obj.userId
                usersDb?.child(userId)?.child("connections")?.child("yeps")?.child(currentUId!!)?.setValue(true)
                isConnectionMatch(userId)
                Toast.makeText(this@MainActivity, "Right", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(scrollProgressPercent: Float) {}
        })


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener { itemPosition, dataObject -> Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show() }
    }

    private fun isConnectionMatch(userId: String) {
        val currentUserConnectionsDb = currentUId?.let { usersDb!!.child(it).child("connections").child("yeps").child(userId) }
        currentUserConnectionsDb?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@MainActivity, "new Connection", Toast.LENGTH_LONG).show()
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    dataSnapshot.key?.let { currentUId?.let { it1 -> usersDb!!.child(it).child("connections").child("matches").child(it1).child("ChatId").setValue(key) } }
                    currentUId?.let { dataSnapshot.key?.let { it1 -> usersDb!!.child(it).child("connections").child("matches").child(it1).child("ChatId").setValue(key) } }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var userSex: String? = null
    private var oppositeUserSex: String? = null
    fun checkUserSex() {
        val user = FirebaseAuth.getInstance().currentUser
        val userDb = usersDb!!.child(user!!.uid)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").value != null) {
                        userSex = dataSnapshot.child("sex").value.toString()
                        when (userSex) {
                            "Male" -> oppositeUserSex = "Female"
                            "Female" -> oppositeUserSex = "Male"
                        }
                        oppositeSexUsers
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    val oppositeSexUsers: Unit
        get() {
            usersDb!!.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    if (dataSnapshot.child("sex").value != null) {
                        if (dataSnapshot.exists() && !currentUId?.let { dataSnapshot.child("connections").child("nope").hasChild(it) }!! && !currentUId?.let {
                                dataSnapshot.child("connections").child("yeps").hasChild(
                                    it
                                )
                            }!! && dataSnapshot.child("sex").value.toString() == oppositeUserSex) {
                            var profileImageUrl = "default"
                            if (dataSnapshot.child("profileImageUrl").value != "default") {
                                profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                            }
                            val item = dataSnapshot.key?.let { cards(it, dataSnapshot.child("name").value.toString(), profileImageUrl) }
                            if (item != null) {
                                rowItems!!.add(item)
                            }
                            arrayAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    fun logoutUser(view: View?) {
        mAuth!!.signOut()
        val intent = Intent(this@MainActivity, ChooseLoginRegisterActivity::class.java)
        startActivity(intent)
        finish()
        return
    }


    fun goToMatches(view: View?) {
        val intent = Intent(this@MainActivity, MatchesActivity::class.java)
        startActivity(intent)
        return
    }
}

