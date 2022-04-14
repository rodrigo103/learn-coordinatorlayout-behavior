package com.example.learn.business

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.learn.R
import com.example.learn.log
import kotlinx.android.synthetic.main.merchant_activity.*

@SuppressLint("Registered")
class MerchantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.merchant_activity)
        initialView()
        log("MerchantActivity onCreate")
    }

    private fun initialView() {
        layContent.laySettle = laySettle
    }
}
