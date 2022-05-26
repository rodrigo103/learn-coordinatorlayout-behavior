package com.example.learn.business

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.example.learn.R
import com.example.learn.databinding.TicketViewBinding
import com.example.learn.view.AnimationUpdateListener
import com.example.learn.view.stateRefresh
import com.example.learn.view.stateSave
import com.example.learn.text

class TicketView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs), AnimationUpdateListener {

    private var firstLayout: Boolean = false

    private var binding: TicketViewBinding =
        TicketViewBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!firstLayout) {
            firstLayout = true

            binding.apply {
                vBorder1.stateSave(R.id.vs1).a(1F)
                vBorder1.stateSave(R.id.vs2).ws(3.8F).hs(3.8F).a(0F)
                vBorder2.stateSave(R.id.vs1).a(0F)
                vBorder2.stateSave(R.id.vs2).ws(3.8F).hs(3.8F).a(1F)
                vSimple.stateSave(R.id.vs1)
                vSimple.stateSave(R.id.vs2).a(0f)
                layDetail.stateSave(R.id.vs1)
                layDetail.stateSave(R.id.vs2).sx(1F).sy(1F).a(1F)
            }
        }
    }

    fun set(amount: Int, limit: Int, expireTime: String) {
        binding.apply {
            vSimple.text("领￥$amount")

            vDetail1.text("￥$amount")
            vDetail2.text("满$limit 可用")
            vDetail3.text("有效期至$expireTime")
        }
    }

    override fun onAnimationUpdate(tag1: Int, tag2: Int, p: Float) {
        arrayOf(
            binding.vBorder1,
            binding.vBorder2,
            binding.vSimple,
            binding.layDetail
        ).forEach { it.stateRefresh(tag1, tag2, p) }
    }
}