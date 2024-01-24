import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NativeViewFactory(
    private val layoutInflater: LayoutInflater,
    private val lifecycleOwner: LifecycleOwner,
    private val activity: Activity
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    private lateinit var nativeView: NativeView
    private lateinit var cameraProvider: ProcessCameraProvider
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        nativeView = NativeView(context, activity, lifecycleOwner, layoutInflater)
        return nativeView
    }

    fun startCamera() {
        Toast.makeText(activity, "startCamera center", Toast.LENGTH_SHORT).show()
        nativeView.startCamera()
    }

    fun showLoading() {
        nativeView.showLoading()
    }

    fun setUpAndStartDetector(){
        nativeView.setUpAndStartDetector()
    }

}
