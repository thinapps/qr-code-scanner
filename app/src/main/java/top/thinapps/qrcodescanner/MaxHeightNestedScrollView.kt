package top.thinapps.qrcodescanner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        post { syncScanGuideVisibility() }
    }

    private fun syncScanGuideVisibility() {
        val root = rootView
        val preview = root.findViewById<View>(R.id.previewView) ?: return
        val guide = root.findViewById<View>(R.id.viewScanGuide) ?: return
        val rootWidth = root.width
        if (rootWidth <= 0) return

        val topInset = ViewCompat.getRootWindowInsets(root)
            ?.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
            )
            ?.top
            ?: 0
        val edgePadding = resources.getDimensionPixelSize(R.dimen.app_gutter)
        val availableGuideWidth = rootWidth - (edgePadding * 2)
        val availableGuideHeight = top.coerceAtLeast(0) - topInset - (edgePadding * 2)
        val hasGuideSpace = availableGuideWidth > 0 && availableGuideHeight > 0

        guide.visibility = if (preview.visibility == View.VISIBLE && hasGuideSpace) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private companion object {
        const val MAX_HEIGHT_RATIO = 0.72f
    }
}
