package com.devcraft.ratingapp.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.MainActivity
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.anim.Anim
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.*
import kotlinx.android.synthetic.main.fragment_sign_in.view.btn_back
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignInFragment : Fragment() {
    private lateinit var FirebaseUpVM: FirebaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseUpVM = ViewModelProvider(activity as MainActivity)[FirebaseViewModel::class.java]
        val v = inflater.inflate(R.layout.fragment_sign_in, container, false)

        initListeners(v)

        return v
    }

    private fun initListeners(v: View) {
        v.btn_sign_in.setOnClickListener {
            if (v.emailSignIn.text.toString() == "" &&
                v.passSignIn.text.toString() == "") {
                Toast.makeText(requireContext(), "Fill in all the fields", Toast.LENGTH_SHORT)
                    .show()
            }else{
                FirebaseUpVM.auth.signInWithEmailAndPassword(emailSignIn.text.toString(),passSignIn.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val activity = activity as MainActivity
                        activity.finish()
                        startActivity(Intent(requireContext(), BNVActivity::class.java))
                    }
                }
            }
        }
        v.btn_back.setOnClickListener {
            Navigation.findNavController(v)
                .navigate(R.id.action_signInFragment_to_selectAuthFragment, null, Anim().animForNav())
        }
    }
}