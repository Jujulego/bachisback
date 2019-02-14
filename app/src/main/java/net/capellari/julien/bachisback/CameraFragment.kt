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

    private lateinit var handler: Handler
    private val backgroundThread = HandlerThread("Camera")

    private val cameraState = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera ${camera.id} opened !")

            this@CameraFragment.camera = camera
            setupPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera ${camera.id} disconnected !")

            this@CameraFragment.camera?.close()
            this@CameraFragment.camera = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.d(TAG, "Camera ${camera.id} error $error !")
        }
    }
    private val textureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false
    }

    // Propriétés
    private val cameraManager: CameraManager by lazy {
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    private val imageSize: Size? get() = camera?.run {
        val char = cameraManager.getCameraCharacteristics(id)
        val map = char.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        map?.getOutputSizes(SurfaceTexture::class.java)?.get(0)
    }

    // Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundThread.start()
        handler = Handler(backgroundThread.looper)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        texture_view.surfaceTextureListener = textureListener
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

    override fun onDestroy() {
        super.onDestroy()

        camera?.close()
        camera = null

        backgroundThread.quitSafely()
        backgroundThread.join()
    }

    // Méthodes
    fun openCamera() {
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

    fun setupPreview() {
        camera?.apply {
            val texture = texture_view.surfaceTexture
            texture.setDefaultBufferSize(imageSize!!.width, imageSize!!.height)

            val surface = Surface(texture)

            // Setup preview
            val previewRqBuilder = createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRqBuilder.addTarget(surface)

            createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    camera?.apply {
                        captureSession = session

                        //previewRqBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                        //previewRqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH)
                        //previewRqBuilder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, Size(1080, 1920))

                        val previewRq = previewRqBuilder.build()
                        session.setRepeatingRequest(previewRq, null, handler)
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Preview failed !")
                }
            }, handler)
        }
    }
}