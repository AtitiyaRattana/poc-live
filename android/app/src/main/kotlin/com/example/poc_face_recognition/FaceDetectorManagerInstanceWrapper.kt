import android.content.Context
import com.pingan.ai.face.control.LiveFaceConfig
import com.pingan.ai.face.entity.PaFaceDetectFrame
import com.pingan.ai.face.manager.PaFaceDetectorManager
import com.pingan.ai.face.manager.impl.OnPaFaceDetectorListener

class FaceDetectorManagerInstanceWrapper {
    private var _paFaceDetectorManager: PaFaceDetectorManager? = null

    private val manager: PaFaceDetectorManager
        get() {
            if (_paFaceDetectorManager == null) {
                _paFaceDetectorManager = PaFaceDetectorManager.getInstance()
            }
            return _paFaceDetectorManager!!
        }

    val isHasStart: Boolean
        get() = manager.isHasStart

    fun setLoggerEnable(enable: Boolean) =
        manager.setLoggerEnable(enable)

    fun initFaceDetector(context: Context, liveFaceConfig: LiveFaceConfig) =
        manager.initFaceDetector(context, liveFaceConfig)

    fun initFaceDetector(context: Context) =
        manager.initFaceDetector(context)

    fun setOnFaceDetectorListener(listener: OnFaceDetectorListener) {
        manager.setOnFaceDetectorListener(object : OnPaFaceDetectorListener() {
            override fun onDetectTips(tip: Int, frame: PaFaceDetectFrame) {
                listener.onDetectTips(tip, frame)
            }

            override fun onDetectMotionTips(motionType: Int) {
                listener.onDetectMotionTips(motionType)
            }

            override fun onDetectComplete(p0: Int, frames: Array<out PaFaceDetectFrame>?) =
                listener.onDetectComplete(p0, frames)

            override fun onInterruptError(errorCode: Int, p1: MutableList<PaFaceDetectFrame>?) {
                listener.onInterruptError(errorCode, p1)
            }

            override fun onDetectMotionDone(id: Int) {
                listener.onDetectMotionDone(id)
            }
        })
    }

    fun startFaceDetect() =
        manager.startFaceDetect()

    fun stopFaceDetect() =
        manager.stopFaceDetect()

    fun release() =
        manager.release()

    fun setMotions(motionList: List<Int>) =
        manager.setMotions(motionList)

    fun detectPreviewFrame(var1: Int, var2: ByteArray, var3: Int, var4: Int, var5: Int, var6: Int) =
        manager.detectPreviewFrame(var1, var2, var3, var4, var5, var6)
}