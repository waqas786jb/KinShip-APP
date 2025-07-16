//
//  LoginScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 29/02/24.
//

import UIKit

class LoginScreen: UIViewController {
    
    //MARK: - OUTLETS
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var passwordVisibilityButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    //MARK: - IBACTIONS
    @IBAction func onChangePasswordVisibility(_ sender: UIButton) {
        self.passwordVisibilityButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.passwordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    
    
    @IBAction func onForgotPassword(_ sender: UIButton) {
        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "ForgotPasswordScreen") as! ForgotPasswordScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    @IBAction func onLogin(_ sender: UIButton) {
        
            if let errorMessage = self.isValidate() {
                Utility.showAlert(message: errorMessage)
            }else{
                self.loginUser()
            }
    }
    
    @IBAction func onRegister(_ sender: UIButton) {
        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "RegistrationScreen") as! RegistrationScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
}

//MARK: - EXTENSION
extension LoginScreen{
    
    func isValidate() -> String? {
        if (self.emailTextField.text ?? "").removeWhiteSpace().isEmpty {
            return "Please enter email"
        }
        else if !Utility.validateEmail(emailTextField.text ?? "") {
            return "Please enter valid email"
        }
        else if (self.passwordTextField.text ?? "").removeWhiteSpace().isEmpty{
            return "Please enter password"
        }
        else if (self.passwordTextField.text ?? "").removeWhiteSpace().count < 8 {
            return "Password Should be Minimum 8 Character"
        }
        return nil
    }
}

//MARK: - API CALL
extension LoginScreen {
    func loginUser() {
        if Utility.isInternetAvailable() {
            
            Utility.showIndicator(view: view)
            
            let param = LoginRequest(email: self.emailTextField.text ?? "", password: self.passwordTextField.text ?? "")
            
            LoginServices.shared.logIn(parameters: param.toJSON()) {[weak self] (StatusCode, response) in
                
                if let data = response.logInResponse {
                    
                    Utility.saveUserData(data: data.toJSON())
                    
                    if data.isVerify == true {
                        Utility.hideIndicator()
                        if Utility.getUserData()?.isProfileCompleted == false {
                            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "BringYouScreen") as! BringYouScreen
                            self?.navigationController?.pushViewController(vc, animated: true)
                        }else {
                            Utility.setTabBarRoot()
                        }
                        
                    }else {
                        Utility.hideIndicator()
                        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "OTPVerificationScreen") as! OTPVerificationScreen
                        vc.type = "1"
                        vc.email = self?.emailTextField.text ?? ""
                        self?.navigationController?.pushViewController(vc, animated: true)
                    }
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
