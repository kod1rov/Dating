package com.devcraft.ratingapp.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.MainActivity
import com.devcraft.ratingapp.ui.anim.Anim
import com.devcraft.ratingapp.ui.fragments.profile.ProfileDetailsFragment
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import kotlinx.android.synthetic.main.fragment_profile_details.view.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignUpFragment : Fragment() {
    private lateinit var FirebaseUpVM: FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseUpVM = ViewModelProvider(activity as MainActivity)[FirebaseViewModel::class.java]

        val v = inflater.inflate(R.layout.fragment_sign_up, container, false)
        initListeners(v)

        return v
    }

    private fun initListeners(v: View) {
        v.btn_sign_up.setOnClickListener {
            if (v.email.text.toString() == "" &&
                v.password.text.toString() == "" && !v.check_box.isChecked
            ) {

                Toast.makeText(requireContext(), "Fill in all the fields", Toast.LENGTH_SHORT)
                    .show()

            } else if (!v.check_box.isChecked) {
                Toast.makeText(requireContext(), "Accept confidentiality agreements", Toast.LENGTH_SHORT)
                    .show()
            } else {
                findNavController()
                    .navigate(
                        R.id.action_signUpFragment_to_profileDetailsFragment,
                        bundleOf(
                            ProfileDetailsFragment.emailU to v.email.text.toString(),
                            ProfileDetailsFragment.passU to v.password.text.toString()
                        ),
                        Anim().animForNav()
                    )
            }
        }
        v.btn_back.setOnClickListener {
            Navigation.findNavController(v)
                .navigate(R.id.action_signUpFragment_to_selectAuthFragment, null, Anim().animForNav())
        }
    }
}