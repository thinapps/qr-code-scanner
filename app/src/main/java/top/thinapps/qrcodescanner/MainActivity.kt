package top.thinapps.qrcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
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
        setupControls()
        syncActionButtons()
        syncTorchButton()

        if (hasCameraPermission()) {
            startCamera()
        } else {
            showPermissionState()
            requestCameraPermission()
        }
    }

    override fun onDestroy() {
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

    private fun setupControls() {
        binding.btnPermission.setOnClickListener { requestCameraPermission() }
        setupTorchControl()
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
        binding.txtStatus.setText(R.string.scan_status_ready)

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
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                torchEnabled = false
                syncTorchButton()
            } catch (error: RuntimeException) {
                camera = null
                torchEnabled = false
                syncTorchButton()
                Log.w(TAG, "Unable to start camera", error)
                binding.txtStatus.setText(R.string.scan_status_camera_error)
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
                Log.w(TAG, "QR analysis failed", error)
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

    private fun showResult(value: String) {
        lastScannedValue = value
        binding.txtStatus.setText(R.string.scan_status_found)
        binding.txtResult.text = value
        binding.previewView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        syncActionButtons()
    }

    private fun showPermissionState() {
        camera = null
        torchEnabled = false
        binding.previewView.visibility = View.INVISIBLE
        binding.layoutPermission.visibility = View.VISIBLE
        binding.txtStatus.setText(R.string.scan_status_permission_needed)
        syncActionButtons()
        syncTorchButton()
    }

    private fun syncActionButtons() {
        val value = lastScannedValue
        val hasResult = !value.isNullOrBlank()
        binding.btnCopy.isEnabled = hasResult
        binding.btnShare.isEnabled = hasResult
        binding.btnOpen.isEnabled = hasResult && value?.toHttpUri() != null
    }

    private fun syncTorchButton() {
        val currentCamera = camera
        val hasTorch = currentCamera?.cameraInfo?.hasFlashUnit() == true
        binding.btnTorch.visibility = if (hasTorch) View.VISIBLE else View.GONE

        if (!hasTorch) {
            binding.btnTorch.isPressed = false
            return
        }

        val iconColor = ContextCompat.getColor(this, R.color.md_text)
        val buttonColor = ContextCompat.getColor(
            this,
            if (torchEnabled) R.color.torch_button_bg_on else R.color.torch_button_bg
        )
        val description = if (torchEnabled) {
            getString(R.string.action_flashlight_off)
        } else {
            getString(R.string.action_flashlight_on)
        }

        binding.btnTorch.isSelected = torchEnabled
        binding.btnTorch.contentDescription = description
        binding.btnTorch.backgroundTintList = ColorStateList.valueOf(buttonColor)
        binding.btnTorch.iconTint = ColorStateList.valueOf(iconColor)
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

        torchEnabled = !torchEnabled
        syncTorchButton()
        currentCamera.cameraControl.enableTorch(torchEnabled)
        return true
    }

    private fun copyResult() {
        val value = lastScannedValue ?: return
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.scan_result_label), value))
        Toast.makeText(this, R.string.scan_result_copied, Toast.LENGTH_SHORT).show()
    }

    private fun openResult() {
        val uri = lastScannedValue?.toHttpUri() ?: return
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

    private fun String.toHttpUri(): Uri? {
        val normalized = trim()
        val lower = normalized.lowercase(Locale.ROOT)
        return if (lower.startsWith("https://") || lower.startsWith("http://")) {
            Uri.parse(normalized)
        } else {
            null
        }
    }

    private companion object {
        const val REQUIRED_SCAN_HITS = 2
        const val RESULT_COOLDOWN_MS = 1000L
        const val SAME_RESULT_IGNORE_MS = 6000L
        const val TAG = "QrCodeScanner"
    }
}
