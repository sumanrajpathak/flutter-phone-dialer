package com.sumanrajpathak.flutterphonedialer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener

/**
 * Flutter plugin that opens the phone dialer (`dialNumber`, no permission
 * required) or places a call directly (`callNumber`, requires the
 * CALL_PHONE permission, which the consuming app must declare itself).
 */
class FlutterPhoneDialerPlugin :
    FlutterPlugin,
    ActivityAware,
    MethodChannel.MethodCallHandler,
    RequestPermissionsResultListener {

    private var channel: MethodChannel? = null
    private var applicationContext: Context? = null
    private var activityBinding: ActivityPluginBinding? = null

    /** Result awaiting the outcome of a CALL_PHONE permission request. */
    private var pendingResult: MethodChannel.Result? = null
    private var pendingNumber: String? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, CHANNEL_NAME).also {
            it.setMethodCallHandler(this)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
        applicationContext = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() = onDetachedFromActivity()

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) =
        onAttachedToActivity(binding)

    override fun onDetachedFromActivity() {
        activityBinding?.removeRequestPermissionsResultListener(this)
        activityBinding = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val number = call.argument<String>("number")?.removePrefix("tel:")
        if (number.isNullOrBlank()) {
            result.error(
                "invalid_argument",
                "A non-empty 'number' argument is required.",
                null,
            )
            return
        }
        when (call.method) {
            "dialNumber" -> result.success(startIntent(Intent.ACTION_DIAL, number))
            "callNumber" -> callNumber(number, result)
            else -> result.notImplemented()
        }
    }

    private fun callNumber(number: String, result: MethodChannel.Result) {
        val context = applicationContext
        if (context == null) {
            result.error("no_context", "Plugin is not attached to an engine.", null)
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            result.success(startIntent(callAction(), number))
            return
        }
        val activity = activityBinding?.activity
        if (activity == null) {
            result.error(
                "no_activity",
                "callNumber needs a foreground activity to request the " +
                    "CALL_PHONE permission.",
                null,
            )
            return
        }
        if (pendingResult != null) {
            result.error(
                "already_pending",
                "A CALL_PHONE permission request is already in progress.",
                null,
            )
            return
        }
        pendingResult = result
        pendingNumber = number
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_REQUEST_CODE,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ): Boolean {
        if (requestCode != CALL_REQUEST_CODE) return false
        val result = pendingResult
        val number = pendingNumber
        pendingResult = null
        pendingNumber = null
        if (result != null) {
            val granted = grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            result.success(granted && number != null && startIntent(callAction(), number))
        }
        return true
    }

    /** ACTION_CALL needs telephony; fall back to a view intent otherwise. */
    private fun callAction(): String =
        if (isTelephonyEnabled()) Intent.ACTION_CALL else Intent.ACTION_VIEW

    private fun isTelephonyEnabled(): Boolean {
        val telephony = applicationContext
            ?.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return false
        return telephony.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    private fun startIntent(action: String, number: String): Boolean {
        return try {
            // Uri.fromParts percent-encodes the number (e.g. `#` -> `%23`).
            val intent = Intent(action, Uri.fromParts("tel", number, null))
            val activity = activityBinding?.activity
            if (activity != null) {
                activity.startActivity(intent)
            } else {
                val context = applicationContext ?: return false
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private companion object {
        const val CHANNEL_NAME = "flutter_phone_dialer"
        const val CALL_REQUEST_CODE = 5001
    }
}
