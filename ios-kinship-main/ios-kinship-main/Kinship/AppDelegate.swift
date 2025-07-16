//
//  AppDelegate.swift
//  Kinship
//
//  Created by Vikram's Macbook on 23/02/24.
//

import UIKit
import IQKeyboardManagerSwift
import LGSideMenuController
import SocketIO
import AVFoundation
import FirebaseCore
import FirebaseMessaging
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var navVC:UINavigationController?
    let gcmMessageIDKey = "gcm.Message_Id"
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.shouldResignOnTouchOutside = true
        self.setRootViewController()
        
        // For Push Notification
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        Messaging.messaging().isAutoInitEnabled = true
        UNUserNotificationCenter.current().delegate = self
        
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )
        application.registerForRemoteNotifications()
        
//        self.connectSocket(){ value in
            
//        }
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
        print("Device token: \(deviceToken)")
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error.localizedDescription)")
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        print("Resign Active")
//        NotificationCenter.default.post(name: .socketDisconnect, object: nil, userInfo: nil)
        SocketHelper.shared.disconnectSocket()
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        print("applicationDidBecomeActive")
        if SocketHelper.shared.checkConnection() == false {
//            Utility.showAlert(message: "Socket Disconnect")
            self.connectSocket() { value in
//                Utility.showAlert(message: "Socket connect")
                NotificationCenter.default.post(name: .socketAgainConnect, object: nil, userInfo: nil)
            }
        } else {
            Utility.showAlert(message: "Socket Alredy connect")
        }
//        NotificationCenter.default.post(name: .socketAgainConnect, object: nil, userInfo: nil)
    }
    
    // MARK: - NAVIGATION CONTROLLER
    func setRootViewController(){
        let vc = STORYBOARD.main.instantiateViewController(withIdentifier: "SplashScreen") as! SplashScreen
        self.navVC = UINavigationController.init(rootViewController: vc)
        self.navVC?.isNavigationBarHidden = true
        self.navVC?.interactivePopGestureRecognizer?.isEnabled = false
        self.window?.rootViewController = nil
        self.window?.rootViewController = self.navVC
        self.window?.tintColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 1)
        self.window?.makeKeyAndVisible()
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        SocketHelper.shared.disconnectSocket()
    }
    
    func connectSocket(success: @escaping (String) -> ()){
        if let _ = Utility.getUserData(){
            SocketHelper.shared.connectSocket(completion: { val in
                if(val){
                    print("==== socket connected ====")
                    var parameter = [String: Any]()
                    parameter = ["senderId": Utility.getUserData()?.Id ?? ""]
                    SocketHelper.Events.UpdateStatusToOnline.emit(params: parameter)
                    success("==== socket connected ====")
                }else{
                    print("==== socket did't connected ====")
                }
            })
        }else{
            print("data getting nil")
        }
    }
    
}

// MARK: - For push notification
extension AppDelegate: UNUserNotificationCenterDelegate {

    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse) async {
        let userInfo = response.notification.request.content.userInfo
        
        print(userInfo)
    }
    
//    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
//        // Handle the notification presentation here
//        completionHandler([.alert, .sound, .badge])
//    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        // Handle the received remote notification here

        // Print the notification payload
        print("Received remote notification: \(userInfo)")

        // Process the notification content
        if let aps = userInfo["aps"] as? [String: Any], let alert = aps["alert"] as? String {
            // Extract information from the notification payload
            print("Notification message: \(alert)")
        }
        
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }

        // Indicate the result of the background fetch to the system
        completionHandler(UIBackgroundFetchResult.newData)
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        let userInfo = notification.request.content.userInfo

        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)

        // ...

        // Print full message.
        print(userInfo)

        // Change this to your preferred presentation option
        return [[.alert, .sound]]
      }
}
                            
extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")
        
        let dataDict: [String: String] = ["token": fcmToken ?? ""]
        NotificationCenter.default.post(
            name: Notification.Name("FCMToken"),
            object: nil,
            userInfo: dataDict
        )
        self.registerForPushNotification(token: fcmToken ?? "")
    }
}

extension AppDelegate {
    func registerForPushNotification(token: String){
        if Utility.isInternetAvailable() {
            let param = fcmTokenRequest(token: token, deviceId: UIDevice.current.identifierForVendor!.uuidString, platform: "iOS")
            LoginServices.shared.fcmRegister(parameters: param.toJSON()) {[weak self] (StatusCode, response) in
                if let _ = response.message {
//                    print(data)
                }
            } failure: {[weak self] (error) in
                print(error)
            }
        }else {
            print("Please connect internet")
        }
        
    }
}
