//
//  ChangePasswordScreen.swift
//  Kinship
//
//  Created by iMac on 09/04/24.
//

import UIKit

class ChangePasswordScreen: UIViewController {

//    MARK: - IB-OUTLETS
    @IBOutlet weak var updateButton: UIButton!
    @IBOutlet weak var currentPasswordTextField: UITextField!
    @IBOutlet weak var currentPasswordShowButton: UIButton!
    @IBOutlet weak var newPasswordTextField: UITextField!
    @IBOutlet weak var newPasswordShowButton: UIButton!
    @IBOutlet weak var conformPasswordTextField: UITextField!
    @IBOutlet weak var conformPasswordShowButton: UIButton!
    @IBOutlet weak var currentPasswordView: UIView!
    
//    MARK: - VAIRABLES
    var isForgotPasswordOn = false
    var step: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        manageUI()
        inilization()
    }
    func inilization(){
//        tabBarController?.tabBar.isHidden = true
    }
    func manageUI(){
        self.updateButton.layer.cornerRadius = 25
    }
    @IBAction func onUpdate(_ sender: Any) {
        self.step = "2"
        if self.isForgotPasswordOn {
            if newPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" || conformPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" {
                Utility.showAlert(message: "You must fill all the fields")
            }else if !Utility.isValidPassword(stringPassword: newPasswordTextField.text ?? "") {
                Utility.showAlert(message: "Your password must be more then 8 character and one upper case contain a combination of letters, numbers and special characters")
            }else if newPasswordTextField.text != conformPasswordTextField.text {
                Utility.showAlert(message: "Password and confirm password is must be same")
            } else {
                self.forgotPasswordAuth()
            }
        } else {
            if currentPasswordTextField.text == "" || newPasswordTextField.text == "" || conformPasswordTextField.text == "" {
                Utility.showAlert(message: "You must fill all the fields")
            }else if !Utility.isValidPassword(stringPassword: newPasswordTextField.text ?? "") {
                Utility.showAlert(message: "Your password must be more then 8 character and one upper case contain a combination of letters, numbers and special characters")
            }else if newPasswordTextField.text != conformPasswordTextField.text {
                Utility.showAlert(message: "Password and confirm password is must be same")
            }else {
                self.changePasswordAPI()
            }
        }
    }
    
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onCurrentPasswordShowButton(_ sender: UIButton) {
        self.currentPasswordShowButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.currentPasswordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    @IBAction func onNewPasswordShowButton(_ sender: UIButton) {
        self.newPasswordShowButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.newPasswordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    @IBAction func onConformPasswordShowButton(_ sender: UIButton) {
        self.conformPasswordShowButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.conformPasswordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    @IBAction func onForgotPassword(_ sender: Any) {
        self.step = "1"
        self.forgotPasswordAuth()
    }
}

//MARK: - API CALL
extension ChangePasswordScreen {
    func changePasswordAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ChangePasswordRequest(oldPassword: self.currentPasswordTextField.text ?? "",
                                              password: self.newPasswordTextField.text ?? "",
                                              conformPassword: self.conformPasswordTextField.text ?? "")
            ChangePasswordService.shared.changePassword(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.message{
                    Utility.successAlert(message: data)
                    self?.navigationController?.popViewController(animated: true)
                }else{
                    Utility.successAlert(message: response.message ?? "")
                    self?.navigationController?.popViewController(animated: true)
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
    func forgotPasswordAuth() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = forogtPasswordAuthRequest(step: self.step, 
                                                  password: self.newPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 ? nil : self.newPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
                                                  confirmPassword: self.conformPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 ? nil : self.conformPasswordTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines))
            
            ForgotPasswordServices.shared.forgotPasswordAuth(parameters: param.toJSON()) {[weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let message = response.message {
                    if param.step == "1" {
                        Utility.successAlert(message: message)
                        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "OTPVerificationScreen") as! OTPVerificationScreen
                        vc.isFromUpdatePassword = false
                        vc.isFromForgotPassword = true
                        vc.email = Utility.getUserData()?.email ?? ""
                        vc.type = "3"
                        vc.isForgotPasswordClick = { [weak self] in
                            self?.currentPasswordView.isHidden = true
                            self?.isForgotPasswordOn = true
                        }
                        self?.navigationController?.pushViewController(vc, animated: true)
                    } else {
                        Utility.successAlert(message: message)
                        self?.navigationController?.popViewController(animated: true)
                    }
                }
            } failure: {[weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        } else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
}
