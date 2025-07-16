//
//  NotificationSettingScreen.swift
//  Kinship
//
//  Created by iMac on 11/04/24.
//

import UIKit

class NotificationSettingScreen: UIViewController {

//    MARK: - IBOUTLETS
    @IBOutlet weak var allNewPostSwitchButton: UISwitch!
    @IBOutlet weak var directMessageSwitchButton: UISwitch!
    @IBOutlet weak var newEventSwitchButton: UISwitch!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.getNotificationSetting()
     }
    
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onAllNewPost(_ sender: Any) {
        self.manageNotifications(isFrom: 1)
    }
    @IBAction func onDirectMessage(_ sender: Any) {
        self.manageNotifications(isFrom: 2)
    }
    @IBAction func onNewEvent(_ sender: Any) {
        self.manageNotifications(isFrom: 3)
    }
}
// MARK: - Extension
extension NotificationSettingScreen {
    func manageNotifications(isFrom: Int, isApicall: Bool = true){
        if isFrom == 1 {
            if self.allNewPostSwitchButton.isOn == true {
                [self.directMessageSwitchButton, self.newEventSwitchButton].forEach{$0?.isOn = true}
                [self.directMessageSwitchButton, self.newEventSwitchButton].forEach{$0?.isEnabled = false}
            } else {
                [self.directMessageSwitchButton, self.newEventSwitchButton].forEach{$0?.isEnabled = true}
            }
        }
        if isApicall == true {
            self.notificationSetting()
        }
    }
}

//  MARK: - API CALL
extension NotificationSettingScreen {
    
//   Get my notification setting
    func getNotificationSetting(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            NotificationServices.shared.getNotificationSetting { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.notificationSettingResponse {
                    self?.allNewPostSwitchButton.setOn(data.allNewPosts == true ? true : false, animated: false)
                    self?.directMessageSwitchButton.setOn(data.directMessage == true ? true : false, animated: false)
                    self?.newEventSwitchButton.setOn(data.newEvents == true ? true : false, animated: false)
                    self?.manageNotifications(isFrom: 1, isApicall: false)
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
//    Change notification setting
    func notificationSetting(){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)

            let param = NotificationRequest(allNewPosts: allNewPostSwitchButton.isOn, directMessage: directMessageSwitchButton.isOn, newEvents: newEventSwitchButton.isOn)
//            print(param.toJSON())
            NotificationServices.shared.notificationSetting(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
//                Utility.hideIndicator()
//                if let _ = response.message{
//                    Utility.successAlert(message: data)
                    
//                }
            } failure: { [weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        } else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
