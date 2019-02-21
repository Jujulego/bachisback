package net.capellari.julien.bachisback

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_camera.*

class CameraFragment : Fragment() {
    // Companion
    companion object {
        const val TAG = "CameraFragment"

        const val RQ_CAMERA = 1
    }

    // Attributs
    private var camera: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null

    private var handler: Handler? = null
    private lateinit var mainHandler: Handler
    private var backgroundThread: HandlerThread? = null

    private val cameraState = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera ${camera.id} opened !")

            this@CameraFragment.camera = camera
            setupPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera ${camera.id} disconnected !")

            closeCamera()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            val err = when(error) {
                ERROR_CAMERA_IN_USE -> "Camera in use"
                ERROR_MAX_CAMERAS_IN_USE -> "Max camera in use"
                ERROR_CAMERA_DISABLED -> "Camera disabled"
                ERROR_CAMERA_DEVICE -> "Camera device"
                ERROR_CAMERA_SERVICE -> "Camera service"
                else -> "Unknown error $error"
            }

            Log.d(TAG, "Camera ${camera.id} error: $err !")

            closeCamera()
        }
    }
    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            if (backgroundThread != null) {
                openCamera()
            }
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false
    }

    // Propriétés
    private val constraintLayout get() = view as? ConstraintLayout
    private val initialConstraints = ConstraintSet()

    private val cameraManager: CameraManager by lazy {
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val imageSize: Size? get() {
        val sizes = camera?.run {
            val char = cameraManager.getCameraCharacteristics(id)
            val map = char.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            map?.getOutputSizes(SurfaceTexture::class.java)
        }

        // Search for 16:9
        sizes?.forEach {
            if (it.height * requireContext().resources.getInteger(R.integer.ratio_width) == it.width * requireContext().resources.getInteger(R.integer.ratio_height)) return it
        }

        return sizes?.get(0)
    }

    // Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main handler
        mainHandler = Handler()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialConstraints.clone(view as ConstraintLayout)

        texture_view.surfaceTextureListener = textureListener
    }

    override fun onResume() {
        super.onResume()

        // Start preview
        startBackgroundThread()

        if (texture_view.isAvailable) {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RQ_CAMERA -> {
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop preview
        closeCamera()
        stopBackgroundThread()
    }

    // Méthodes
    private fun setRatioConstraint() {
        val constraints = ConstraintSet()
        constraints.clone(initialConstraints)
        constraints.setDimensionRatio(R.id.texture_view, "${imageSize!!.height}:${imageSize!!.width}")
        constraintLayout?.let { constraints.applyTo(it) }
    }

    // - manage thread
    private fun startBackgroundThread() {
        // Gardien
        if (backgroundThread != null) return

        // Start thread !
        backgroundThread = HandlerThread("Camera").apply {
            start()

            // get handler
            handler = Handler(looper)
        }
    }

    private fun stopBackgroundThread() {
        backgroundThread?.let {
            it.quitSafely()

            try {
                it.join()

            } catch (err: InterruptedException) {
                Log.w(TAG, "Interrupted while stopping background thread", err)

            } finally {
                handler = null
                backgroundThread = null
            }
        }
    }

    // - manage camera
    private fun openCamera() {
        // Get a back camera id
        val id = cameraManager.cameraIdList.find {
            cameraManager.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK
        }

        if (id != null) {
            Log.d(TAG, "Found back camera : $id")

            // Open camera
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(id, cameraState, handler)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), RQ_CAMERA)
            }
        } else {
            Log.d(TAG, "No back camera")
        }
    }

    private fun setupPreview() {
        try {
            camera?.apply {
                // Setup texture
                val texture = texture_view.surfaceTexture
                texture.setDefaultBufferSize(imageSize!!.width, imageSize!!.height)

                val surface = Surface(texture)

                mainHandler.post(this@CameraFragment::setRatioConstraint)

                // Setup preview
                val previewRqBuilder = createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                previewRqBuilder.addTarget(surface)

                createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        updatePreview(previewRqBuilder, session)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Preview failed !")
                    }
                }, handler)
            }
        } catch (err: CameraAccessException) {
            Log.w(TAG, "Unable to access camera", err)

            if (err.reason == CameraAccessException.CAMERA_DISCONNECTED) {
                openCamera()
            }
        }
    }

    private fun updatePreview(previewRqBuilder: CaptureRequest.Builder, session: CameraCaptureSession) {
        camera?.apply {
            captureSession = session

            previewRqBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            previewRqBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            previewRqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH)

            try {
                session.setRepeatingRequest(previewRqBuilder.build(), null, handler)
            } catch (err: CameraAccessException) {
                Log.w(TAG, "Unable to access camera", err)
            }
        }
    }

    private fun closeCamera() {
        camera?.close()

        camera = null
        captureSession = null
    }
}