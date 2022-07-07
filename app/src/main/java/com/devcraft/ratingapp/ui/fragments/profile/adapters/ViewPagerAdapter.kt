package com.devcraft.ratingapp.ui.fragments.profile.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devcraft.ratingapp.ui.fragments.profile.ProfileFragment


class ViewPagerAdapter(ctx: ProfileFragment, private val images: List<String>): FragmentStateAdapter(ctx) {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun createFragment(position: Int): Fragment = SliderFromProfile().apply {
        arguments = bundleOf(
            "url" to images[position]
        )}
}