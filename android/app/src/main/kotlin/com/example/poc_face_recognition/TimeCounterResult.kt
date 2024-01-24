sealed class TimeCounterResult {
    class TimeLeft(val timeSeconds: Long) : TimeCounterResult()
    object TimeOut : TimeCounterResult()
}