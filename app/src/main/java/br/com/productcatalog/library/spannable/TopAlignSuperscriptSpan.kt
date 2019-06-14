package br.com.productcatalog.library.spannable

import android.text.TextPaint
import android.text.style.SuperscriptSpan

class TopAlignSuperscriptSpan(private val shiftPercentage: Float = 0f) : SuperscriptSpan() {

    // divide superscript by this number
    private var fontScale = 1.3f

    override fun updateDrawState(textPaint: TextPaint) {
        // original ascent
        val ascent = textPaint.ascent()

        // scale down the font
        textPaint.textSize = textPaint.textSize / fontScale

        // get the new font ascent
        val newAscent = textPaint.fontMetrics.ascent

        // move baseline to top of old font, then move down size of new font
        // adjust for errors with shift percentage
        textPaint.baselineShift += (ascent - ascent * shiftPercentage - (newAscent - newAscent * shiftPercentage)).toInt()
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        updateDrawState(textPaint)
    }
}
