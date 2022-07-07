package com.devcraft.ratingapp.ui.activity.match

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.data.models.Message
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_match.*

class MatchActivity : AppCompatActivity() {

    private lateinit var imgs : MutableList<*>
    private lateinit var nameU : String
    private lateinit var uid : String
    private lateinit var FirebaseVM : FirebaseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomTheme()
        setContentView(R.layout.activity_match)
        FirebaseVM = ViewModelProvider(this)[FirebaseViewModel::class.java]
        //get bundle data(images for match)
        imgs = intent.getBundleExtra("imgs")?.get("imgs") as MutableList<*>
        //get bundle data(name user for match)
        nameU = intent.getBundleExtra("imgs")?.get("name") as String
        uid = intent.getBundleExtra("imgs")?.get("uid") as String
        initListeners()
        setData()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        if(imgs.count()>0){
            Picasso.get().load(imgs[0].toString()).placeholder(R.drawable.test_photo).fit().into(third_picture)
        }
        if(imgs.count()>1){
            Picasso.get().load(imgs[1].toString()).placeholder(R.drawable.test_photo).fit().into(second_picture)
        }
        if(imgs.count()>2){
            Picasso.get().load(imgs[2].toString()).placeholder(R.drawable.test_photo).fit().into(main_picture)
        }
        match_name_u.text = "$nameU likes you too"
    }

    private fun initListeners() {
        btn_kepp.setOnClickListener {
            finish()
        }
        btn_send_message.setOnClickListener {
            FirebaseVM.sendMessage(uid, Message(senderId = uid,text = first_message.text.toString(),timestamp = ServerValue.TIMESTAMP)){
                finish()
            }
        }
        btn_kepp.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun setCustomTheme(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.orange_0)
    }
}