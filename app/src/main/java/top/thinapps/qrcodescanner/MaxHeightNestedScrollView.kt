package top.thinapps.qrcodescanner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
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
        post { updateScanGuideForAvailablePreview() }
    }

    private fun updateScanGuideForAvailablePreview() {
        val root = rootView ?: return
        val preview = root.findViewById<View>(R.id.previewView) ?: return
        val guide = root.findViewById<View>(R.id.viewScanGuide) ?: return
        val rootHeight = root.height
        val rootWidth = root.width
        if (rootHeight <= 0 || rootWidth <= 0) return

        val previewBottom = top.coerceIn(0, rootHeight)
        val visiblePreviewHeight = previewBottom
        if (preview.visibility != View.VISIBLE || visiblePreviewHeight <= 0) {
            guide.visibility = View.INVISIBLE
            return
        }

        val edgePadding = resources.getDimensionPixelSize(R.dimen.app_gutter)
        val minGuideSize = resources.getDimensionPixelSize(R.dimen.scan_guide_min_size)
        val availableGuideWidth = rootWidth - (edgePadding * 2)
        val availableGuideHeight = visiblePreviewHeight - (edgePadding * 2)
        val targetGuideSize = (rootWidth * SCAN_GUIDE_WIDTH_RATIO).toInt()
        val preferredGuideSize = maxOf(targetGuideSize, minGuideSize)
        val guideSize = minOf(
            preferredGuideSize,
            availableGuideWidth,
            availableGuideHeight
        ).coerceAtLeast(0)

        if (guideSize <= 0) {
            guide.visibility = View.INVISIBLE
            return
        }

        guide.updateLayoutParams<FrameLayout.LayoutParams> {
            width = guideSize
            height = guideSize
        }
        guide.translationY = ((visiblePreviewHeight - guideSize) / 2f).coerceAtLeast(0f)
        guide.visibility = View.VISIBLE
    }

    private companion object {
        const val MAX_HEIGHT_RATIO = 0.72f
        const val SCAN_GUIDE_WIDTH_RATIO = 0.80f
    }
}
