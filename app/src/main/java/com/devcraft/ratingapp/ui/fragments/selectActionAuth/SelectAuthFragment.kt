package com.devcraft.ratingapp.ui.fragments.selectActionAuth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.anim.Anim
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_select_auth.view.*

class SelectAuthFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_select_auth, container, false)

        initListeners(v)

        return v
    }

    private fun initListeners(v: View) {
        v.btn_sign_up.setOnClickListener {
            Navigation.findNavController(v)
                .navigate(R.id.action_selectAuthFragment_to_signUpFragment, null, Anim().animForNav())
        }
        v.btn_sign_in.setOnClickListener {
            Navigation.findNavController(v)
                .navigate(R.id.action_selectAuthFragment_to_signInFragment, null, Anim().animForNav())
        }
    }
}