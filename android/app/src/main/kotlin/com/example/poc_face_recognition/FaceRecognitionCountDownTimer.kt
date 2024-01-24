import android.os.CountDownTimer
import android.os.SystemClock
import timber.log.Timber
import java.lang.Math.floor

class FaceRecognitionCountDownTimer {

    private var millisInFuture: Long = 0L
    private var countDownTimer: CountDownTimer? = null
    private var _isHasStart: Boolean = false
    val isHasStart: Boolean
        get() = _isHasStart
    private var mPauseTimeRemaining: Long = millisInFuture
    private val isPause: Boolean
        get() = mPauseTimeRemaining > 0
    private val isRunning: Boolean
        get() = !isPause
    private var mStopTimeInFuture: Long = 0L
    var onTickCallBack: ((Long) -> Unit)? = null
    var onFinishCallback: (() -> Unit)? = null

    fun start(
        millisInFuture: Long,
        onTickCallBack: (Long) -> Unit,
        onFinishCallBack: () -> Unit) {
        this.onTickCallBack = onTickCallBack
        this.onFinishCallback = onFinishCallBack
        mStopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture;
        startTimer(millisInFuture)
    }

    private
    fun startTimer(millisInFuture: Long) {
        this.millisInFuture = millisInFuture
        countDownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.e("time: object - onTick = $millisUntilFinished")
                this@FaceRecognitionCountDownTimer.onTickCallBack?.invoke(floor(millisUntilFinished / 1000.0).toLong())
            }

            override fun onFinish() {
                onFinishCallback?.invoke()
            }

        }
        _isHasStart = true
        countDownTimer?.start()
    }

    fun cancelTimer() {
        _isHasStart = false
        countDownTimer?.cancel()
    }

    fun pauseTimer() {
        if (isRunning) {
            mPauseTimeRemaining = timeLeft()
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    fun resumeTimer() {
        if (isPause) {
            millisInFuture = mPauseTimeRemaining
            mStopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture
            mPauseTimeRemaining = 0
            startTimer(millisInFuture)
        }
    }

    private fun timeLeft(): Long {
        var millisUntilFinished = 0L
        if (isPause) {
            millisUntilFinished = mPauseTimeRemaining
        } else {
            millisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime()
            if (millisUntilFinished < 0) millisUntilFinished = 0
        }
        return millisUntilFinished
    }

    companion object {
        fun newInstance(): FaceRecognitionCountDownTimer = FaceRecognitionCountDownTimer()
    }
}