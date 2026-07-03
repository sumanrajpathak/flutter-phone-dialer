import Flutter
import UIKit

/// Flutter plugin that opens the phone dialer.
///
/// iOS never allows placing a call without user interaction, so both
/// `dialNumber` and `callNumber` open the system call prompt.
public class FlutterPhoneDialerPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(
      name: "flutter_phone_dialer",
      binaryMessenger: registrar.messenger())
    let instance = FlutterPhoneDialerPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "dialNumber", "callNumber":
      guard
        let arguments = call.arguments as? [String: Any],
        let number = arguments["number"] as? String,
        !number.isEmpty
      else {
        result(
          FlutterError(
            code: "invalid_argument",
            message: "A non-empty 'number' argument is required.",
            details: nil))
        return
      }
      dial(number: number, result: result)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func dial(number: String, result: @escaping FlutterResult) {
    var rawNumber = number
    if rawNumber.hasPrefix("tel:") {
      rawNumber = String(rawNumber.dropFirst("tel:".count))
    }
    // Percent-encode so characters like `#` (-> %23) survive URL parsing.
    guard
      let encoded = rawNumber.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed),
      let url = URL(string: "tel:\(encoded)"),
      UIApplication.shared.canOpenURL(url)
    else {
      result(false)
      return
    }
    UIApplication.shared.open(url, options: [:]) { success in
      result(success)
    }
  }
}
