package evan.chen.tutorial.loadingbutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.atry.R

// Reveal 改成完成的圖
@SuppressLint("ResourceAsColor")
class RevealDrawable(private val animatedView: View, private var doneBitmap: Bitmap) : Drawable(), Animatable {

    private val paint: Paint = Paint()
    //目前Reveal圓的半徑
    private var radius: Float = 0f
    //Reveal完成後的半徑
    private var finalRadius: Float = 0f
    private lateinit var revealInAnimation: ValueAnimator
    private var isRunning: Boolean = false
    //圓心的X、Y
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var doneBitmapX: Float = 0f
    private var doneBitmapY: Float = 0f
    var listener: OnReVealDrawableListener? = null

    interface OnReVealDrawableListener {
        fun onFinish()
    }

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color=android.R.color.holo_blue_dark
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        //圖的寬度、高度是0.6
        val bitMapWidth = ((bounds.right - bounds.left) * 0.6).toInt()
        val bitMapHeight = ((bounds.bottom - bounds.top) * 0.6).toInt()

        //打勾的圖片大小
        doneBitmapX = ((bounds.right - bounds.left - bitMapWidth) / 2).toFloat()
        doneBitmapY = ((bounds.bottom - bounds.top - bitMapHeight) / 2).toFloat()

        doneBitmap = Bitmap.createScaledBitmap(doneBitmap, bitMapWidth, bitMapHeight, false)

        //Reveal完成的最終半徑，=bound的寬度/2
        finalRadius = ((bounds.right - bounds.left) / 2).toFloat()

        //圓心的X、Y
        centerX = ((bounds.right + bounds.left) / 2).toFloat()
        centerY = ((bounds.bottom + bounds.top) / 2).toFloat()

    }

    override fun start() {

        setupAnimations()
        isRunning = true
        revealInAnimation.start()
    }

    override fun stop() {

        isRunning = false
        revealInAnimation.cancel()
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    override fun draw(canvas: Canvas) {
        //畫Reveal的圓, radius會隨著動畫改變大小
        canvas.drawCircle(centerX, centerY, radius, paint)

        //畫打勾的圖
        canvas.drawBitmap(doneBitmap, doneBitmapX, doneBitmapY, paint)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    private fun setupAnimations() {

        //動畫：reveal
        revealInAnimation = ValueAnimator.ofFloat(0f, finalRadius)
        revealInAnimation.interpolator = DecelerateInterpolator()
        revealInAnimation.duration = 120
        revealInAnimation.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {

                listener?.onFinish()
            }
        })
        revealInAnimation.addUpdateListener { animation ->
            //Reveal的半徑增加
            radius = animation.animatedValue as Float
            invalidateSelf()
            animatedView.invalidate()
        }
    }

}


