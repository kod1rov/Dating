package com.devcraft.ratingapp.ui.fragments.hotOrNot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.Utils.Utils
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.activity.match.MatchActivity
import com.devcraft.ratingapp.ui.fragments.profile.ProfileFragment
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_hot_or_not.*
import kotlinx.android.synthetic.main.fragment_hot_or_not.view.*

class HotOrNotFragment : Fragment() {
    private lateinit var firebaseVM : FirebaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_hot_or_not, container, false)
        firebaseVM = ViewModelProvider(activity as BNVActivity)[FirebaseViewModel::class.java]
        initListenrs(v)
        return v
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var potentialMatchObserver = firebaseVM.potentialMatch.observe(viewLifecycleOwner){
            if(it != null){
                if(!firebaseVM.user.value?.not?.contains(it.uid)!! && !firebaseVM.user.value?.hot?.contains(it.uid)!!){

                    Picasso.get().load(it.gallery?.get(0)).fit().centerCrop().into(picture_hotOrNot)
                    name_u.text = it.name +  ", " + it.bornDate?.let { Utils.getAge(it) }
                    user.visibility = View.VISIBLE
                    if(it.online != true){
                        indicator.visibility = View.GONE
                    }
                }
                else {
                    if(firebaseVM.potentials.count() > 0){
                        firebaseVM.potentials.removeAt(0)

                    }
                    if(firebaseVM.potentials.count() > 0){
                        firebaseVM.removeListenerUser()
                        firebaseVM.setListenerUser(firebaseVM.potentials[0])
                    }
                    else{
                        firebaseVM.removeListenerUser()
                    }
                }
            }else if(firebaseVM.potentials.count() == 0){
                user.visibility = View.GONE
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        firebaseVM.listenerUserList?.remove()
    }

    private fun initListenrs(v: View) {
        v.btn_hot.setOnClickListener {
            hotEvent()
        }
        v.btn_not.setOnClickListener {
            notEvent()
        }
        v.user.setOnClickListener {
            (activity as BNVActivity).addFragment(ProfileFragment(false), "self", true)
        }
    }

    private fun notEvent() {
        firebaseVM.potentialMatch.value?.uid?.let { it1 -> firebaseVM.setResult("not", it1) }
        nextUser()
    }

    private fun hotEvent() {
        firebaseVM.potentialMatch.value?.uid?.let { it1 -> firebaseVM.setResult("hot", it1) }
        if(firebaseVM.potentialMatch.value?.hot?.contains(firebaseVM.uid) == true){
            val intent = Intent(requireContext(), MatchActivity::class.java)
            intent.putExtra("imgs", bundleOf(
                "imgs" to firebaseVM.potentialMatch.value?.gallery,
                "name" to firebaseVM.potentialMatch.value?.name,
                "uid" to firebaseVM.potentialMatch.value?.uid
            ))
            startActivity(intent)
        }

            nextUser()
    }

    private fun nextUser(){
        if(firebaseVM.potentials.count() >0){
            firebaseVM.potentials.removeAt(0)
            firebaseVM.removeListenerUser()

        }

        if(firebaseVM.potentials.count() > 0){
            firebaseVM.setListenerUser(firebaseVM.potentials[0])
        }else{
            user.visibility = View.GONE
        }
    }
}