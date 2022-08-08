package com.example.atry.loadingButton

import android.animation.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet

class LoadingButton : AppCompatButton {

    //取得background，也就是shape,用來改變圓角
    private var gradientDrawable: GradientDrawable? = null

    //是否在Loading
    private var isMorphingInProgress: Boolean = false
    private var state: State? = null
    //載入的動畫
    private var loadingDrawable: LoadingDrawable? = null
    //載入完成的Reveal動畫
    private var revealDrawable: RevealDrawable? = null
    //用來處理矩形變圓形再變回來的動猙
    private var animatorSet: AnimatorSet? = null

    private var doneBitmap: Bitmap? = null

    var rectButtonHeight: Int = 0
    var rectButtonWidth: Int = 0
    var initButtonText: String? = null
    var initialCornerRadius: Float = 79f
    var finalCornerRadius: Float = 90f

    private enum class State {
        PROGRESS, IDLE, DONE
    }

    constructor(context: Context) : super(context) {

        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        init(context, attrs, defStyleAttr, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

        val attrsArray = intArrayOf(android.R.attr.background)

        val typedArrayBG = context.obtainStyledAttributes(attrs, attrsArray, defStyleAttr, defStyleRes)

        //取得background，也就是shape,用來改變圓角
        gradientDrawable = typedArrayBG.getDrawable(0) as GradientDrawable

        initButtonText = this.text.toString()

        state = State.IDLE

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (state == State.PROGRESS && !isMorphingInProgress) {
            //開始Loading的動畫
            drawLoadingProgress(canvas)
        } else if (state == State.DONE) {
            //成功換圖的動畫
            drawDoneAnimation(canvas)
        }
    }

    private fun drawLoadingProgress(canvas: Canvas) {
        //畫Loading
        if (loadingDrawable == null || !loadingDrawable!!.isRunning) {
            loadingDrawable = LoadingDrawable(this)

            val padding = 10

            val left = padding
            val top = padding
            val right = width - padding
            val bottom = height - padding

            //設定大小
            loadingDrawable!!.setBounds(left, top, right, bottom)
            //開始Loading
            loadingDrawable!!.start()
        } else {
            //畫在canvas上
            loadingDrawable!!.draw(canvas)
        }
    }

    private fun drawDoneAnimation(canvas: Canvas) {
        revealDrawable!!.draw(canvas)
    }

    fun setDoneImage(@DrawableRes drawableId: Int) {
        doneBitmap = getBitmapFromDrawable(context, drawableId)
    }

    fun startAnimation() {

        //讓矩形的Button縮小至圓形的按鈕
        //記住Button的Width
        rectButtonWidth = width
        rectButtonHeight = height
        initButtonText = text.toString()

        state = State.PROGRESS

        //文字不顯示
        this.text = null
        isClickable = false

        //用高來當圓的半徑
        val diameter = rectButtonHeight

        //調整圓角的動畫
        val cornerAnimation = getCornerAnimation(finalCornerRadius)

        //調整寬度的動畫
        val widthAnimation = getWidthAnimation(rectButtonWidth, diameter)

        animatorSet = AnimatorSet()
        animatorSet!!.duration = 300
        animatorSet!!.playTogether(cornerAnimation, widthAnimation)
        animatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isMorphingInProgress = false
            }
        })

        isMorphingInProgress = true
        animatorSet!!.start()

    }

    fun doneLoadingAnimation() {
        //傳入成功時要換的圖片

        state = State.DONE
        loadingDrawable!!.stop()

        revealDrawable = RevealDrawable(this, doneBitmap!!)
        revealDrawable!!.setBounds(0, 0, width, height)
        revealDrawable!!.start()
        revealDrawable!!.listener = object : RevealDrawable.OnReVealDrawableListener {
            override fun onFinish() {
                postDelayed({ revertAnimation() }, 500)
            }

        }

    }

    private fun revertAnimation() {
        state = State.IDLE
        val cornerAnimation = getCornerAnimation(initialCornerRadius)
        val widthAnimation = getWidthAnimation(width, rectButtonWidth)

        animatorSet = AnimatorSet()
        animatorSet!!.duration = 300
        animatorSet!!.playTogether(cornerAnimation, widthAnimation)

        animatorSet!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                //動畫結束，設定可點擊
                isClickable = true

                isMorphingInProgress = false

                text = initButtonText

            }
        })

        animatorSet!!.start()
        isMorphingInProgress = true
    }

    private fun getBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, drawableId)

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    private fun getWidthAnimation(startWidth: Int, endWidth: Int): ValueAnimator? {
        val widthAnimation = ValueAnimator.ofInt(startWidth, endWidth)
        widthAnimation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = layoutParams
            layoutParams.width = value
            setLayoutParams(layoutParams)
        }
        return widthAnimation
    }

    private fun getCornerAnimation(finalRadius: Float): ObjectAnimator? {
        return ObjectAnimator.ofFloat(gradientDrawable!!,
                "cornerRadius",
                finalRadius)
    }

}
