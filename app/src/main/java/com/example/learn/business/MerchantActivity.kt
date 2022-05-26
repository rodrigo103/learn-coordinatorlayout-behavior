package com.example.learn.business

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.learn.databinding.MerchantActivityBinding
import com.example.learn.log

@SuppressLint("Registered")
class MerchantActivity : AppCompatActivity() {

    private lateinit var binding: MerchantActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MerchantActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        initialView()
        log("MerchantActivity onCreate")
    }

    private fun initialView() {
        binding.layContent.laySettle = binding.laySettle
    }
}
