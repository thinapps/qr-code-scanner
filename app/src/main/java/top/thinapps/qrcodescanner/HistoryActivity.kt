package top.thinapps.qrcodescanner

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import top.thinapps.qrcodescanner.databinding.ActivityHistoryBinding
import top.thinapps.qrcodescanner.databinding.ItemScanHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private val dateFormatter by lazy {
        SimpleDateFormat(HISTORY_TIME_FORMAT, Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupControls()
        renderHistory()
    }

    private fun applyWindowInsets() {
        val baseTop = binding.layoutHistoryContent.paddingTop
        val baseBottom = binding.layoutHistoryContent.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            binding.layoutHistoryContent.updatePadding(
                top = baseTop + bars.top,
                bottom = baseBottom + bars.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setupControls() {
        binding.btnHistoryBack.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            finish()
        }

        binding.btnClearHistory.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            ScanHistoryRepository.clear(this)
            Toast.makeText(this, R.string.history_cleared, Toast.LENGTH_SHORT).show()
            renderHistory()
        }
    }

    private fun renderHistory() {
        val items = ScanHistoryRepository.getItems(this)
        binding.layoutHistoryList.removeAllViews()
        binding.txtHistoryEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.scrollHistory.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        binding.btnClearHistory.isEnabled = items.isNotEmpty()

        items.forEachIndexed { index, item ->
            val itemBinding = ItemScanHistoryBinding.inflate(
                layoutInflater,
                binding.layoutHistoryList,
                false
            )
            itemBinding.txtHistoryValue.text = item.value
            itemBinding.txtHistoryMeta.text = formatHistoryMeta(item)
            itemBinding.root.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                selectHistoryItem(item)
            }

            val itemMargin = resources.getDimensionPixelSize(R.dimen.history_item_top_margin)
            itemBinding.root.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                if (index > 0) topMargin = itemMargin
            }
            binding.layoutHistoryList.addView(itemBinding.root)
        }
    }

    private fun formatHistoryMeta(item: ScanHistoryItem): String {
        return if (item.scannedAtMs > 0L) {
            dateFormatter.format(Date(item.scannedAtMs))
        } else {
            ""
        }
    }

    private fun selectHistoryItem(item: ScanHistoryItem) {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(EXTRA_SELECTED_VALUE, item.value)
        )
        finish()
    }

    companion object {
        const val EXTRA_SELECTED_VALUE = "top.thinapps.qrcodescanner.extra.SELECTED_VALUE"
        private const val HISTORY_TIME_FORMAT = "MMM d, h:mm a"
    }
}
