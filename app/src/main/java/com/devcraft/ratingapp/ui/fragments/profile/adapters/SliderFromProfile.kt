package com.devcraft.ratingapp.ui.fragments.profile.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devcraft.ratingapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image_profile.*

class SliderFromProfile : Fragment() {
    lateinit var src : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            src = it.getString("url","0")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(src !="0"){
            Picasso.get().load(src).fit().into(picture)
        }
    }

}