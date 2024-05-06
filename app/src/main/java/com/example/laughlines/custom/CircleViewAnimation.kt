package com.example.st006_gpscamera.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.pow


class CircleViewAnimation {

    private var target: View? = null
    private var dest: View? = null

    private var contextReference: WeakReference<Activity?>? = null
    private var borderWidth = 4
    private var borderColor = Color.BLACK

    fun CircleViewAnimation() {}

    fun attachToActivity(activity: Activity?): CircleViewAnimation {
        contextReference = WeakReference(activity)
        return this
    }

    fun setTargetView(view: View?): CircleViewAnimation {
        target = view
        setOriginRect(target!!.width.toFloat(), target!!.height.toFloat())
        return this
    }

    private fun drawViewToBitmap(view: View?, width: Int, height: Int): Bitmap? {
        val drawable: Drawable = BitmapDrawable()
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(dest)
        drawable.bounds = Rect(0, 0, width, height)
        drawable.draw(c)
        view!!.draw(c)
        return dest
    }

    private fun reset() {
        bm!!.recycle()
        bm = null
        if (circleView!!.parent != null) (circleView!!.parent as ViewGroup).removeView(circleView)
        circleView = null
    }

    private fun setOriginRect(x: Float, y: Float): CircleViewAnimation {
        originalX = x
        originalY = y
        return this
    }

    private fun setDestRect(x: Float, y: Float): CircleViewAnimation {
        destinationX = x
        destinationY = y
        return this
    }

    private var originalX = 0f
    private var originalY = 0f
    private var destinationX = 0f
    private var destinationY = 0f

    fun setDestView(view: View?): CircleViewAnimation {
        dest = view
        setDestRect(dest!!.width.toFloat(), dest!!.width.toFloat())
        return this
    }

    fun setBorderWidth(width: Int): CircleViewAnimation {
        borderWidth = width
        return this
    }

    private fun prepare(): Boolean {
        if (contextReference!!.get() != null) {
            val decoreView = contextReference!!.get()!!.window.decorView as ViewGroup
            bm = drawViewToBitmap(target, target!!.width, target!!.height)
            if (circleView == null) circleView = contextReference!!.get()?.let { CircleView(it) }
            circleView!!.setImageBitmap(bm)
            circleView!!.setBorderWidth(borderWidth)
            circleView!!.setBorderColor(borderColor)
            val src = IntArray(2)
            target!!.getLocationOnScreen(src)
            val params = FrameLayout.LayoutParams(target!!.width, target!!.height)
            params.setMargins(src[0], src[1], 0, 0)
            if (circleView!!.parent == null) decoreView.addView(circleView, params)
        }
        return true
    }

    fun startAnimation() {
        if (prepare()) {
            target!!.visibility = View.INVISIBLE
            getAvatarRevealAnimator().start()
        }
    }

    fun setBorderColor(color: Int): CircleViewAnimation {
        borderColor = color
        return this
    }

    fun setCircleDuration(duration: Int): CircleViewAnimation {
        circleDuration = duration
        return this
    }

    fun setMoveDuration(duration: Int): CircleViewAnimation {
        moveDuration = duration
        return this
    }

    @SuppressLint("ObjectAnimatorBinding") private fun getAvatarRevealAnimator(): AnimatorSet {
        val endRadius = max(destinationX, destinationY) / 2
        val startRadius = max(originalX, originalY)
        val mRevealAnimator: Animator = ObjectAnimator.ofFloat(circleView, "drawableRadius", startRadius, endRadius * 1.05f, endRadius * 0.9f, endRadius)
        mRevealAnimator.interpolator = AccelerateInterpolator()

        val scaleFactor = 0.5f
        val scaleAnimatorY: Animator = ObjectAnimator.ofFloat(circleView, View.SCALE_Y, 1f, 1f, scaleFactor, scaleFactor)
        val scaleAnimatorX: Animator = ObjectAnimator.ofFloat(circleView, View.SCALE_X, 1f, 1f, scaleFactor, scaleFactor)
        val animatorCircleSet = AnimatorSet()
        animatorCircleSet.setDuration(circleDuration.toLong())
        animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY, mRevealAnimator)
        animatorCircleSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animListener?.onAnimationStart(p0)
            }

            override fun onAnimationEnd(p0: Animator) {
                val src = IntArray(2)
                val dest = IntArray(2)
                circleView!!.getLocationOnScreen(src)
                this@CircleViewAnimation.dest!!.getLocationOnScreen(dest)
                val y = circleView!!.y
                val x = circleView!!.x
                val translatorX: Animator = ObjectAnimator.ofFloat(circleView, View.X, x, x + dest[0] - (src[0] + (originalX * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destinationX - scaleFactor * endRadius))
                translatorX.interpolator = TimeInterpolator { input ->
                    (-(input - 1).toDouble().pow(2.0) + 1f).toFloat()
                }
                val translatorY: Animator = ObjectAnimator.ofFloat(circleView, View.Y, y, y + dest[1] - (src[1] + (originalY * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destinationY - scaleFactor * endRadius))
                translatorY.interpolator = LinearInterpolator()
                val animatorMoveSet = AnimatorSet()
                animatorMoveSet.playTogether(translatorX, translatorY)
                animatorMoveSet.setDuration(moveDuration.toLong())
                val animatorDisappearSet = AnimatorSet()
                val disappearAnimatorY: Animator = ObjectAnimator.ofFloat(circleView, View.SCALE_Y, scaleFactor, 0f)
                val disappearAnimatorX: Animator = ObjectAnimator.ofFloat(circleView, View.SCALE_X, scaleFactor, 0f)
                animatorDisappearSet.setDuration(disappearDuration.toLong())
                animatorDisappearSet.playTogether(disappearAnimatorX, disappearAnimatorY)
                val total = AnimatorSet()
                total.playSequentially(animatorMoveSet, animatorDisappearSet)
                total.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) {
                        animListener?.onAnimationEnd(p0)
                        reset()
                    }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
                total.start()
            }

            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        return animatorCircleSet
    }

    private var bm: Bitmap? = null
    private var circleView: CircleView? = null
    private var animListener: Animator.AnimatorListener? = null

    fun setAnimationListener(listener: Animator.AnimatorListener?): CircleViewAnimation {
        animListener = listener
        return this
    }

    private val DEFAULT_DURATION = 1000
    private val DEFAULT_DURATION_DISAPPEAR = 200

    private var circleDuration = DEFAULT_DURATION
    private var moveDuration = DEFAULT_DURATION
    private val disappearDuration = DEFAULT_DURATION_DISAPPEAR

}