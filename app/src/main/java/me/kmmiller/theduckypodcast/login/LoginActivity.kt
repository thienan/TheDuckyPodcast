package me.kmmiller.theduckypodcast.login

import android.content.Intent
import android.os.Bundle
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.main.MainActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser != null)
            logIn()
        else
            pushFragment(LoginFragment(), true, false, LoginFragment.TAG)
    }

    fun logIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }
}