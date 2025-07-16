//
//  Constant.swift
//  Kinship
//
//  Created by Vikram's Macbook on 23/02/24.
//

import UIKit

let APPLICATION_NAME                     = "Kinship"

//MARK: - Storyboard identifiers
struct STORYBOARD {
    static let main                 = UIStoryboard(name : "Main", bundle : Bundle.main)
    static let authentication       = UIStoryboard(name : "Authentication", bundle : Bundle.main)
    static let bringYou             = UIStoryboard(name : "BringYou", bundle : Bundle.main)
    static let tabbar               = UIStoryboard(name : "Tabbar", bundle : Bundle.main)
    static let home                 = UIStoryboard(name : "Home", bundle : Bundle.main)
    static let events               = UIStoryboard(name : "Events", bundle : Bundle.main)
    static let messages             = UIStoryboard(name : "Messages", bundle : Bundle.main)
    static let settings             = UIStoryboard(name : "Settings", bundle : Bundle.main)
    static let members              = UIStoryboard(name : "Members", bundle : Bundle.main)
    static let notification         = UIStoryboard(name : "Notification", bundle : Bundle.main)
    static let editprofile          = UIStoryboard(name : "EditProfile", bundle: Bundle.main)
    static let changepassword       = UIStoryboard(name : "ChangePassword", bundle: Bundle.main)
    static let updateProfileDetails = UIStoryboard(name : "UpdateContachDetails", bundle: Bundle.main)
    static let eventDetails         = UIStoryboard(name : "EventDetails", bundle: Bundle.main)
    static let leaveKinship         = UIStoryboard(name : "LeaveKinship", bundle: Bundle.main)
    static let Community            = UIStoryboard(name : "Community", bundle: Bundle.main)
//    static let alert
}

//MARK: - Global Variables
let isAppInTestMode                     = false // true or false
let userDefaults = UserDefaults.standard
let appDelegate                         = UIApplication.shared.delegate as! AppDelegate
let DEVICE_UNIQUE_IDETIFICATION: String = UIDevice.current.identifierForVendor!.uuidString
let DEVICE_TYPE: String                 = "iOS"
let IS_LOGIN                            = "is_login"
var CHAT_GROUP_ID: String?
var TOTAL_GROUP_MEMBRS: Int?
var IS_GROUP_CREATED                    = false
var GROUP_MEMBER_COUNT: Int?

//MARK: - Session Key
let USER_DATA       = "user_data"
var ChatViewController: UIViewController?


//MARK: -
func getFileName() -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyyMMddHHmmss"
    return dateFormatter.string(from: Date())
}

var topSafeArea: CGFloat{
    if #available(iOS 11.0, *) {
        let window = UIApplication.shared.keyWindow
        return window?.safeAreaInsets.top ?? 0
    }
    return 0
}
