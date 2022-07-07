package com.devcraft.ratingapp.ui.fragments.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.MainActivity
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.data.Constants
import com.devcraft.ratingapp.ui.data.models.DataReg
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_details.*
import kotlinx.android.synthetic.main.fragment_profile_details.view.*
import kotlinx.android.synthetic.main.fragment_profile_details.view.drop_menu
import kotlinx.android.synthetic.main.fragment_profile_details.view.gender_t_f
import java.text.SimpleDateFormat
import java.util.*

class ProfileDetailsFragment : Fragment() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var firebaseVM: FirebaseViewModel
    private var listUriForAddPhoto: MutableLiveData<ArrayList<Uri>> = MutableLiveData(arrayListOf())
    private var user = DataReg()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if((activity is BNVActivity)){
            firebaseVM = ViewModelProvider(activity as BNVActivity)[FirebaseViewModel::class.java]

        }else{
            firebaseVM = ViewModelProvider(activity as MainActivity)[FirebaseViewModel::class.java]


        }

        val v = inflater.inflate(R.layout.fragment_profile_details, container, false)

        initListeners(v)
        initViews(v)

        return v
    }

    private fun addPhoto() {
        listUriForAddPhoto.observeForever {list ->
            list.forEach {
                if(list.indexOf(it) == 0){
                    Picasso.get().load(it).fit().into(main_img)
                }
                if(list.indexOf(it) == 1){
                    Picasso.get().load(it).fit().into(first_img)
                }
                if(list.indexOf(it) == 2){
                    Picasso.get().load(it).fit().into(second_img)
                }
                if(list.indexOf(it) == 3){
                    Picasso.get().load(it).fit().into(third_img)
                }
            }
        }
    }

    private fun initViews(v: View) {
        addPhoto()

        adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Constants.dataDropMenu
        )

        v.gender_t_f.setAdapter(adapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1){
            data?.data?.let { listUriForAddPhoto.value?.add(it) }
            listUriForAddPhoto.value = listUriForAddPhoto.value
        }
    }

    private fun initListeners(v: View) {
        v.btn_image_picker.setOnClickListener {
            eventPickImg()
        }

        v.date_born.setOnClickListener {
            selectDateBorn()
        }

        v.drop_menu.setOnClickListener {
            v.gender_t_f.showDropDown()
        }

        v.btn_p_born_date.setOnClickListener {
            selectDateBorn()
        }
        v.btn_continue.setOnClickListener {
            if (listUriForAddPhoto.value?.count() == 0 &&
                v.gender_t_f.text.toString() == "" &&
                v.name.text.toString() == "" &&
                v.input_about.text.toString() == "" &&
                v.date_born.text.toString() == ""){

                Toast.makeText(requireContext(), "Fill in all the fields", Toast.LENGTH_SHORT).show()

            }else{
                sendDataintoFirebase()
            }
        }
    }

    private fun sendDataintoFirebase() {
        firebaseVM.auth.createUserWithEmailAndPassword(
            arguments?.getString(emailU).toString(),
            arguments?.getString(passU).toString()
        ).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                user.email = firebaseVM.auth.currentUser!!.email!!
                user.gender = gender_t_f.text.toString()
                user.uid = firebaseVM.auth.currentUser!!.uid
                user.name = name.text.toString()
                user.desc = input_about.text.toString()
                firebaseVM.db.collection("users").document(firebaseVM.auth.currentUser?.uid!!)
                    .set(
                        user
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {

                            var batch = firebaseVM.db.batch()
                            val strgRef = firebaseVM.storage.reference.child("users")

                            listUriForAddPhoto.value?.forEach {uri->
                                val uid = UUID.randomUUID().toString().replace("-","")
                                strgRef.child(firebaseVM.auth.currentUser!!.uid +"/"+uid+".jpg").putFile(uri).addOnCompleteListener{ it ->
                                    if(it.isSuccessful){
                                        strgRef.child(firebaseVM.auth.currentUser!!.uid+"/"+uid+".jpg").downloadUrl.addOnSuccessListener {
                                            firebaseVM.db.collection("users").document(firebaseVM.auth.currentUser?.uid!!).update("gallery",FieldValue.arrayUnion(it.toString()))
                                                .addOnCompleteListener {
                                                    if(listUriForAddPhoto.value!!.indexOf(uri) == listUriForAddPhoto.value!!.count()-1){
                                                        val activity = activity as MainActivity
                                                        activity.finish()
                                                        startActivity(Intent(requireContext(), BNVActivity::class.java))
                                                    }
                                                }
                                        }
                                    }

                                }

                            }

                        } else if (it.isCanceled) {
                            println(it.exception!!.message)
                            firebaseVM.auth.currentUser!!.delete()
                        }
                    }
            }
            if(authTask.isCanceled){
                println(authTask.exception!!.message.toString())
            }
        }
    }

    private fun eventPickImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }


    @SuppressLint("SimpleDateFormat")
    private fun selectDateBorn() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("SELECT DATE")
        val materialDatePicker = builder.build()
        materialDatePicker.show(
            requireActivity().supportFragmentManager,
            "DATE_PICKER"
        )

        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            val formatter = SimpleDateFormat("dd/M/yyyy")
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = selection as Long
            date_born.setText(formatter.format(calendar.time).toString().replace("/", "."))
            user.bornDate = calendar.timeInMillis
        }
    }

    companion object {
        const val emailU = "EMAIL"
        const val passU = "PASS"
    }
}