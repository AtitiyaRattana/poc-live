//import android.app.Activity
//import android.app.Activity.RESULT_CANCELED
//import android.content.Context
//import android.content.Intent
//import androidx.activity.result.contract.ActivityResultContract
//import com.example.poc_face_recognition.databinding.ActivityCameraScanBinding
//
//class FaceRecognitionResultContract :
//    ActivityResultContract<FaceRecognitionIntentData, FaceRecognitionResultContract.FaceRecognitionResult>() {
//
//    companion object {
//        const val RESULT_DISAGREE_CONSENT = 110
//        const val RESULT_LIVENESS_FAILED = 101
//        const val RESULT_LIVENESS_FAILED_INTERRUPT = 108
//        const val RESULT_FACE_COMPARE_FAILED = 102
//        const val RESULT_FACE_COMPARE_FAILED_RETRY = 105
//        const val RESULT_EXIT = 103
//        const val RESULT_GENERAL_ERROR_OCCURRED = 104
//        const val RESULT_ON_BACK = 106
//
//        const val EXTRA_FACIAL_RECOGNITION_DATA = "EXTRA_FACIAL_RECOGNITION_DATA"
//
//        fun getDataModelFromIntent(intent: Intent): FaceRecognitionIntentData? =
//            intent.getParcelableExtra(EXTRA_FACIAL_RECOGNITION_DATA)
//    }
//
//    override fun createIntent(context: Context, input: FaceRecognitionIntentData): Intent =
//        Intent(context, ActivityCameraScanBinding::class.java)
//            .putExtra(EXTRA_FACIAL_RECOGNITION_DATA, input)
//
//    override fun parseResult(
//        resultCode: Int,
//        intent: Intent?
//    ): FaceRecognitionResult {
//        return when (resultCode) {
//            Activity.RESULT_OK -> {
//                val uuid = intent?.getStringExtra("ud_extra") ?: ""
//                FaceRecognitionResult.SUCCESS(data = uuid)
//            }
//            RESULT_LIVENESS_FAILED -> FaceRecognitionResult.LIVENESS_CHECK_FAILED
//            RESULT_LIVENESS_FAILED_INTERRUPT -> FaceRecognitionResult.LIVENESS_CHECK_FAILED_INTERRUPT
//            RESULT_FACE_COMPARE_FAILED -> FaceRecognitionResult.FACE_COMPARE_FAILED
//            RESULT_EXIT -> FaceRecognitionResult.EXIT_ACTIVATION_AND_GO_HOME
//            RESULT_GENERAL_ERROR_OCCURRED -> FaceRecognitionResult.GENERAL_ERROR_OCCURRED
//            RESULT_CANCELED -> FaceRecognitionResult.CANCEL_BY_USER
//            RESULT_FACE_COMPARE_FAILED_RETRY -> FaceRecognitionResult.FACE_COMPARE_FAILED_RETRY
//            RESULT_DISAGREE_CONSENT -> FaceRecognitionResult.DISAGREE_CONSENT
////            RESULT_COMMON_LIVENESS_SUCCESS -> FacialRecognitionResult.COMMON_LIVENESS_SUCCESS
//            else -> FaceRecognitionResult.UNKNOWN
//        }
//    }
//
//    sealed class FaceRecognitionResult(open val data: String? = null) {
//        data class SUCCESS(override val data: String) : FaceRecognitionResult(data)
//        object LIVENESS_CHECK_FAILED : FaceRecognitionResult("")
//        object LIVENESS_CHECK_FAILED_INTERRUPT : FaceRecognitionResult("")
//        object FACE_COMPARE_FAILED : FaceRecognitionResult("")
//        object FACE_COMPARE_FAILED_RETRY : FaceRecognitionResult("")
//        object CANCEL_BY_USER : FaceRecognitionResult("")
//        object GENERAL_ERROR_OCCURRED : FaceRecognitionResult("")
//        object EXIT_ACTIVATION_AND_GO_HOME : FaceRecognitionResult("")
//        object DISAGREE_CONSENT : FaceRecognitionResult("")
//        object UNKNOWN : FaceRecognitionResult("")
//        object COMMON_LIVENESS_SUCCESS : FaceRecognitionResult("")
//    }
//
//}