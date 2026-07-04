package top.thinapps.qrcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.Locale
import java.util.concurrent.Executors
import top.thinapps.qrcodescanner.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val scanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_CODABAR
            )
            .build()
        BarcodeScanning.getClient(options)
    }

    private var camera: Camera? = null
    private var processingFrame = false
    private var torchEnabled = false
    private var lastScannedValue: String? = null
    private var candidateScanValue: String? = null
    private var candidateScanHits = 0
    private var lastAcceptedScanValue: String? = null
    private var lastAcceptedScanAtMs = 0L

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            showPermissionState()
        }
    }

    private val historyLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult

        val value = result.data
            ?.getStringExtra(HistoryActivity.EXTRA_SELECTED_VALUE)
            ?.trim()
            .orEmpty()
        if (value.isBlank()) return@registerForActivityResult

        showResult(value, recordHistory = false)
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupTypography()
        setupControls()
        syncActionButtons()
        syncTorchButton()
        showStatus(R.string.scan_status_ready)

        if (hasCameraPermission()) {
            startCamera()
        } else {
            showPermissionState()
            requestCameraPermission()
        }
    }

    override fun onDestroy() {
        camera?.cameraInfo?.torchState?.removeObservers(this)
        camera?.cameraControl?.enableTorch(false)
        scanner.close()
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    private fun applyWindowInsets() {
        val baseBottom = binding.layoutContent.paddingBottom
        val torchLayoutParams = binding.btnTorch.layoutParams as FrameLayout.LayoutParams
        val baseTorchTopMargin = torchLayoutParams.topMargin
        val baseTorchEndMargin = torchLayoutParams.marginEnd

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            binding.layoutContent.updatePadding(bottom = baseBottom + bars.bottom)
            binding.btnTorch.updateLayoutParams<FrameLayout.LayoutParams> {
                topMargin = baseTorchTopMargin + bars.top
                marginEnd = baseTorchEndMargin + bars.right
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setupTypography() {
        binding.txtResult.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        setResultText(getString(R.string.scan_result_empty))
    }

    private fun setupControls() {
        binding.btnPermission.setOnClickListener { requestCameraPermission() }
        setupTorchControl()
        binding.btnHistory.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            historyLauncher.launch(Intent(this, HistoryActivity::class.java))
        }
        binding.btnClearResult.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            clearResult()
        }
        binding.btnCopy.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            copyResult()
        }
        binding.btnOpen.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            openResult()
        }
        binding.btnShare.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            shareResult()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTorchControl() {
        binding.btnTorch.setOnClickListener { toggleTorchWithFeedback() }
        binding.btnTorch.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (view.visibility != View.VISIBLE || !view.isEnabled) {
                        return@setOnTouchListener false
                    }
                    view.isPressed = true
                    toggleTorchWithFeedback()
                    true
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.isPressed = false
                    true
                }

                else -> true
            }
        }
    }

    private fun requestCameraPermission() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        binding.previewView.visibility = View.VISIBLE
        binding.layoutPermission.visibility = View.GONE
        showStatus(R.string.scan_status_ready)
        if (lastScannedValue.isNullOrBlank()) {
            setResultText(getString(R.string.scan_result_empty))
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, ::analyzeImage) }

            try {
                camera?.cameraInfo?.torchState?.removeObservers(this)
                cameraProvider.unbindAll()
                val boundCamera = cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                camera = boundCamera
                boundCamera.cameraInfo.torchState.observe(this) { state ->
                    torchEnabled = state == TorchState.ON
                    syncTorchButton()
                }
                syncTorchState(boundCamera)
            } catch (error: RuntimeException) {
                camera = null
                torchEnabled = false
                syncTorchButton()
                Log.w(TAG, "Unable to start camera", error)
                showStatus(R.string.scan_status_camera_error)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        if (processingFrame) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        processingFrame = true
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstNotNullOfOrNull { it.rawValue }?.trim().orEmpty()
                if (value.isNotEmpty()) {
                    runOnUiThread { maybeAcceptScanResult(value) }
                }
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "Barcode analysis failed", error)
            }
            .addOnCompleteListener {
                processingFrame = false
                imageProxy.close()
            }
    }

    private fun maybeAcceptScanResult(value: String) {
        val now = SystemClock.elapsedRealtime()

        if (value == candidateScanValue) {
            candidateScanHits += 1
        } else {
            candidateScanValue = value
            candidateScanHits = 1
        }

        if (candidateScanHits < REQUIRED_SCAN_HITS) return
        if (now - lastAcceptedScanAtMs < RESULT_COOLDOWN_MS) return
        if (value == lastAcceptedScanValue && now - lastAcceptedScanAtMs < SAME_RESULT_IGNORE_MS) {
            lastAcceptedScanAtMs = now
            return
        }

        lastAcceptedScanValue = value
        lastAcceptedScanAtMs = now
        showResult(value)
    }

    private fun showResult(value: String, recordHistory: Boolean = true) {
        lastScannedValue = value
        showStatus(R.string.scan_status_found)
        setResultText(value)
        binding.previewView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        if (recordHistory) {
            ScanHistoryRepository.record(
                this,
                value,
                openableWebLink = value.toWebUri() != null
            )
        }
        syncActionButtons()
    }

    private fun clearResult() {
        val value = lastScannedValue
        if (!value.isNullOrBlank()) {
            lastAcceptedScanValue = value
            lastAcceptedScanAtMs = SystemClock.elapsedRealtime()
        }
        lastScannedValue = null
        showStatus(R.string.scan_status_ready)
        setResultText(getString(R.string.scan_result_empty))
        syncActionButtons()
    }

    private fun showPermissionState() {
        camera?.cameraInfo?.torchState?.removeObservers(this)
        camera = null
        torchEnabled = false
        binding.previewView.visibility = View.INVISIBLE
        binding.layoutPermission.visibility = View.VISIBLE
        showStatus(R.string.scan_status_permission_needed)
        syncActionButtons()
        syncTorchButton()
    }

    private fun showStatus(messageResId: Int) {
        binding.txtStatus.setText(messageResId)
        binding.txtStatus.visibility = View.VISIBLE
    }

    private fun setResultText(value: String) {
        binding.txtResult.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        binding.txtResult.text = monospaceText(value)
    }

    private fun monospaceText(value: String): SpannableString {
        return SpannableString(value).apply {
            if (value.isEmpty()) return@apply

            val typefaceSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TypefaceSpan(Typeface.MONOSPACE)
            } else {
                @Suppress("DEPRECATION")
                TypefaceSpan("monospace")
            }
            setSpan(
                typefaceSpan,
                0,
                value.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun syncActionButtons() {
        val value = lastScannedValue
        val hasResult = !value.isNullOrBlank()
        binding.btnClearResult.visibility = if (hasResult) View.VISIBLE else View.INVISIBLE
        binding.btnCopy.isEnabled = hasResult
        binding.btnShare.isEnabled = hasResult
        binding.btnOpen.isEnabled = hasResult && value?.toWebUri() != null
    }

    private fun syncTorchButton() {
        val currentCamera = camera
        val hasTorch = currentCamera?.cameraInfo?.hasFlashUnit() == true
        binding.btnTorch.visibility = if (hasTorch) View.VISIBLE else View.GONE

        if (!hasTorch) {
            binding.btnTorch.isPressed = false
            return
        }

        val iconColor = ContextCompat.getColor(
            this,
            if (torchEnabled) R.color.md_bg else R.color.md_text
        )
        val buttonColor = ContextCompat.getColor(
            this,
            if (torchEnabled) R.color.torch_button_bg_on else R.color.torch_button_bg
        )
        val iconResource = if (torchEnabled) R.drawable.ic_flash_filled else R.drawable.ic_flash
        val description = if (torchEnabled) {
            getString(R.string.action_flashlight_off)
        } else {
            getString(R.string.action_flashlight_on)
        }

        binding.btnTorch.isSelected = torchEnabled
        binding.btnTorch.contentDescription = description
        binding.btnTorch.setImageResource(iconResource)
        binding.btnTorch.backgroundTintList = ColorStateList.valueOf(buttonColor)
        binding.btnTorch.imageTintList = ColorStateList.valueOf(iconColor)
    }

    private fun syncTorchState(currentCamera: Camera?) {
        torchEnabled = currentCamera?.cameraInfo?.torchState?.value == TorchState.ON
        syncTorchButton()
    }

    private fun toggleTorchWithFeedback() {
        if (toggleTorch()) {
            binding.btnTorch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    private fun toggleTorch(): Boolean {
        val currentCamera = camera ?: return false
        if (!currentCamera.cameraInfo.hasFlashUnit()) {
            syncTorchButton()
            return false
        }

        val requestedTorchEnabled = !torchEnabled
        torchEnabled = requestedTorchEnabled
        syncTorchButton()

        val torchRequest = currentCamera.cameraControl.enableTorch(requestedTorchEnabled)
        torchRequest.addListener(
            {
                try {
                    torchRequest.get()
                } catch (error: Exception) {
                    if (error is InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                    Log.w(TAG, "Unable to toggle torch", error)
                    syncTorchState(currentCamera)
                }
            },
            ContextCompat.getMainExecutor(this)
        )
        return true
    }

    private fun copyResult() {
        val value = lastScannedValue ?: return
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.scan_result_label), value))
    }

    private fun openResult() {
        val uri = lastScannedValue?.toWebUri() ?: return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (error: ActivityNotFoundException) {
            Log.w(TAG, "No app available to open scanned link", error)
            Toast.makeText(this, R.string.scan_result_open_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareResult() {
        val value = lastScannedValue ?: return
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, value)
        startActivity(Intent.createChooser(intent, getString(R.string.scan_result_share_title)))
    }

    private fun String.toWebUri(): Uri? {
        val normalized = trim()
        if (normalized.isBlank() || normalized.any { it.isWhitespace() }) return null

        val httpsPrefix = WEB_SCHEME_HTTPS + WEB_SCHEME_SEPARATOR
        val httpPrefix = WEB_SCHEME_HTTP + WEB_SCHEME_SEPARATOR
        if (normalized.startsWith(httpsPrefix, ignoreCase = true) ||
            normalized.startsWith(httpPrefix, ignoreCase = true)
        ) {
            val uri = Uri.parse(normalized)
            val scheme = uri.scheme?.lowercase(Locale.ROOT)
            return if (
                (scheme == WEB_SCHEME_HTTPS || scheme == WEB_SCHEME_HTTP) &&
                uri.userInfo.isNullOrBlank() &&
                !uri.host.isNullOrBlank()
            ) {
                uri
            } else {
                null
            }
        }

        if (normalized.contains(WEB_SCHEME_SEPARATOR) || normalized.contains(USER_INFO_SEPARATOR)) return null

        val uri = Uri.parse(httpsPrefix + normalized)
        val host = uri.host ?: return null
        return if (host.isLikelyWebHost()) uri else null
    }

    private fun String.isLikelyWebHost(): Boolean {
        val host = trim().trimEnd('.').lowercase(Locale.ROOT)
        if (host.length > MAX_HOST_LENGTH || !host.contains('.')) return false

        val labels = host.split('.')
        if (labels.size < 2) return false
        if (!labels.all { it.isValidHostLabel() }) return false

        val topLevelDomain = labels.last()
        return topLevelDomain.startsWith(PUNYCODE_PREFIX) ||
            (topLevelDomain.length >= MIN_TLD_LENGTH && topLevelDomain.all { it.isLetter() })
    }

    private fun String.isValidHostLabel(): Boolean {
        return isNotEmpty() &&
            length <= MAX_HOST_LABEL_LENGTH &&
            !startsWith('-') &&
            !endsWith('-') &&
            all { it.isLetterOrDigit() || it == '-' }
    }

    private companion object {
        const val REQUIRED_SCAN_HITS = 2
        const val RESULT_COOLDOWN_MS = 1000L
        const val SAME_RESULT_IGNORE_MS = 6000L
        const val MAX_HOST_LENGTH = 253
        const val MAX_HOST_LABEL_LENGTH = 63
        const val MIN_TLD_LENGTH = 2
        const val WEB_SCHEME_HTTPS = "https"
        const val WEB_SCHEME_HTTP = "http"
        const val WEB_SCHEME_SEPARATOR = "://"
        const val USER_INFO_SEPARATOR = '@'
        const val PUNYCODE_PREFIX = "xn--"
        const val TAG = "QrCodeScanner"
    }
}
