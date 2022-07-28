package com.example.atry

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator


class LoadingDrawable(private val animatedView: View) : Drawable(), Animatable {

    private lateinit var animatorSet: AnimatorSet

    private var paint: Paint = Paint()

    private var currentStartAngle = 0f
    private var currentSweepAngle = 0f
    //每轉一圈GlobalAngle加多少
    private var currentAngleOffset = 0f
    private val minSweepAngle = 50f

    //是否為增加模式
    private var isAppearing: Boolean = false
    private var running: Boolean = false

    init {

        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paint.color=animatedView.context.getColor(R.color.white)
        }

        setupAnimations()

    }

    override fun start() {
        running = true

        animatorSet.start()
    }

    override fun stop() {
        running = false
        animatorSet.cancel()
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun draw(canvas: Canvas) {
        //畫Loading的arc
        //startAngle為弧的起點
        //sweepAngle為圓弧角度  360代表全滿

        //每次進入增長模式一圈，currentStartAngle 就會減100
        var startAngle = currentStartAngle - currentAngleOffset
        var sweepAngle = (minSweepAngle + currentSweepAngle)

        if (!isAppearing) {
            //消減模式，弧度減小多少，StartAngle就要增加多少
            startAngle += currentSweepAngle

            sweepAngle = 360f - sweepAngle
        }

        val rectF = RectF().apply {
            val padding = 20f
            left = bounds.left + padding
            right = bounds.right - padding
            top = bounds.top + padding
            bottom = bounds.bottom - padding

        }

        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    private fun setupAnimations() {
        //改變弧的起點，從0~360
        val angleValueAnimator = ValueAnimator.ofFloat(0f, 360f)
        angleValueAnimator.interpolator = LinearInterpolator()
        angleValueAnimator.duration = 3000
        angleValueAnimator.repeatCount = ValueAnimator.INFINITE
        angleValueAnimator.addUpdateListener { animation ->
            currentStartAngle = animation.animatedValue as Float
        }

        //改變弧的圓弧角度，0~260
        val sweepValueAnimator = ValueAnimator.ofFloat(0f, 360f - 2 * minSweepAngle)
        sweepValueAnimator.interpolator = AccelerateDecelerateInterpolator()
        sweepValueAnimator.duration = 2000
        sweepValueAnimator.repeatCount = ValueAnimator.INFINITE
        sweepValueAnimator.addUpdateListener { animation ->
            currentSweepAngle = animation.animatedValue as Float

            animatedView.invalidate()
        }

        //Repeat的Listener
        sweepValueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                changeAppearing()
            }
        })

        animatorSet = AnimatorSet()

        animatorSet.playTogether(angleValueAnimator, sweepValueAnimator)
    }

    private fun changeAppearing() {
        isAppearing = !isAppearing

        if (isAppearing) {
            currentAngleOffset = (currentAngleOffset + minSweepAngle * 2) % 360
        }
    }

}