package com.devcraft.ratingapp.ui.fragments.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.Utils.Utils
import com.devcraft.ratingapp.ui.activity.ProfileEditActivity
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.fragments.profile.adapters.ViewPagerAdapter
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment(
    private val self: Boolean = true
) : Fragment() {

    private lateinit var firebaseVM: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseVM = ViewModelProvider(activity as BNVActivity)[FirebaseViewModel::class.java]

        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        if (!self){
            v.fab.visibility = View.GONE
            initViewsPotentialUser(v)
        }else{
            initViews(v)
            v.fab.setOnClickListener {
                startActivity(Intent(v.context, ProfileEditActivity::class.java))
            }
        }
        return v
    }

    @SuppressLint("SetTextI18n")
    private fun initViewsPotentialUser(v: View) {
        firebaseVM.potentialMatch.observeForever { user ->
            try {
                v.pager_photo.adapter =
                    user.gallery?.let { item -> ViewPagerAdapter(this, item) }
                TabLayoutMediator(v.tab_layout, v.pager_photo) { _, _ -> }.attach()

                v.name_and_age.text = user.name + ", " + user.bornDate?.let { Utils.getAge(it) }
                v.description.text = user.desc
                v.gender.text = user.gender
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(v: View) {
        firebaseVM.user.observeForever { user ->
            try {
                v.pager_photo.adapter =
                    user.gallery?.let { item -> ViewPagerAdapter(this, item) }
                TabLayoutMediator(v.tab_layout, v.pager_photo) { _, _ -> }.attach()

                v.name_and_age.text = user.name + ", " + user.bornDate?.let { Utils.getAge(it) }
                v.description.text = user.desc
                v.gender.text = user.gender
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}
