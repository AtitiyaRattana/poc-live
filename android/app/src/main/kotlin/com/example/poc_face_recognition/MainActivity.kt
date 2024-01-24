package com.example.poc_face_recognition

import NativeView
import NativeViewFactory
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import com.example.poc_face_recognition.databinding.ActivityCameraScanBinding
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class MainActivity: FlutterActivity() {
    private val CHANNEL = "channel_face_recognition"
    lateinit var nativeView: NativeViewFactory
    lateinit var nativeViewModel: NativeView
    private lateinit var binding: ActivityCameraScanBinding
    companion object {
        init {
            System.loadLibrary("face_detect")
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, "onRequestPermissionsResult", Toast.LENGTH_SHORT).show()
        if (requestCode == 1111) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "permission grant", Toast.LENGTH_SHORT).show()
                nativeView.startCamera()
            }
            nativeView.showLoading()
            nativeView.setUpAndStartDetector()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camera_scan)


        nativeView = NativeViewFactory(layoutInflater, this, this)
        flutterEngine?.platformViewsController?.registry?.registerViewFactory("LivenessCameraView", nativeView)

        MethodChannel(getFlutterEngine()!!.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method.equals( "close")) {
                    close()
                }

            }
    }

    private fun close() {
        binding.imgClose.setOnClickListener {
            finish()
        }
    }


}

