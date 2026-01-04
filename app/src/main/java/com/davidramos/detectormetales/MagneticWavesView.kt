package com.davidramos.detectormetales

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class WavePulseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 6f
        setShadowLayer(25f, 0f, 0f, Color.GREEN)
    }

    private var pulse = 0f
    private var maxRadius = 0f
    private var strength = 0f  // 0–1 intensidad del campo magnético

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun updateStrength(value: Float) {
        // value: intensidad normalizada (0–1)
        strength = value.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        maxRadius = min(width, height) / 2f

        val radius = pulse * maxRadius

        paint.alpha = (150 + 100 * strength).toInt().coerceAtMost(255)
        paint.strokeWidth = 4f + (10f * strength)

        canvas.drawCircle(cx, cy, radius, paint)

        pulse += 0.02f + (0.05f * strength)

        if (pulse > 1f) pulse = 0f

        invalidate()
    }
}
