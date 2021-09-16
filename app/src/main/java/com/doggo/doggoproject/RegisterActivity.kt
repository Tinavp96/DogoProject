package com.doggo.doggoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {

    private var mRegister: Button? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mName: EditText? = null
    private var mRadioGroup: RadioGroup? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mRegister = findViewById<View>(R.id.letsgo) as Button
        mEmail = findViewById<View>(R.id.email) as EditText
        mPassword = findViewById<View>(R.id.password) as EditText
        mName = findViewById<View>(R.id.doggyName) as EditText
        mRadioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        mRegister!!.setOnClickListener(View.OnClickListener {
            val selectId = mRadioGroup!!.checkedRadioButtonId
            val radioButton = findViewById<View>(selectId) as RadioButton
            if (radioButton.text == null) {
                return@OnClickListener
            }
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            val name = mName!!.text.toString()
            mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "sign up error", Toast.LENGTH_SHORT).show()
                }
                else {
                    val userId = mAuth?.currentUser?.uid
                    val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId.toString())
                    operator fun <K, V> MutableMap<K, V>.set(v: String, value: String) {
                        val userInfo: MutableMap<*, *> = HashMap<Any?, Any?>()
                        userInfo.set("name", name)
                        userInfo.set("sex", radioButton.text.toString())
                        userInfo.set("profileImageUrl", "default")
                        currentUserDb.updateChildren(userInfo as MutableMap<String, Any>)
                    }
                }
            }
            fun onStart() {
                super.onStart()
                mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
            }

            fun onStop() {
                super.onStop()
                mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)

            }
        })

    }
}