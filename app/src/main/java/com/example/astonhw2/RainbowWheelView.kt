package com.example.astonhw2

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class RainbowWheelView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val colors = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.rgb(255, 165, 0), Color.BLUE, Color.rgb(0, 191, 255), Color.rgb(128, 0, 255))
    private val paint = Paint()

    private var currentColor = Color.RED
    private var isSpinning = false
    private var lastStopAngle = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()

        val segmentAngle = 360f / colors.size
        var startAngle = 0f

        for (color in colors) {
            paint.color = color
            canvas.drawArc(0f, 0f, width, height, startAngle, segmentAngle, true, paint)
            startAngle += segmentAngle
        }

        paint.color = currentColor
        canvas.drawCircle(width / 2f, height / 2f, width / 3f, paint)
    }

    fun spinWheel() {
        if (!isSpinning) {
            val animator = ValueAnimator.ofFloat(lastStopAngle, lastStopAngle + 360 * 5)
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                rotation = value
                invalidate()
            }
            animator.interpolator = DecelerateInterpolator()
            animator.duration = 3000

            animator.start()

            animator.doOnEnd {
                stopWheelAtRandomPosition()
            }

            isSpinning = true
        }
    }

    private fun stopWheelAtRandomPosition() {
        val randomColorIndex = (0 until colors.size).random()
        val segmentAngle = 360f / colors.size
        val randomStopAngle = (randomColorIndex + 1) * segmentAngle - segmentAngle / 2 + (360 * (3..6).random())

        val animator = ValueAnimator.ofFloat(rotation, randomStopAngle)
        animator.addUpdateListener { valueAnimator ->
            rotation = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 1000
        animator.start()

        animator.doOnEnd {
            currentColor = colors[randomColorIndex]
            lastStopAngle = randomStopAngle % 360
            isSpinning = false

            when (currentColor) {
                Color.RED, Color.YELLOW, Color.rgb(0, 191, 255), Color.rgb(128, 0, 255) -> showText("Sample Text")
                Color.rgb(255, 165, 0), Color.GREEN, Color.BLUE -> showImageFromService()
            }

            invalidate()
        }
    }

    private fun showText(text: String) {
        val textView = TextView(context)
        textView.text = text
        textView.textSize = 18f
        textView.setTextColor(Color.BLACK)
        val parentLayout = (parent as ViewGroup)
        parentLayout.addView(textView)
    }

    private fun showImageFromService() {
        val imageView = ImageView(context)

        val requestOptions = RequestOptions().centerCrop()

        Glide.with(this)
            .load("https://loremflickr.com/240/360")
            .apply(requestOptions)
            .into(imageView)

        val layoutParams = LinearLayout.LayoutParams(
            240,
            360
        )

        imageView.layoutParams = layoutParams

        val spinButton = (context as Activity).findViewById<Button>(R.id.btn_delete)

        val parentContainer = spinButton.parent as ViewGroup

        val index = parentContainer.indexOfChild(spinButton)
        parentContainer.addView(imageView, index + 1)
    }

    fun clearCustomView() {
            val parentLayout = parent as ViewGroup
            val viewsToRemove = ArrayList<View>()

            for (i in 0 until parentLayout.childCount) {
                val child = parentLayout.getChildAt(i)
                if (child != this && child !is Button && child !is SeekBar) {
                    viewsToRemove.add(child)
                }
            }

            for (view in viewsToRemove) {
                parentLayout.removeView(view)
            }
        }
}