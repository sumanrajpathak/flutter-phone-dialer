package com.sumanrajpathak.flutterphonedialer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener
import java.lang.Exception

/**
 * The `FlutterPhoneDialerPlugin` class is a Flutter plugin responsible for handling phone dialing
 * functionality. It implements the FlutterPlugin and ActivityAware interfaces for plugin
 * initialization and activity attachment.
 */
class FlutterPhoneDialerPlugin : FlutterPlugin, ActivityAware {

    private var handler: FlutterPhoneDialer? = null

    /**
     * Called when the plugin is attached to the Flutter engine. Initializes the plugin and sets up
     * method channel handling.
     *
     * @param binding The FlutterPluginBinding containing the binary messenger for method channel
     * communication.
     */
    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        handler = FlutterPhoneDialer()
        val channel = MethodChannel(binding.binaryMessenger, "flutter_phone_dialer")
        channel.setMethodCallHandler(handler)
    }

    /**
     * Called when the plugin is detached from the Flutter engine. Cleanup can be performed here.
     *
     * @param binding The FlutterPluginBinding, but it is not needed in this case.
     */
    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {}
    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        handler!!.setActivityPluginBinding(activityPluginBinding)
    }

    // ... Activity attachment and detachment methods ...
    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}
    override fun onDetachedFromActivity() {}
}

/**
 * The `FlutterPhoneDialer` class is a helper class for handling method calls and permissions
 * related to phone dialing. It implements the MethodCallHandler and
 * RequestPermissionsResultListener interfaces.
 */
internal class FlutterPhoneDialer : MethodCallHandler, RequestPermissionsResultListener {
    private var activityPluginBinding: ActivityPluginBinding? = null
    private var number: String? = null
    private var flutterResult: MethodChannel.Result? = null

    /**
     * Sets the ActivityPluginBinding for this class, allowing access to the activity for
     * permissions and starting phone calls.
     *
     * @param activityPluginBinding The ActivityPluginBinding associated with the plugin.
     */
    fun setActivityPluginBinding(activityPluginBinding: ActivityPluginBinding) {
        this.activityPluginBinding = activityPluginBinding
        activityPluginBinding.addRequestPermissionsResultListener(this)
    }

    /**
     * Handles incoming method calls from Flutter, such as "dialNumber". Processes the call
     * arguments and initiates phone dialing.
     *
     * @param call The MethodCall containing the method name and arguments.
     * @param result The MethodChannel.Result for sending the result back to Flutter.
     */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        flutterResult = result
        if (call.method == "dialNumber") {
            number = call.argument("number")
            Log.d("FlutterPhoneDialer", "Dialing " + number)
            number = number!!.replace("#".toRegex(), "%23")
            if (!number!!.startsWith("tel:")) {
                number = String.format("tel:%s", number)
            }
            if (permissionStatus != 1) {
                requestsPermission()
            } else {
                result.success(dialNumber(number))
            }
        } else {
            result.notImplemented()
        }
    }
    // ... Permission handling methods ...

    /**
     * Initiates a phone call with the given number. Handles the device's telephony capabilities and
     * starts the call.
     *
     * @param number The phone number to dial.
     * @return `true` if the call was initiated successfully, `false` otherwise.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ): Boolean {
        if (requestCode == CALL_REQ_CODE) {
            for (r in grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    flutterResult!!.success(false)
                    return false
                }
            }
            flutterResult!!.success(dialNumber(number))
        }
        return true
    }

    private fun requestsPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(CALL_PHONE), CALL_REQ_CODE)
    }

    private val permissionStatus: Int
        get() =
                if (ContextCompat.checkSelfPermission(activity, CALL_PHONE) ==
                                PackageManager.PERMISSION_DENIED
                ) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, CALL_PHONE)
                    ) {
                        -1
                    } else {
                        0
                    }
                } else {
                    1
                }

    private fun dialNumber(number: String?): Boolean {
        return try {
            val intent = Intent(if (isTelephonyEnabled) Intent.ACTION_CALL else Intent.ACTION_VIEW)
            intent.data = Uri.parse(number)
            activity.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.d("FlutterPhoneDialer", "error: " + e.message)
            false
        }
    }
    // ... Utility methods ...

    /**
     * Gets the activity associated with the plugin binding for accessing context and starting phone
     * calls.
     *
     * @return The current Activity.
     */
    private val isTelephonyEnabled: Boolean
        get() {
            val tm = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
        }
    private val activity: Activity
        get() = activityPluginBinding!!.activity

    // ... Constants and other properties ...

    companion object {
        private const val CALL_REQ_CODE = 0
        private const val CALL_PHONE = Manifest.permission.CALL_PHONE
    }
}
