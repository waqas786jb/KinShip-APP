//
//  ViewController.swift
//  Kinship
//
//  Created by Vikram's Macbook on 23/02/24.
//

import UIKit
import CoreLocation
import MapKit
import LGSideMenuController

class SplashScreen: UIViewController {
    
    let userData = Utility.getUserData()
    let appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //Delay navigation to new screen by 3 seconds
//        self.checkVersionAPI()
        self.appNavigation()
    }
}
extension SplashScreen {
    func appNavigation(){
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) { [weak self] in
            
            if self?.userData == nil {
                let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "LoginScreen") as! LoginScreen
                self?.navigationController?.pushViewController(vc, animated: true)
            }
            else {
                if self?.userData?.isVerify == false {   // For OTP Validation or not
                    let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "LoginScreen") as! LoginScreen
                    self?.navigationController?.pushViewController(vc, animated: true)
                }
                else {
                    if self?.userData?.isProfileCompleted == false {     // For profile completed or not
                        if self?.userData?.profile?.step == 1{
                            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "CompleteProfileScreen") as! CompleteProfileScreen
                            vc.isFromRoot = true
                            self?.navigationController?.pushViewController(vc, animated: true)
                        }else{
                            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "BringYouScreen") as! BringYouScreen
                            self?.navigationController?.pushViewController(vc, animated: true)
//                            let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "MyCommunitiesScreen") as! MyCommunitiesScreen
//                            self?.navigationController?.pushViewController(vc, animated: true)
                        }
                    }
                    else {
                        Utility.setTabBarRoot()
//                        let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "MyCommunitiesScreen") as! MyCommunitiesScreen
//                        self?.navigationController?.pushViewController(vc, animated: true)
                    }
                }
            }
        }
    }
    
    func forseUpdateVarsion(){
        let alert = UIAlertController(title: APPLICATION_NAME, message: "An update required before the user is allowed to continue using the application", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Update",
                                      style: UIAlertAction.Style.destructive,
                                      handler: {(_: UIAlertAction!) in
            if let url = URL(string: "https://apps.apple.com/in/app/instagram/id389801252") {
                UIApplication.shared.open(url)
            }
        }))
        
        DispatchQueue.main.async {
            self.present(alert, animated: true, completion: nil)
        }
    }
    func updateVarsion(){
        let alert = UIAlertController(title: APPLICATION_NAME, message: "Application new version is avalable click update now for new version", preferredStyle: .alert)
        
            alert.addAction(UIAlertAction(title: "Update later", style: UIAlertAction.Style.default, handler: { _ in
                alert.dismiss(animated: true)
                self.appNavigation()
            }))
        alert.addAction(UIAlertAction(title: "Update now",
                                      style: UIAlertAction.Style.destructive,
                                      handler: {(_: UIAlertAction!) in
            if let url = URL(string: "https://apps.apple.com/in/app/instagram/id389801252") {
                UIApplication.shared.open(url)
            }
        }))
            
        DispatchQueue.main.async {
            self.present(alert, animated: true, completion: nil)
        }
    }
}
extension SplashScreen {
    func checkVersionAPI(){
            if Utility.isInternetAvailable() {
                let param = ApplicationVersion(type: "1", version: self.appVersion)
//                print(param.toJSON())
                LoginServices.shared.checkAppVersion(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                    Utility.hideIndicator()
                    if let data = response.versionCheckResponse{
                        if data == 1 {
                            self?.forseUpdateVarsion()
                        }else if data == 2{
                            self?.updateVarsion()
                        }else if data == 3{
                            self?.appNavigation()
                        }
                    }
                } failure: { [weak self] (error) in
                    Utility.showAlert(vc: self, message: error)
                }
            } else {
                Utility.hideIndicator()
                Utility.showNoInternetConnectionAlertDialog(vc: self)
            }
    }
}
