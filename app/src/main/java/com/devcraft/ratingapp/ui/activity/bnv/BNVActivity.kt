package com.devcraft.ratingapp.ui.activity.bnv

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.fragments.chat.ChatFragment
import com.devcraft.ratingapp.ui.fragments.hotOrNot.HotOrNotFragment
import com.devcraft.ratingapp.ui.fragments.profile.ProfileFragment
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import kotlinx.android.synthetic.main.activity_bnvactivity.*

class BNVActivity : AppCompatActivity() {

    private val fragments: List<Fragment> = listOf(
        HotOrNotFragment(),
        ChatFragment(),
        ProfileFragment(true)
    )
    private val frg = supportFragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var FirebaseVM: FirebaseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setWhiteThemeStatusBar(this)
        setContentView(R.layout.activity_bnvactivity)

        FirebaseVM = ViewModelProvider(this)[FirebaseViewModel::class.java]

        replaceFragment(fragments[0])

        FirebaseVM.user.observe(this) {
            if (it.name != null) {
                FirebaseVM.setUserListListener()
            }

        }

        if (!FirebaseVM.userss.hasObservers()) {
            FirebaseVM.userss.observe(this) {
                if (FirebaseVM.potentials.count() > 0) {

                    FirebaseVM.setListenerUser(FirebaseVM.potentials[0])
                }
            }
        }

        initListeners()
    }

    private fun initListeners() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.hotOrNotFragment -> {
                    addFragment(fragments[0], "hot_or_not")
                    true
                }
                R.id.chatFragment -> {
                    addFragment(fragments[1], "chat")
                    true
                }
                R.id.profileFragment -> {
                    addFragment(fragments[2], "profile")
                    true
                }
                else -> false
            }
        }
    }

    fun addFragment(frgActive: Fragment, tag: String, toStack: Boolean = true) {

        fragmentTransaction =
            frg.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.replace(R.id.fragment_view, frgActive)

        if (toStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
    }

    fun replaceFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_view, fragment)
            commit()
        }

    @SuppressLint("ObsoleteSdkInt")
    private fun setWhiteThemeStatusBar(mainActivity: BNVActivity) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = mainActivity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = mainActivity.resources.getColor(R.color.white)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseVM.setStatus(false)
    }

    override fun onPause() {
        super.onPause()
        FirebaseVM.setStatus(false)
        FirebaseVM.removeListenersChats()
    }

    override fun onResume() {
        super.onResume()
        FirebaseVM.setStatus(true)
        FirebaseVM.setlistenersChats()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (frg.backStackEntryCount > 0) {
            frg.popBackStack();
        } else {
            finish()
        }
        //finish()
    }
}