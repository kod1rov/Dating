package com.devcraft.ratingapp.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.Utils.Utils
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.data.models.DataReg
import com.devcraft.ratingapp.ui.data.models.User
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.activity_profile_edit.btn_continue
import kotlinx.android.synthetic.main.activity_profile_edit.first_img
import kotlinx.android.synthetic.main.activity_profile_edit.gender_t_f
import kotlinx.android.synthetic.main.activity_profile_edit.input_about
import kotlinx.android.synthetic.main.activity_profile_edit.main_img
import kotlinx.android.synthetic.main.activity_profile_edit.name
import kotlinx.android.synthetic.main.activity_profile_edit.second_img
import kotlinx.android.synthetic.main.activity_profile_edit.third_img
import kotlinx.android.synthetic.main.fragment_hot_or_not.*
import kotlinx.android.synthetic.main.fragment_profile_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileEditActivity : AppCompatActivity() {

    private var userChangeData = User()
    private lateinit var firebaseVM: FirebaseViewModel
    private var listUriForAddPhoto: MutableLiveData<ArrayList<Uri>> = MutableLiveData(arrayListOf())
    private var UID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        firebaseVM = ViewModelProvider(this)[FirebaseViewModel::class.java]

        initViews()
        initListeners()
    }

    private fun initViews() {
        getDataU()
        addPhoto()
    }

    private fun initListeners() {
        btn_continue.setOnClickListener {
            updateData()
        }
        btn_image_picker_change.setOnClickListener {
            eventPickImg()
        }
        btn_picker_born_date.setOnClickListener {
            selectDateBorn()
        }

    }

    private fun updateData() {
        fun update(){
            firebaseVM.db.collection("users").document(UID).update(
                "name", name.text.toString(),
                "bornDate", userChangeData.bornDate,
                "desc", input_about.text.toString(),
                "gender", gender_t_f.text.toString(),
                "gallery", userChangeData.gallery
            ).addOnCompleteListener {
                if(it.isCanceled){
                    println(it.exception)
                }
                if (it.isSuccessful){
                    finish()
                }
            }
        }
        if (listUriForAddPhoto.value?.count()!! > 0){
            val storageRef = firebaseVM.storage.reference.child("users")
            listUriForAddPhoto.value?.forEach {uri->
                val uid = UUID.randomUUID().toString().replace("-","")
                storageRef.child(firebaseVM.auth.currentUser!!.uid +"/"+uid+".jpg").putFile(uri).addOnCompleteListener{ it ->
                    if(it.isSuccessful){
                        storageRef.child(firebaseVM.auth.currentUser!!.uid+"/"+uid+".jpg").downloadUrl.addOnSuccessListener { urlImg ->
                           val index = listUriForAddPhoto.value!!.indexOf(uri)
                            if (userChangeData.gallery?.size == index) {
                                userChangeData.gallery!!.add(urlImg.toString())
                            }else{
                                userChangeData.gallery?.set(index, urlImg.toString())
                            }
                            if(index == listUriForAddPhoto.value!!.size-1){
                                update()
                            }
                        }
                    }
                }
            }
        }
        else{
            update()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            data?.data?.let { listUriForAddPhoto.value?.add(it) }
            listUriForAddPhoto.value = listUriForAddPhoto.value
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
    private fun getDataU() {

        firebaseVM.user.observeForever { user ->
            try {
                val formatter = SimpleDateFormat("dd/M/yyyy")

                name.setText(user.name)
                gender_t_f.setText(user.gender)
                input_about.setText(user.desc)
                UID = user.uid.toString()
                date_born_change.setText(formatter.format(user.bornDate).toString().replace("/", "."))

                if (user.gallery!!.count() > 0 ){
                    Picasso.get().load(user.gallery!![0]).fit().into(main_img_change)
                }
                if (user.gallery!!.count() > 1 ){
                    Picasso.get().load(user.gallery!![1]).fit().into(first_img_change)
                }
                if (user.gallery!!.count() > 2 ){
                    Picasso.get().load(user.gallery!![3]).fit().into(second_img_change)
                }
                if (user.gallery!!.count() > 3 ){
                    Picasso.get().load(user.gallery!![3]).fit().into(third_img_change)
                }

                userChangeData = user
                println(userChangeData)

            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun selectDateBorn() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("SELECT DATE")
        val materialDatePicker = builder.build()
        materialDatePicker.show(supportFragmentManager,
            "DATE_PICKER"
        )

        materialDatePicker.addOnPositiveButtonClickListener { selection ->
            val formatter = SimpleDateFormat("dd/M/yyyy")
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = selection as Long
            date_born_change.setText(formatter.format(calendar.time).toString().replace("/", "."))
            userChangeData.bornDate = calendar.timeInMillis
        }
    }

    private fun addPhoto() {
        listUriForAddPhoto.observeForever { list ->
            list.forEach {
                if (list.indexOf(it) == 0) {
                    Picasso.get().load(it).fit().into(main_img_change)
                }
                if (list.indexOf(it) == 1) {
                    Picasso.get().load(it).fit().into(first_img_change)
                }
                if (list.indexOf(it) == 2) {
                    Picasso.get().load(it).fit().into(second_img_change)
                }
                if (list.indexOf(it) == 3) {
                    Picasso.get().load(it).fit().into(third_img_change)
                }
            }
        }
    }

}