//
//  KinshipNameChangeScreen.swift
//  Kinship
//
//  Created by iMac on 14/06/24.
//

import UIKit
import IQKeyboardManagerSwift

class KinshipNameChangeScreen: UIViewController {

//    MARK: - IB-OUTLETS
    @IBOutlet weak var kinshipNameTextField: UITextField!
    
//    MARK: - VARIABLES
    var groupId: String?
    var kinshipName: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
//        self.initilize()
        // Do any additional setup after loading the view.
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
           self.view.endEditing(true)
       }
    
    func initilize(){
        UserDefaults.standard.setValue(self.kinshipNameTextField.text, forKey: "GROUP_NAME")
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.kinshipNameTextField.text = UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? ""
    }
    @IBAction func onCancle(_ sender: Any) {
        self.dismiss(animated: true)
    }
    @IBAction func onOkay(_ sender: Any) {
        if self.kinshipNameTextField.text == UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? ""{
            self.dismiss(animated: true)
        }else if self.kinshipNameTextField.text == ""{
            Utility.showAlert(message: "Group name is not allow to be empty")
        }else{
            if self.kinshipNameTextField.text != "" && !(self.kinshipNameTextField.text ?? "").removeWhiteSpace().isEmpty{
                self.kinshipName = self.kinshipNameTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
//                self.nameChangeAPI()
                let nameDataDict:[String: String] = ["name": self.kinshipName ?? ""]
                NotificationCenter.default.post(name: .nameChangeNotification, object: nil, userInfo: nameDataDict)
                UserDefaults.standard.setValue(self.kinshipName ?? "", forKey: "GROUP_NAME")
                self.dismiss(animated: true)
            }
        }
    }
}

// MARK: - NOTIFICATION OBSERVER
extension Notification.Name {
    static let nameChangeNotification = Notification.Name("GROUP_NAME_CHANGE")
    
    static let disLikeNotification = Notification.Name("DISLIKE_MESSAGE")
    
    static let socketDisconnect = Notification.Name("SOCKET_DISCONNECT")
    
    static let socketAgainConnect = Notification.Name("SOCKET_AGAINCONNECT")
}

//MARK: - API CALL
extension KinshipNameChangeScreen {
    func nameChangeAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = KinshipNameChangeResponse(groupId: self.groupId ?? "", groupName: self.kinshipName ?? "")
            GroupChatServices.shared.kinshipGroupNameChange(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                guard let self = self else { return }
                Utility.hideIndicator()
                if let data = response.message{
                    Utility.successAlert(message: data)
                    let nameDataDict:[String: String] = ["name": self.kinshipName ?? ""]
                    NotificationCenter.default.post(name: .nameChangeNotification, object: nil, userInfo: nameDataDict)
                    UserDefaults.standard.setValue(self.kinshipName ?? "", forKey: "GROUP_NAME")
                    self.dismiss(animated: true)
                }
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
