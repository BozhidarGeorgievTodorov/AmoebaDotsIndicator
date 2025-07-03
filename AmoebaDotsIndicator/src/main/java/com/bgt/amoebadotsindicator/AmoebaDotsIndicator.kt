package com.bgt.amoebadotsindicator

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.min
import kotlin.math.sqrt

/**
 * AmoebaDotsIndicator is a custom View that shows an animated dots indicator
 * for a ViewPager2. It features circular skeleton dots connected by animated
 * "arms" and a movable body that smoothly animates between dots.
 *
 * @constructor Creates an AmoebaDotsIndicator with the given [context] and optional [attrs].
 */
class AmoebaDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Default configuration constants ---
    private var dotCount: Int = 3
    private var dotRadius: Float = 0f
    private var dotColor: Int = 0
    private var bodyColor: Int = 0
    private var animationDuration: Long = 200L

    private var dotSpacingMultiplier = 4f
    private val dotSpacing: Float
        get() = dotRadius * dotSpacingMultiplier

    // --- Paint objects for drawing ---
    private val paintSkeleton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = dotColor
    }
    private val paintBody = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = bodyColor
    }

    // --- State variables ---
    private var currentPage: Int = 0             // Origin dot index during swipe
    private var targetPage: Int = 0              // Destination dot index during swipe
    private var scrollOffset: Float = 0f         // Progress of swipe [0..1]
    private var selectedPage: Int = 0            // Last fully selected page index

    // --- Body animation state ---
    private var bodyPositionX: Float = 0f        // Current horizontal position of the body
    private val bodyRadius: Float
        get() = dotRadius                         // Body radius equals skeleton dot radius
    private var dashAnimator: ValueAnimator? = null

    init {
        // get default colors from res (depend on theme)
        val defaultDotColor = ContextCompat.getColor(context, R.color.amoeba_dot_color)
        val defaultBodyColor = ContextCompat.getColor(context, R.color.amoeba_body_color)
        val defaultDotRadiusPx = 4f * context.resources.displayMetrics.density

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AmoebaDotsIndicator, defStyleAttr, 0)

            dotRadius = typedArray.getDimension(R.styleable.AmoebaDotsIndicator_dotRadius, defaultDotRadiusPx)

            dotRadius = typedArray.getDimension(R.styleable.AmoebaDotsIndicator_dotRadius, dotRadius)
            dotCount = typedArray.getInt(R.styleable.AmoebaDotsIndicator_dotCount, dotCount)
            // Here use the theme-dependent default colors as fallback
            dotColor = typedArray.getColor(R.styleable.AmoebaDotsIndicator_dotColor, defaultDotColor)
            bodyColor = typedArray.getColor(R.styleable.AmoebaDotsIndicator_bodyColor, defaultBodyColor)
            animationDuration = typedArray.getInt(R.styleable.AmoebaDotsIndicator_animationDuration, animationDuration.toInt()).toLong()

            typedArray.recycle()
        } ?: run {
            // If not attrs, uses default values
            dotRadius = defaultDotRadiusPx
            dotColor = defaultDotColor
            bodyColor = defaultBodyColor
        }

        paintSkeleton.color = dotColor
        paintBody.color = bodyColor
    }


    /**
     * Draws the indicator: skeleton dots, connecting arms if swiping, and the body circle.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dotCount == 0) return

        val totalWidth = (dotCount - 1) * dotSpacing
        val startX = (width / 2f) - (totalWidth / 2f)  // Center all dots horizontally
        val centerY = height / 2f                      // Vertical center

        val originX = startX + currentPage * dotSpacing
        val destX = startX + targetPage * dotSpacing

        drawSkeletonDots(canvas, startX, centerY)
        drawConnectingArmsIfNeeded(canvas, originX, destX, centerY)
        drawBody(canvas, centerY)
    }

    private fun drawSkeletonDots(canvas: Canvas, startX: Float, centerY: Float) {
        for (i in 0 until dotCount) {
            val x = startX + i * dotSpacing
            canvas.drawCircle(x, centerY, dotRadius, paintSkeleton)
        }
    }

    private fun drawConnectingArmsIfNeeded(canvas: Canvas, originX: Float, destX: Float, centerY: Float) {
        if (currentPage == targetPage) return

        val left = min(originX, destX)
        val right = kotlin.math.max(originX, destX)

        val armProgress = scrollOffset
        val minArmLength = dotRadius
        val maxArmLength = right - left
        val armLength = minArmLength + (maxArmLength - minArmLength) * armProgress
        val armTipWidth = dotRadius * 0.2f

        drawLeftArm(canvas, originX, centerY, armLength, armTipWidth)
        drawRightArm(canvas, destX, centerY, armLength, armTipWidth)
    }

    private fun drawLeftArm(canvas: Canvas, originX: Float, centerY: Float, armLength: Float, armTipWidth: Float) {
        val path = Path()
        val offsetX = dotRadius / 2f
        val dx = offsetX
        val dy = sqrt(dotRadius * dotRadius - dx * dx)

        val startYTop = centerY - dy
        val startYBottom = centerY + dy

        path.moveTo(originX + dx, startYTop)
        path.quadTo(originX + dx + armLength, centerY - armTipWidth, originX + dx + armLength, centerY)
        path.quadTo(originX + dx + armLength, centerY + armTipWidth, originX + dx, startYBottom)
        path.close()

        canvas.drawPath(path, paintSkeleton)
    }

    private fun drawRightArm(canvas: Canvas, destX: Float, centerY: Float, armLength: Float, armTipWidth: Float) {
        val path = Path()
        val offsetX = -dotRadius / 2f
        val dx = offsetX
        val dy = sqrt(dotRadius * dotRadius - dx * dx)

        val startYTop = centerY - dy
        val startYBottom = centerY + dy

        path.moveTo(destX + dx, startYTop)
        path.quadTo(destX + dx - armLength, centerY - armTipWidth, destX + dx - armLength, centerY)
        path.quadTo(destX + dx - armLength, centerY + armTipWidth, destX + dx, startYBottom)
        path.close()

        canvas.drawPath(path, paintSkeleton)
    }

    private fun drawBody(canvas: Canvas, centerY: Float) {
        canvas.drawCircle(bodyPositionX, centerY, bodyRadius, paintBody)
    }

    /**
     * Attaches the indicator to a [ViewPager2] instance, syncing its state and animations.
     */
    fun attachToViewPager(viewPager: ViewPager2) {
        dotCount = viewPager.adapter?.itemCount ?: dotCount

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                selectedPage = position
                currentPage = position
                startDashAnimation(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (positionOffset == 0f) {
                    scrollOffset = 0f
                    currentPage = selectedPage
                    targetPage = selectedPage
                } else {
                    if (position >= selectedPage) {
                        currentPage = selectedPage
                        targetPage = if (selectedPage + 1 < dotCount) selectedPage + 1 else selectedPage
                        scrollOffset = positionOffset
                    } else {
                        currentPage = position
                        targetPage = selectedPage
                        scrollOffset = 1f - positionOffset
                    }
                }
                invalidate()
            }
        })

        // Initialize the body position after layout
        viewPager.post {
            initializeBodyPosition()
        }
    }

    private fun initializeBodyPosition() {
        if (dotCount == 0 || width == 0) return
        val totalWidth = (dotCount - 1) * dotSpacing
        val startX = (width / 2f) - (totalWidth / 2f)
        bodyPositionX = startX + selectedPage * dotSpacing
        invalidate()
    }

    private fun startDashAnimation(targetPage: Int) {
        if (width == 0) return
        val totalWidth = (dotCount - 1) * dotSpacing
        val startX = (width / 2f) - (totalWidth / 2f)
        val targetX = startX + targetPage * dotSpacing

        dashAnimator?.cancel()
        dashAnimator = ValueAnimator.ofFloat(bodyPositionX, targetX).apply {
            duration = animationDuration
            startDelay = 25
            interpolator = OvershootInterpolator()
            addUpdateListener {
                bodyPositionX = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    // --- Public setters for dynamic configuration ---

    /** Sets the number of dots */
    fun setDotCount(count: Int) {
        if (count > 0) {
            dotCount = count
            invalidate()
            requestLayout()
        }
    }

    /** Sets the radius of the dots and body */
    fun setDotRadius(radius: Float) {
        if (radius > 0) {
            dotRadius = radius
            invalidate()
            requestLayout()
        }
    }

    /** Sets the color of the dots and arms */
    fun setDotColor(color: Int) {
        dotColor = color
        paintSkeleton.color = dotColor
        invalidate()
    }

    /** Sets the color of the body */
    fun setBodyColor(color: Int) {
        bodyColor = color
        paintBody.color = bodyColor
        invalidate()
    }

    /** Sets the duration of the body animation in milliseconds */
    fun setAnimationDuration(durationMillis: Long) {
        animationDuration = durationMillis
    }

    /** Sets the multiplier used to calculate the spacing between dots.  */
    fun setDotSpacingMultiplier(multiplier: Float) {
        if (multiplier > 0) {
            dotSpacingMultiplier = multiplier
            invalidate()
            requestLayout()
        }
    }
}
