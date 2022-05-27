package com.example.learn.business

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.Scroller
import com.example.learn.R
import com.example.learn.argbEvaluator
import com.example.learn.dp
import com.example.learn.view.ViewPager2
import kotlin.math.abs

class MerchantPageBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<MerchantPageLayout>(context, attrs) {
    private lateinit var selfView: MerchantPageLayout
    private lateinit var layTitle: MerchantTitleLayout // store title
    private lateinit var vPager: ViewPager2 // The pager where the product menu is located
    private lateinit var layContent: MerchantContentLayout // store details
    private lateinit var laySettle: MerchantSettleLayout
    private val pagingTouchSlop = dp(5)
    private var horizontalPagingTouch = 0 // The touch sliding distance of the menu horizontal item list (recommended products) content
    private var isScrollRecommends = false
    private var verticalPagingTouch = 0 // The touch sliding distance of the menu vertical item list (products, reviews, businesses) content
    private var simpleTopDistance = 0
    private var isScrollToFullFood = false // Scroll up to display the product menu
    private var isScrollToHideFood = false // scroll down to show store details
    private val scroller = Scroller(context)
    private val scrollDuration = 800
    private val handler = android.os.Handler()
    private val flingRunnable = object : Runnable {
        override fun run() {
            if (scroller.computeScrollOffset()) {
                selfView.translationY = scroller.currY.toFloat()
                layContent.effectByOffset(selfView.translationY)
                laySettle.effectByOffset(selfView.translationY)
                handler.post(this)
            } else {
                isScrollToHideFood = false
            }
        }
    }

    private val mAnimListener = object : MerchantContentLayout.AnimatorListenerAdapter1 {
        override fun onAnimationStart(animation: Animator?, toExpanded: Boolean) {
            if (toExpanded) {
                val defaultDisplayHeight = (selfView.height - simpleTopDistance)
                scroller.startScroll(0, selfView.translationY.toInt(), 0, (defaultDisplayHeight - selfView.translationY).toInt(), scrollDuration)
            } else {
                scroller.startScroll(0, selfView.translationY.toInt(), 0, (-selfView.translationY).toInt(), scrollDuration)
            }

            handler.post(flingRunnable)
            isScrollToHideFood = true
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: MerchantPageLayout, layoutDirection: Int): Boolean {
        selfView = child
        vPager = child.binding.vPager

        val lp = selfView.layoutParams as CoordinatorLayout.LayoutParams
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            simpleTopDistance = lp.topMargin - layTitle.height
            lp.height = parent.height - layTitle.height
            child.layoutParams = lp
            return true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: MerchantPageLayout, dependency: View): Boolean {
        when (dependency.id) {
            R.id.layTitle -> layTitle = dependency as MerchantTitleLayout
            R.id.layContent -> layContent = (dependency as MerchantContentLayout).apply { animListener = mAnimListener }
            R.id.laySettle -> laySettle = dependency as MerchantSettleLayout
            else -> return false
        }
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: MerchantPageLayout, dependency: View): Boolean = true
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: MerchantPageLayout, directTargetChild: View, target: View, axes: Int, type: Int): Boolean = true

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: MerchantPageLayout, directTargetChild: View, target: View, axes: Int, type: Int) {
        scroller.abortAnimation()
        isScrollToHideFood = false
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: MerchantPageLayout, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (isScrollToHideFood) {
            consumed[1] = dy
            return // scroller 滑动中.. do nothing
        }

        verticalPagingTouch += dy
        if (vPager.isScrollable && abs(verticalPagingTouch) > pagingTouchSlop) {
            vPager.isScrollable = false // 屏蔽 pager横向滑动干扰
        }

        horizontalPagingTouch += dx
        if (R.id.vRecommends == target.id) {
            if (!isScrollRecommends) {
                consumed[0] = dx
                if (vPager.isScrollable && abs(horizontalPagingTouch) > pagingTouchSlop) isScrollRecommends = true
            }
            if (isScrollRecommends) {
                consumed[1] = dy
                return // 横项滑动推荐列表中
            }
        }

        if ((child.translationY < 0 || (child.translationY == 0F && dy > 0)) && !child.canScrollVertically()) {
            val effect = layTitle.effectByOffset(dy)
            selfView.binding.vSmartTab.setBackgroundColor(argbEvaluator.evaluate(effect, Color.WHITE, 0xFFFAFAFA.toInt()) as Int)
            val transY = -simpleTopDistance * effect
            if (transY != child.translationY) {
                child.translationY = transY
                consumed[1] = dy
            }

            if (type == 1) {
                isScrollToFullFood = true
            }
        } else if ((child.translationY > 0 || (child.translationY == 0F && dy < 0)) && !child.canScrollVertically()) {
            if (isScrollToFullFood) {
                child.translationY = 0F // top fling to bottom
            } else {
                child.translationY -= dy
                layContent.effectByOffset(child.translationY)
                laySettle.effectByOffset(child.translationY)
            }
            consumed[1] = dy
        }
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: MerchantPageLayout, target: View, velocityX: Float, velocityY: Float): Boolean {
        return onUserStopDragging()
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: MerchantPageLayout, target: View, type: Int) {
        isScrollToFullFood = false
        verticalPagingTouch = 0
        vPager.isScrollable = true
        horizontalPagingTouch = 0
        isScrollRecommends = false

        if (!isScrollToHideFood) {
            onUserStopDragging()
        }
    }

    private fun onUserStopDragging(): Boolean {
        if (selfView.translationY < 0f) {
            return false
        }

        val defaultDisplayHeight = (selfView.height - simpleTopDistance)
        if (defaultDisplayHeight * 0.4F > selfView.translationY) {
            scroller.startScroll(0, selfView.translationY.toInt(), 0, (-selfView.translationY).toInt(), scrollDuration)
            layContent.switch(expanded = false, byScrollerSlide = true)
            laySettle.switch(false)
        } else {
            scroller.startScroll(0, selfView.translationY.toInt(), 0, (defaultDisplayHeight - selfView.translationY).toInt(), scrollDuration)
            layContent.switch(expanded = true, byScrollerSlide = true)
            laySettle.switch(true)
        }
        handler.post(flingRunnable)
        isScrollToHideFood = true
        return true
    }
}
