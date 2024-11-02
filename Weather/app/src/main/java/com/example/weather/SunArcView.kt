package com.example.weather

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*
class SunArcView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var sunriseTime: Long = 0L
    private var sunsetTime: Long = 0L
    private var currentTime: Long = 0L

    // Paint for drawing arcs
    private val arcPaintPast = Paint().apply {
        color = Color.parseColor("#FFC62B") // Changed to #FFC62B
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }


    private val arcPaintFuture = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 4f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // Paint for horizontal line
    private val linePaint = Paint().apply {
        color = Color.LTGRAY // Set to gray
        strokeWidth = 4f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }



    // Load the sun bitmap
    private val sunBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.sun)

    // Scaled sun bitmap
    private var scaledSunBitmap: Bitmap

    // Scale down the sun image (adjust the size as needed)
    init {
        val scaleFactor = 0.04f // Adjust this value to resize the sun image
        scaledSunBitmap = Bitmap.createScaledBitmap(
            sunBitmap,
            (sunBitmap.width * scaleFactor).toInt(),
            (sunBitmap.height * scaleFactor).toInt(),
            true
        )
    }

    // Function to set the sunrise and sunset times
    fun setSunTimes(sunrise: Long, sunset: Long) {
        this.sunriseTime = sunrise
        this.sunsetTime = sunset
        invalidate()
    }

    // Update the current time and redraw the view
    fun updateSunPosition(currentTime: Long) {
        this.currentTime = currentTime
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the dimensions considering the padding
        val width = width.toFloat() - paddingLeft - paddingRight
        val height = height.toFloat() - paddingTop - paddingBottom
        val padding = 100f

        // Adjust radii to make an ellipse
        val horizontalRadius = (width - padding * 2) / 2
        val verticalRadius = horizontalRadius * 0.8f // Elliptical ratio
        val centerX = paddingLeft + width / 2
        val centerY = paddingTop + height / 2 + verticalRadius / 2

        // Calculate the day's total duration and the elapsed time
        val totalDayDuration = sunsetTime - sunriseTime
        val elapsedDayTime = currentTime - sunriseTime
        val sunProgress = elapsedDayTime.coerceIn(0, totalDayDuration).toFloat() / totalDayDuration

        // Arc Start and Sweep Angles
        val arcStartAngle = 180f
        val arcSweepAngle = 180f

        // Draw the past (yellow) arc
        canvas.drawArc(
            paddingLeft + padding,
            centerY - verticalRadius,
            width + paddingLeft - padding,
            centerY + verticalRadius,
            arcStartAngle,
            sunProgress * arcSweepAngle,
            false,
            arcPaintPast
        )

        // Draw the future (gray) arc
        canvas.drawArc(
            paddingLeft + padding,
            centerY - verticalRadius,
            width + paddingLeft - padding,
            centerY + verticalRadius,
            arcStartAngle + sunProgress * arcSweepAngle,
            (1 - sunProgress) * arcSweepAngle,
            false,
            arcPaintFuture
        )

        // Draw the gray horizontal line slightly extended beyond the ellipse
        val lineExtension = 50f
        canvas.drawLine(
            paddingLeft + padding - lineExtension, centerY,
            width + paddingLeft - padding + lineExtension, centerY,
            linePaint
        )

        // Only draw the sun if the current time is between sunrise and sunset
        if (currentTime in sunriseTime..sunsetTime) {
            // Calculate the sun's position on the elliptical arc
            val sunAngle = arcStartAngle - sunProgress * arcSweepAngle
            val sunX = (centerX + horizontalRadius * cos(Math.toRadians(sunAngle.toDouble()))).toFloat()
            val sunY = (centerY - verticalRadius * sin(Math.toRadians(sunAngle.toDouble()))).toFloat()

            // Draw the scaled sun bitmap
            val sunWidth = scaledSunBitmap.width.toFloat()
            val sunHeight = scaledSunBitmap.height.toFloat()
            canvas.drawBitmap(scaledSunBitmap, sunX - sunWidth / 2, sunY - sunHeight / 2, null)
        }
    }


    // Helper function to format the time
    private fun formatTime(timeInMillis: Long): String {
        val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return formatter.format(java.util.Date(timeInMillis))
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Get padding
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        // Desired width and height considering the padding
        val desiredWidth = 250 + paddingLeft + paddingRight
        val desiredHeight = 200 + paddingTop + paddingBottom

        // Measure width and height based on the specs provided by the parent
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        // Call setMeasuredDimension with the calculated width and height
        setMeasuredDimension(width, height)
    }

}

