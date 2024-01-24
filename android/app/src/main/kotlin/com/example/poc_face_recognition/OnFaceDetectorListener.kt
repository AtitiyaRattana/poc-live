import com.pingan.ai.face.entity.PaFaceDetectFrame

interface OnFaceDetectorListener {
    fun onDetectTips(tip: Int, frame: PaFaceDetectFrame)
    fun onDetectMotionTips(motionType: Int)
    fun onDetectComplete(p0: Int, frames: Array<out PaFaceDetectFrame>?)
    fun onInterruptError(errorCode: Int, p1: MutableList<PaFaceDetectFrame>?)
    fun onDetectMotionDone(motion: Int)
}