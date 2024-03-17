package com.example.roomdb

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.example.roomdb.databinding.ActivityMainBinding


class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (preferences.getString(R.string.UserLogin.toString(), "") != ""){
            startActivity(Intent(this, FlowActivity::class.java))
            finish()
        }

        binding.apply {
            SignInBtn.setOnClickListener{
                val db: AppDatabase = AppDatabase.getDbInstance(applicationContext)
                if(LoginET.text.isEmpty() || PwdET.text.isEmpty()){
                    Toast.makeText(this@SignInActivity, "Empty fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val user: User = db.usersDao().getUser("${LoginET.text}")
                if(user == null || user.password != PwdET.text.toString()){
                    Toast.makeText(this@SignInActivity, "Incorrect data!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@SignInActivity)
                sharedPreferences.edit().apply {
                    putString(R.string.UserLogin.toString(), LoginET.text.toString())
                    putString(R.string.UserPassword.toString(), PwdET.text.toString())
                    apply()
                }
                startActivity(Intent(this@SignInActivity, FlowActivity::class.java))
                finish()
            }
            GoToSignUpBtn.setOnClickListener{
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }
        }

    }
}