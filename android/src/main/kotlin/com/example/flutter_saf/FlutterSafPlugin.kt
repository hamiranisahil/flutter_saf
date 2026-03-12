package com.example.flutter_saf

import android.content.Intent
import com.example.flutter_saf.src.FlutterSafEventHandler
import com.example.flutter_saf.src.FlutterSafMethodHandler

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.PluginRegistry

class FlutterSafPlugin : FlutterPlugin, ActivityAware, PluginRegistry.ActivityResultListener {
    private var activity: ActivityPluginBinding? = null

    private lateinit var methodChannel: MethodChannel
    private var methodHandler: FlutterSafMethodHandler? = null

    private lateinit var eventChannel: EventChannel
    private var eventHandler: FlutterSafEventHandler? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_saf/methods")
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_saf/events")
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding
        activity?.addActivityResultListener(this)
        // Method Channel Handler
        methodHandler = FlutterSafMethodHandler(binding.activity)
        methodChannel.setMethodCallHandler(methodHandler)
        // Event Channel Handler
        eventHandler = FlutterSafEventHandler(binding.activity)
        eventChannel.setStreamHandler(eventHandler)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activity?.removeActivityResultListener(this)
        activity = null
        methodHandler = null
        eventHandler = null
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Boolean {
        return methodHandler?.onActivityResult(requestCode, resultCode, data) ?: false
    }

}