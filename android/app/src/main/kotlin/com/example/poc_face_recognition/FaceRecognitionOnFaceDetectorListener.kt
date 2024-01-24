import com.pingan.ai.face.entity.PaFaceDetectFrame
import kotlin.reflect.KSuspendFunction1

class FaceRecognitionOnFaceDetectorListener(
    private val setTipTextLiveData: (Int, Boolean) -> Unit,
    private val onLivenessCheckSuccess: (PaFaceDetectFrame?) -> Unit,
    private val onLivenessCheckFailed: (Boolean, Int?) -> Unit,
    private val onMotionDone: (Int) -> Unit
) : OnFaceDetectorListener {

    override fun onDetectTips(tip: Int, frame: PaFaceDetectFrame) {
        setTipTextLiveData(tip, false)
    }

    override fun onDetectMotionTips(motionType: Int) {
        setTipTextLiveData(motionType, true)
    }

    override fun onDetectComplete(p0: Int, frames: Array<out PaFaceDetectFrame>?) {
        frames?.filterIndexed { _, paFaceDetectFrame ->
            paFaceDetectFrame != null
        }?.let { frame ->
            val paFrame = try {
                if (frame.size == 1){
                    frame[0]
                }else{
                    frame.random()
                }
            } catch (e: NoSuchElementException) {
                null
            }
            onLivenessCheckSuccess(paFrame)
        }
    }

    override fun onInterruptError(errorCode: Int, p1: MutableList<PaFaceDetectFrame>?) {
        onLivenessCheckFailed(false, errorCode)
    }

    override fun onDetectMotionDone(motion: Int) {
        onMotionDone(motion)
    }

}
