package com.example.learn.business

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.learn.R
import com.example.learn.databinding.MerchantSettleLayoutBinding
import com.example.learn.dp
import com.example.learn.view.stateRefresh
import com.example.learn.view.stateSave
import com.example.learn.view.statesChangeByAnimation

class MerchantSettleLayout(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var firstLayout: Boolean = false
    private var isExpanded = false // Whether the content of layContent is being expanded and viewed
    private var effected: Float = 0f

    init {
        MerchantSettleLayoutBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun animViews(): Array<View> = arrayOf(this)

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!firstLayout) {
            firstLayout = true
            stateSave(R.id.vs1).a(1F)
            stateSave(R.id.vs2).a(0F)
        }
    }

    // The effect is simple to achieve, and the specific content should dynamically calculate the change height interval according to the business.
    fun effectByOffset(transY: Float) {
        effected = when {
            transY <= dp(110) -> 0F
            transY > dp(110) && transY < dp(140) -> (transY - dp(110)) / dp(30)
            else -> 1F
        }
        animViews().forEach { it.stateRefresh(R.id.vs1, R.id.vs2, effected) }
    }

    fun switch(expanded: Boolean) {
        if (isExpanded == expanded) {
            return
        }

        isExpanded = expanded // Target
        val start = effected
        val end = if (expanded) 1F else 0F
        statesChangeByAnimation(animViews(), R.id.vs1, R.id.vs2, start, end,
                null, object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (isExpanded) visibility = View.INVISIBLE
            }
        }, 300)
    }
}