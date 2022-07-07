package com.devcraft.ratingapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.firebase.firestore.ktx.firestoreSettings

class MainActivity : AppCompatActivity() {

    lateinit var FirebaseVM : FirebaseViewModel
    lateinit var Fi : FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setWhiteThemeStatusBar(this)
        setContentView(R.layout.select_auth_activity)


        FirebaseVM = ViewModelProvider(this)[FirebaseViewModel::class.java]

        checkAuthUser()

    }

    private fun checkAuthUser() {
        if(FirebaseVM.auth.currentUser != null){
            startActivity(Intent(this, BNVActivity::class.java))
            finish()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setWhiteThemeStatusBar(mainActivity: MainActivity) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = mainActivity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = mainActivity.resources.getColor(R.color.white)
        }
    }
}