import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.example.poc_face_recognition.R
import com.example.poc_face_recognition.databinding.ActivityCameraScanBinding
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

class ActivityCameraScanActivity: AppCompatActivity(){

    lateinit var nativeView: NativeView
    lateinit var binding: ActivityCameraScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scan)

//            nativeView.timeCounter.value.let {
//                if (it is TimeCounterResult.TimeLeft) {
//                    updateTimerText(it.timeSeconds)
//                }
//            }
//            nativeView.actionNameText.value?.let {
//                updateActionNameText(it)
//            }

            showLoading()
//        setStatusBarTrustNavyColor()

            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight =
                if (resourceId > 0) {
                    resources.getDimensionPixelSize(resourceId)
                } else {
                    0
                }

            with(binding) {
                (imgClose.layoutParams as? ConstraintLayout.LayoutParams)?.let {
                    it.topMargin += statusBarHeight
                }
//            imgClose.set {
//                onClose()
//            }
            }

            try {
//            setUpViewModel()
                setUpAndStartDetector()
                //setUpAndStartCamera() == startCamera in nativeView
            } catch (e: Exception) {
            }
            // log
            observeStateFlow()
            observeLiveData()
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

    override fun finish() {
        super.finish()
    }

    override fun onBackPressed() {
        onClose()
    }

    private fun onClose() {
        setResult(RESULT_CANCELED)
        binding.imgClose
        finish()
    }

    override fun onResume() {
        super.onResume()
        nativeView.resumeCountDown()
    }

    override fun onPause() {
        super.onPause()
        nativeView.pauseCountDown()
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        nativeView.releaseDetector()
        super.onDestroy()
    }



    private fun observeLiveData() {
        nativeView.actionNameText.observe(::getLifecycle) {
            updateActionNameText(it)
        }
        nativeView.faceRecognitionEvent.observe(::getLifecycle) {
            it?.let { onEvent(it) }
        }
        nativeView.circleBorder.observe(::getLifecycle) {
            binding.previewContainer.isVisible = it
        }
        nativeView.onDidMotionDone.observe(::getLifecycle) {
            binding.faceCircleBorder.isVisible = it
            binding.faceGreenCheckedIcon.isVisible = it
        }
    }

    private fun onEvent(event: NativeView.FaceRecognitionEvent) {
        when (event) {
            NativeView.FaceRecognitionEvent.TIMER_SHOULD_START -> {
//                viewModel.startCountDownTimer()
                showNormalUI()
            }

            NativeView.FaceRecognitionEvent.LIVENESS_CHECK_SUCCESS -> {
                onFaceRecognitionSuccess()
            }
            else -> {}
        }
    }

    private fun onFaceRecognitionSuccess() {
        lifecycleScope.launch {
            showResultSuccessUI()
            cameraProvider.unbindAll()
        }
    }

    private fun showResultSuccessUI() {
        with(binding) {
            previewContainer.isActivated = true
            viewVisibilitySelectors.forEach {
                it.setToSuccessVisibility()
            }
        }
    }

    private fun showNormalUI() {
        binding.previewContainer.isActivated = false
        viewVisibilitySelectors.forEach { it.setToNormalVisibility() }
    }

    private fun setUpAndStartDetector() {
        nativeView.faceDetectorManagerInstanceWrapper =
            FaceDetectorManagerInstanceWrapper()
        nativeView.initialAndStartDetectorManager(this)
    }

//    private fun setUpViewModel() {
//        val intentData = FaceRecognitionResultContract.getDataModelFromIntent(intent)
//        if (intentData == null) {
//            throw FaceRecognitionNoIntentDataException
//        } else {
//            nativeView.faceRecognitionIntentData = intentData
//            nativeView.setNumOfLivenessFactor(intentData.numLivenessFactor)
//        }
////        nativeView.showNoConnectionBottomSheet = ::showNoInternetConnectionBottomSheetWithRetry
////        nativeView.isNetworkAvailable = {
////            isNetworkAvailable(this)
////        }
//    }


    private fun showLoading() {
        binding.previewContainer.isActivated = false
        viewVisibilitySelectors.forEach { it.setToLoadingVisibility() }
    }

    private fun updateActionNameText(text: String) {
        binding.facialRecognitionTip.text = text
    }

    private fun observeStateFlow() {
        lifecycleScope.launch {
            lifecycle.whenResumed {
                nativeView.timeCounter.collect {
                    onTimeCounterChange(it)
                }
            }
        }
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

    private fun updateTimerText(secLeft: Long) {
        binding.tvTimer.text = secLeft.toTimeFormat()
    }

    private fun Long.toTimeFormat(): String =
        "${this / 60}:${(this % 60).toString().padStart(2, '0')}"

}