#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'flutter_phone_dialer'
  s.version          = '0.1.0'
  s.summary          = 'Open the phone dialer or place calls from a Flutter app.'
  s.description      = <<-DESC
Open the phone dialer or place calls from a Flutter app on Android and iOS.
                       DESC
  s.homepage         = 'https://github.com/sumanrajpathak/flutter-phone-dialer'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Suman Raj Pathak' => 'suman.pathak@wesionary.team' }
  s.source           = { :path => '.' }
  s.source_files     = 'Classes/**/*'
  s.resource_bundles = { 'flutter_phone_dialer_privacy' => ['Resources/PrivacyInfo.xcprivacy'] }
  s.dependency 'Flutter'

  s.platform = :ios, '13.0'
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
