package com.example.learn.business

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.learn.databinding.MerchantPageCellLayoutBinding

class MerchantInfoLayout(context: Context) : FrameLayout(context), ScrollableViewProvider {

    private var binding: MerchantPageCellLayoutBinding =
        MerchantPageCellLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    override fun getScrollableView(): View {
        return binding.vRecycler
    }

    init {
        initialData()
    }

    private fun initialData() {
    }
}