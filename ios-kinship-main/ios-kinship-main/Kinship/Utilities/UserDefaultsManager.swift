//
//  UserDefaultsManager.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation

enum UserDefaultsKeys : String {
    case DEVICETOKEN
    case DEVICETOKENDATA
    case FirebasePushToken
    case isUserLoggedIn
    case userDataDix
    case userData
    case isUpdateUserProfile
    case isUpdateDashboard
    case isUpdateAddProperty
    
    case isUpdateRanking
    case isUpdateInbox
    case respondedPostsIds
    
    case isFromRequestSent
}

extension UserDefaults {
    
    func clearUserDefaultsValues() {
        
        userDefaults.removeObject(forKey: UserDefaultsKeys.isUserLoggedIn.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.userDataDix.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.userData.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.isUpdateUserProfile.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.isUpdateDashboard.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.isUpdateAddProperty.rawValue)
        
        userDefaults.removeObject(forKey: UserDefaultsKeys.isUpdateRanking.rawValue)
        userDefaults.removeObject(forKey: UserDefaultsKeys.respondedPostsIds.rawValue)
        
        userDefaults.removeObject(forKey: UserDefaultsKeys.isFromRequestSent.rawValue)
        
        synchronize()
    }
    
}
