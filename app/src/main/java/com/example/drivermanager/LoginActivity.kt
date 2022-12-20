package com.example.drivermanager

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    lateinit var driverService: DriverServiceImpl
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "배달원 현황 로그인"

        val txtId = findViewById<EditText>(R.id.txtId)
        val txtPass = findViewById<EditText>(R.id.txePass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        driverService = DriverServiceImpl(this)

        btnLogin.setOnClickListener {
            // 로그인 처리
            id = txtId.text.toString()
            val pw = txtPass.text.toString()
            driverService.getToken(id, pw) { token ->
                checkToken(token)
            }

        }
    }

    fun checkToken(token : String?) {
        println("getToken 4: " + token)
        if (token == null) {
            Toast.makeText(this, "아이디 혹은 암호가 다릅니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 로그인 토큰 반환
            val outIntent = Intent(applicationContext, MainActivity::class.java)
            outIntent.putExtra(getString(R.string.login_token), token)
            setResult(Activity.RESULT_OK, outIntent)
            finish()
        }
    }

}