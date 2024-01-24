import android.view.View

class ViewVisibilitySelector(
    private val view: View,
    private val isVisibleNormal: Boolean,
    private val isVisibleLoading: Boolean,
    private val isVisibleSuccess: Boolean,
) {
    fun setToNormalVisibility() {
        view.visibility = isVisibleNormal.trueVisibleOrInvisible()
    }

    fun setToLoadingVisibility() {
        view.visibility = isVisibleLoading.trueVisibleOrInvisible()
    }

    fun setToSuccessVisibility() {
        view.visibility = isVisibleSuccess.trueVisibleOrInvisible()
    }

    private fun Boolean.trueVisibleOrInvisible() =
        if (this) View.VISIBLE else View.INVISIBLE
}