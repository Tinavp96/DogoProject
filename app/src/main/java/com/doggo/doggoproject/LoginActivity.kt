package com.doggo.doggoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var mLogin: Button? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mLogin = findViewById<View>(R.id.login) as Button
        mEmail = findViewById<View>(R.id.email) as EditText
        mPassword = findViewById<View>(R.id.password) as EditText
        mLogin!!.setOnClickListener {
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this@LoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "sign in error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)
    }

}
