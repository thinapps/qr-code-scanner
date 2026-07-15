package top.thinapps.qrcodescanner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView

class MaxHeightNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        if (availableHeight <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val maximumHeight = (availableHeight * MAX_HEIGHT_RATIO).toInt()
        val cappedHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            maximumHeight,
            View.MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, cappedHeightMeasureSpec)
    }

    private companion object {
        const val MAX_HEIGHT_RATIO = 0.72f
    }
}
