package com.example.st006_gpscamera.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


@SuppressLint("ViewConstructor")
class CircleView : AppCompatImageView {
    private val TYPE_SCALE = ScaleType.CENTER_CROP

    private val COLORDRAWABLE_DIMENSION = 2

    private fun init() {
        super.setScaleType(TYPE_SCALE)
        isReady = true
        if (isSetupPending) {
            setupView()
            isSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return TYPE_SCALE
    }

    override fun setScaleType(scaleType: ScaleType) {
        require(scaleType == TYPE_SCALE) { String.format("ScaleType $scaleType not supported.") }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        this.bm = bm
        setupView()
    }

    private val DEFAULT_BORDER_WIDTH = 2
    private val DEFAULT_BORDER_COLOR = Color.BLACK

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        bm = getBitmapFromDrawable(drawable)
        setupView()
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    private var bm: Bitmap? = null
    private var bmShader: BitmapShader? = null
    private var bmW = 0
    private var bmH = 0

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), drawableRadius, bitmapPaint)
        if (borderWidth != 0) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), borderRadius, borderPaint)
        }
    }

    private var drawableRadius = 0f
    private var borderRadius = 0f

    private var colorFilter: ColorFilter? = null

    private var isReady = false
    private var isSetupPending = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupView()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(borderColor: Int) {
        if (borderColor == this.borderColor) {
            return
        }
        this.borderColor = borderColor
        borderPaint.color = this.borderColor
        invalidate()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        bm = getBitmapFromDrawable(drawable)
        setupView()
    }

    fun getBorderWidth(): Int {
        return borderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        if (borderWidth == this.borderWidth) {
            return
        }
        this.borderWidth = borderWidth
        setupView()
    }

    private fun setupView() {
        if (!isReady) {
            isSetupPending = true
            return
        }
        if (bm == null) {
            return
        }
        bmShader = BitmapShader(bm!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.isAntiAlias = true
        bitmapPaint.setShader(bmShader)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth.toFloat()
        bmH = bm!!.height
        bmW = bm!!.width
        borderRectF[0f, 0f, width.toFloat()] = height.toFloat()
        drawableRectF[borderWidth.toFloat(), borderWidth.toFloat(), borderRectF.width() - borderWidth] = borderRectF.height() - borderWidth
        updateShaderMatrix()
        invalidate()
    }

    fun setDrawableRadius(radius: Float) {
        drawableRadius = radius
        borderRadius = radius - borderWidth / 2
        invalidate()
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        shaderMatrix.set(null)
        if (bmW * drawableRectF.height() > drawableRectF.width() * bmH) {
            scale = drawableRectF.height() / bmH.toFloat()
            dx = (drawableRectF.width() - bmW * scale) * 0.5f
        } else {
            scale = drawableRectF.width() / bmW.toFloat()
            dy = (drawableRectF.height() - bmH * scale) * 0.5f
        }
        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate((dx + 0.5f).toInt() + borderWidth.toFloat(), (dy + 0.5f).toInt() + borderWidth.toFloat())
        bmShader!!.setLocalMatrix(shaderMatrix)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        bm = getBitmapFromDrawable(drawable)
        setupView()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === colorFilter) {
            return
        }
        colorFilter = cf
        bitmapPaint.setColorFilter(colorFilter)
        invalidate()
    }

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private val drawableRectF = RectF()
    private val borderRectF = RectF()

    private val shaderMatrix = Matrix()
    private val bitmapPaint = Paint()
    private val borderPaint = Paint()

}