//
//  alertScreen.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit

class alertScreen: UIViewController {
    
    //    MARK: - IB OUTLETS
    @IBOutlet weak var alertView: UIView!
    @IBOutlet weak var leavingKinshipTextView: UITextView!
    @IBOutlet weak var cancleButton: UIButton!
    @IBOutlet weak var confirmButton: UIButton!
    @IBOutlet weak var LeavingPurposLabel: UILabel!
    @IBOutlet weak var leavingKinshipView: UIView!
    
    //    MARK: - VARIABLES
    var delegate: alertScreen?
    var isFromDeleteScreen: Bool?
    var back : ((String) -> Void)?
    var goToregister : ((String) -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.manageUI()
        initilize()
//        print(isFromDeleteScreen)
    }
    func initilize(){
        if isFromDeleteScreen == true{
            //            For Delete Account
            self.LeavingPurposLabel.text = "Please tell us why you’re deleting your account."
        }else{
            //            For Leave your Kinship
            self.LeavingPurposLabel.text = "Please tell us why you’re leaving your kinship."
        }
    }
    func manageUI(){
        self.alertView.layer.cornerRadius = 10
        
        self.leavingKinshipView.layer.cornerRadius = 5
        self.leavingKinshipView.layer.borderWidth = 1
        self.leavingKinshipView.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
        
        self.cancleButton.layer.cornerRadius = 15
        self.cancleButton.layer.borderWidth = 1
        self.cancleButton.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
        
        self.confirmButton.layer.cornerRadius = 15
    }
    @IBAction func onCancle(_ sender: Any) {
//        print("Cancel button click")
        self.dismiss(animated: true, completion: nil)
    }
    @IBAction func onConform(_ sender: Any) {
        if isFromDeleteScreen == true{
            if self.leavingKinshipTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
                Utility.showAlert(message: "You must write Delete account reason")
            }else{
                //            For Delete Account
                self.deleteAccountAPI()
//                print(self.isFromDeleteScreen ?? "")
            }
        }else{
            if self.leavingKinshipTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
                Utility.showAlert(message: "You must write Leave kinship reason")
            }else{
                //            For Leave your Kinship
                self.leaveKinshipAPI()
//                print(self.isFromDeleteScreen ?? "")
            }
        }
    }
}

// MARK: - API CALLING
extension alertScreen {

//    Leave kinship API
    func leaveKinshipAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = LeaveKinshipRequest(reason: self.leavingKinshipTextView.text ?? "")
            LeaveYourKinshipServices.shared.leaveKinship(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.message {
                    guard let userdate = Utility.getUserData() else { return }
                    userdate.isProfileCompleted = false
                    Utility.saveUserData(data: userdate.toJSON())
                    Utility.successAlert(message: data)
                    self?.dismiss(animated: true, completion: nil)
                    self?.back?("Back")
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
    
//    Delete Account API
    func deleteAccountAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = LeaveKinshipRequest(reason: self.leavingKinshipTextView.text ?? "")
            LeaveYourKinshipServices.shared.deleteAccount(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                if let data = response?.message {
                    Utility.hideIndicator()
                    Utility.showAlert(message: data)
                    self?.dismiss(animated: true, completion: nil)
                    Utility.removeUserData()
                    self?.goToregister?("Go to register account")
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
        print("delete account Click")
    }
}
