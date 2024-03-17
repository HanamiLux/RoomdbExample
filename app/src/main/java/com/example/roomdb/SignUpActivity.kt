package com.example.roomdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.roomdb.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            GoToSignInBtn.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }
            SignUpBtn.setOnClickListener {
                val db: AppDatabase = AppDatabase.getDbInstance(applicationContext)
                val user = db.usersDao().getUser(binding.LoginET.text.toString())
                if (user != null
                    || binding.LoginET.text.toString().isEmpty()
                    || binding.PasswordET.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Неа) Такое не добавляем!",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                db.usersDao().insertUser(
                    User(
                        null,
                        binding.LoginET.text.toString(),
                        binding.PasswordET.text.toString()
                    )
                )
                Toast.makeText(
                    this@SignUpActivity,
                    "Welcome to app, ${LoginET.text.toString()}!",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }
        }

    }
}