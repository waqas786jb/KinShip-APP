//
//  SettingsScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit
import WebKit
import SafariServices

class SettingsScreen: UIViewController {
    
//    MARK: - IBOUTLETS
    @IBOutlet weak var profileBackgroundView: UIView!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        manageUI()
        self.tabBarController?.tabBar.isHidden = false
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let userData = Utility.getUserData()
        self.nameLabel.text = "\(Utility.getUserData()?.profile?.firstName ?? "") \(Utility.getUserData()?.profile?.lastName ?? "")"
        self.emailLabel.text = userData?.email
        Utility.setImage(userData?.profile?.profileImage, imageView: profileImageView)
        self.tabBarController?.tabBar.isHidden = false
    }
    func manageUI() {
        profileBackgroundView.layer.cornerRadius = 40
        profileImageView.layer.cornerRadius = 30
    }

//    MARK: - IBOUTLETS
    @IBAction func onLogOut(_ sender: Any) {
        self.openLogoutAlert()
    }
    @IBAction func onNotificationSetting(_ sender: Any) {
        let vc = STORYBOARD.notification.instantiateViewController(withIdentifier: "NotificationSettingScreen") as! NotificationSettingScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    @IBAction func onEditProfile(_ sender: Any) {
        let vc = STORYBOARD.editprofile.instantiateViewController(withIdentifier: "EditProfileScreen") as! EditProfileScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onChangePassword(_ sender: Any) {
        let vc = STORYBOARD.changepassword.instantiateViewController(withIdentifier: "ChangePasswordScreen") as! ChangePasswordScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onUpdateContactDetails(_ sender: Any) {
        let vc = STORYBOARD.updateProfileDetails.instantiateViewController(withIdentifier: "UpdateContactDetailsScreen") as! UpdateContactDetailsScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onEventDetails(_ sender: Any) {
        let vc = STORYBOARD.eventDetails.instantiateViewController(withIdentifier: "EventDetails") as! EventDetails
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onLeaveKinship(_ sender: Any) {
        let vc = STORYBOARD.leaveKinship.instantiateViewController(withIdentifier: "LeaveKinshipScreen") as! LeaveKinshipScreen
        vc.isFromDeleteScreen = false
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onDeleteAccount(_ sender: Any) {
        let vc = STORYBOARD.leaveKinship.instantiateViewController(withIdentifier: "LeaveKinshipScreen") as! LeaveKinshipScreen
        vc.isFromDeleteScreen = true
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func iroidSolutionButton(_ sender: Any) {
        guard let iroid = URL(string: "https://iroidsolutions.com/") else{ return }
        let sfSafari = SFSafariViewController(url: iroid)
        present(sfSafari, animated: true)
    }
    @IBAction func onHelp(_ sender: Any) {
        let vc = STORYBOARD.settings.instantiateViewController(withIdentifier: "HelpScreen") as! HelpScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onTermsAndConditions(_ sender: Any) {
        self.termsAndConditions()
    }
}

// MARK: - API CALL

extension SettingsScreen {

    func logOutApi(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)

            let param = LogOutRequest(deviceId: UIDevice.current.identifierForVendor!.uuidString)

            LoginServices.shared.logOut (parameters: param.toJSON()) { [weak self] (StatusCode, response) in

                if let data = response.message {
                    Utility.removeUserData()
                    Utility.hideIndicator()
                    Utility.showAlert(message: data)
                    Utility.setLoginRoot()
                }
            } failure: { [weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
    func openLogoutAlert(){
        let alert = UIAlertController(title: APPLICATION_NAME, message: "Are you sure you want to logout?", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertAction.Style.default, handler: { _ in
            alert.dismiss(animated: true)
        }))
        alert.addAction(UIAlertAction(title: "Logout",
                                      style: UIAlertAction.Style.destructive,
                                      handler: {(_: UIAlertAction!) in
            //Sign out action
            self.logOutApi()
        }))
        
        DispatchQueue.main.async {
            self.present(alert, animated: true, completion: nil)
        }
        
    }
    
    func termsAndConditions() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            LoginServices.shared.termsAndconditionsApi(parameters: [:]) {[weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let webUrl = response?.termsAndConditions {
                    Utility.openWebView(urlString: webUrl.url ?? "")
                }
            } failure: {[weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
