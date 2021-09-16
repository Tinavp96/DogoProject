package com.doggo.doggoproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class ChooseLoginRegisterActivity : AppCompatActivity() {

    private var mLogin: Button? = null
    private var mRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_register)
        mLogin = findViewById<View>(R.id.loginButton) as Button
        mRegister = findViewById<View>(R.id.registerButton) as Button
        mLogin!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
        mRegister!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
    }
}
