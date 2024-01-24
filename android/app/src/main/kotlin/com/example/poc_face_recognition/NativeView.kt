import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.poc_face_recognition.databinding.ActivityCameraScanBinding
import com.pingan.ai.face.common.PaFaceConstants
import com.pingan.ai.face.control.LiveFaceConfig
import com.pingan.ai.face.entity.PaFaceDetectFrame
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NativeView (
    private val context: Context,
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val layoutInflater: LayoutInflater,
) : PlatformView {

    object FaceRecognitionFailInitFaceDetectorException : Exception("FACIAL_RECOGNITION_FAIL_INIT_FACE_DETECTOR")
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    private lateinit var binding: ActivityCameraScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    lateinit var faceDetectorManagerInstanceWrapper: FaceDetectorManagerInstanceWrapper
    private var motionList: List<Int>? = null
    private val _facialRecognitionEvent: SingleLiveEvent<FaceRecognitionEvent> =
        SingleLiveEvent()
    val circleBorder = SingleLiveEvent<Boolean>()
    var currentTips: Int = 0
    var motionDone: String? = null
    val onDidMotionDone = SingleLiveEvent<Boolean>()
    private var _isForceExit: Pair<Boolean, FaceRecognitionEvent?> = false to null
    var numOfLivenessMotionType = 3
    private val _timeCounter: MutableStateFlow<TimeCounterResult> =
        MutableStateFlow(TimeCounterResult.TimeLeft(1L))
    val timeCounter: StateFlow<TimeCounterResult> = _timeCounter
    private val _actionNameText: MutableLiveData<String> = MutableLiveData("")
    val actionNameText: LiveData<String> = _actionNameText
    val faceRecognitionEvent: LiveData<NativeView.FaceRecognitionEvent> = _facialRecognitionEvent

    override fun getView(): View {
        return binding.root
    }

    override fun dispose() {
        cameraExecutor.shutdown()
    }

    init {
        init()
    }

    private fun init() {
        binding = ActivityCameraScanBinding.inflate(layoutInflater)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        setUpAndStartDetector()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, 1111);
    }

    fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                val previewBuilder = Preview.Builder()
                val extender: Camera2Interop.Extender<*> = Camera2Interop.Extender(previewBuilder)
                extender.setCaptureRequestOption(
                    CaptureRequest.CONTROL_AF_MODE,
                    CameraMetadata.CONTROL_AF_MODE_OFF
                )
                extender.setCaptureRequestOption(CaptureRequest.LENS_FOCUS_DISTANCE, 0.0f)

                val preview: Preview = previewBuilder
                    .setTargetResolution(
                        Size(binding.viewFinder.width, binding.viewFinder.height)
                    )
                    .setTargetRotation(Surface.ROTATION_0)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview)
                    camera.let {
                        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }
                } catch (e: Exception) {}
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun setUpAndStartDetector() {
        faceDetectorManagerInstanceWrapper = FaceDetectorManagerInstanceWrapper()
        initialAndStartDetectorManager(context)
    }

    fun initialAndStartDetectorManager(context: Context) {
        with(faceDetectorManagerInstanceWrapper) {
            setLoggerEnable(true)

            val isInitSuccess = initFaceDetector(context, getLiveFaceConfig())
            if (!isInitSuccess) throw FaceRecognitionFailInitFaceDetectorException
            setOnFaceDetectorListener(onFaceDetectorListener)

            if (isHasStart) {
                stopFaceDetect()
            }

            val motion = getMotionTypeByNums()
            setMotions(motion)
            startFaceDetect()
        }
    }

    private fun getMotionTypeByNums(): List<Int> {
        motionList = if (numOfLivenessMotionType == 0) {
            listOf(PaFaceConstants.MotionType.NORMAL)
        } else {
            motionList?.shuffled()?.take(numOfLivenessMotionType)
        }
        return motionList!!
    }





    private fun updateTimerText(secLeft: Long) {
        binding.tvTimer.text = secLeft.toTimeFormat()
    }

    private fun Long.toTimeFormat(): String =
        "${this / 60}:${(this % 60).toString().padStart(2, '0')}"

    private fun updateActionNameText(text: String) {
        binding.facialRecognitionTip.text = text
    }

//    private fun onTimeCounterChange(timeCounterResult: TimeCounterResult) {
//        when (timeCounterResult) {
//            is TimeCounterResult.TimeLeft -> {
//                updateTimerText(timeCounterResult.timeSeconds)
//            }
//
//            is TimeCounterResult.TimeOut -> updateTimerText(0L)
//            else -> {}
//        }
//    }


    enum class FaceRecognitionEvent {
        TIMER_SHOULD_START,
        LIVENESS_CHECK_FAILED_INTERRUPT,
        LIVENESS_CHECK_FAILED,
        LIVENESS_CHECK_SUCCESS,
        FACE_COMPARE_FAILED,
        FACE_COMPARE_FAILED_RETRY,
        FACE_COMPARE_SUCCESS,
        GENERAL_ERROR_OCCURRED,
    }


    private fun onEvent(event: FaceRecognitionEvent) {
        when (event) {
            FaceRecognitionEvent.TIMER_SHOULD_START -> {
//                viewModel.startCountDownTimer()
                showNormalUI()
            }

            FaceRecognitionEvent.LIVENESS_CHECK_SUCCESS -> {
//                onFaceRecognitionSuccess()
            }
            else -> {}
        }
    }

    private fun showNormalUI() {
        binding.previewContainer.isActivated = false
        viewVisibilitySelectors.forEach { it.setToNormalVisibility() }
    }

    private fun onTimeCounterChange(timeCounterResult: TimeCounterResult) {
        when (timeCounterResult) {
            is TimeCounterResult.TimeLeft -> {
                updateTimerText(timeCounterResult.timeSeconds)
            }

            is TimeCounterResult.TimeOut -> updateTimerText(0L)
            else -> {}
        }
    }

    fun showLoading() {
        binding.previewContainer.isActivated = false
        viewVisibilitySelectors.forEach { it.setToLoadingVisibility() }
    }

    private val viewVisibilitySelectors: List<ViewVisibilitySelector> by lazy {
        listOf(
            ViewVisibilitySelector(
                binding.facialRecognitionTip,
                isVisibleNormal = true,
                isVisibleLoading = false,
                isVisibleSuccess = false
            ),
            ViewVisibilitySelector(
                binding.tvTimer,
                isVisibleNormal = true,
                isVisibleLoading = false,
                isVisibleSuccess = false
            ),
        )
    }

    private fun getLiveFaceConfig(): LiveFaceConfig {
        val hashMap = hashMapOf(
            "isMultiFrameLive" to "false",
            "openFeatureCmp" to "true",
            "openAntiSplit" to "false",
            "allowChangeFace" to "false"
        )
        return LiveFaceConfig.LiveFaceConfigBuilder()
            .farThreshold(0.3f)
            .closeThreshold(0.55f)
            .yawThreshold(15)
            .rollThreshold(15)
            .pitchThreshold(15)
            .centerDistanceThreshold(130)
            .blurThreshold(0.2f)
            .darkThreshold(45.0f)
            .brightnessThreshold(240.0f)
            .extensionMap(hashMap)
            .build()
    }

    private val onFaceDetectorListener: OnFaceDetectorListener =
        FaceRecognitionOnFaceDetectorListener(
            setTipTextLiveData = ::setTipTextLiveData,
            onLivenessCheckSuccess = ::onLivenessCheckSuccess,
            onLivenessCheckFailed = ::onLivenessCheckFailed,
            onMotionDone = ::onMotionDone
        )

    private fun getStringMotionLog(motionType: Int?): String {
        return when (motionType) {
            PaFaceConstants.MotionType.NORMAL -> "Normal"
            PaFaceConstants.MotionType.OPEN_MOUTH -> "Open mouth"
            PaFaceConstants.MotionType.BLINK_EYE -> "Blink eye"
            PaFaceConstants.MotionType.SHAKE_HEAD -> "Shake head"
            PaFaceConstants.MotionType.NOD_HEAD -> "Nod head"
            PaFaceConstants.MotionType.AURORA -> "Aurora"
            else -> {
                ""
            }
        }
    }

    private fun setTipTextLiveData(tip: Int, isFaceDetected: Boolean) {
        _facialRecognitionEvent.postValue(FaceRecognitionEvent.TIMER_SHOULD_START)
        circleBorder.postValue(isFaceDetected)
        if (tip == currentTips) {
            return
        } else {
            currentTips = tip
        }
        // no selected Language -> getPhrase
        val tipText = Tips.getDescription(tip)
        val isEnvironmentTip = (tip > 2000) and (tip < 3000)
        motionList?.let {
            if (isEnvironmentTip || it.contains(tip)) {
                _actionNameText.postValue(tipText)
            }
        }

        getStringMotionLog(tip).let {
            if (it.isNotEmpty()) {
                motionDone = it
            }
        }
    }

    private fun onLivenessCheckSuccess(paFrame: PaFaceDetectFrame?) {
        _facialRecognitionEvent.postValue(FaceRecognitionEvent.LIVENESS_CHECK_SUCCESS)
        faceDetectorManagerInstanceWrapper.stopFaceDetect()
        cancelCountDownTimer()
        val retry: () -> Unit = {
            _facialRecognitionEvent.postValue(FaceRecognitionEvent.FACE_COMPARE_FAILED_RETRY)
        }
    }

    fun cancelCountDownTimer() {
        countDownTimer.cancelTimer()
    }

    private fun onLivenessCheckFailed(isTimeOut: Boolean, errorCode: Int?) {
        if (isTimeOut) {
            _isForceExit = true to FaceRecognitionEvent.LIVENESS_CHECK_FAILED
            _facialRecognitionEvent.postValue(FaceRecognitionEvent.LIVENESS_CHECK_FAILED)
        } else {
            _isForceExit = true to FaceRecognitionEvent.LIVENESS_CHECK_FAILED_INTERRUPT
            _facialRecognitionEvent.postValue(FaceRecognitionEvent.LIVENESS_CHECK_FAILED_INTERRUPT)
        }
    }

    private fun onMotionDone(tip: Int) {
            motionList?.let {
                if (it.contains(tip)) {
                    onDidMotionDone.postValue(true)
//                    delay(1000L)
//                    onDidMotionDone.postValue(false)
                }
            }
    }

    private val countDownTimer by lazy {
        FaceRecognitionCountDownTimer.newInstance()
    }

    fun resumeCountDown() {
        if (countDownTimer.isHasStart) {
            countDownTimer.resumeTimer()
        }
    }

    fun pauseCountDown() {
        if (countDownTimer.isHasStart) {
            countDownTimer.pauseTimer()
        }
    }

    fun releaseDetector() {
        faceDetectorManagerInstanceWrapper.release()
    }

}