//
//  RegistrationScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 29/02/24.
//

import UIKit

class RegistrationScreen: UIViewController {
    
    //MARK: - OUTLETS
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var confirmPasswordTextField: UITextField!
    @IBOutlet weak var passwordVisibilityButton: UIButton!
    @IBOutlet weak var confirmPasswordVisibilityButton: UIButton!
    @IBOutlet weak var checkButton: UIButton!
    @IBOutlet weak var tAndCLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let text = "By selecting register, I agree to Terms and Conditions"
        let range = (text as NSString).range(of: "Terms and Conditions")
        let attributedString = NSMutableAttributedString(string:text)
        attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1), range: range)

        self.tAndCLabel.attributedText = attributedString;
    }
    
    //MARK: - IBACTION
    @IBAction func onChangePasswordVisibility(_ sender: UIButton) {
        self.passwordVisibilityButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.passwordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    
    @IBAction func onChangeConfirmPasswordVisibility(_ sender: UIButton) {
        self.confirmPasswordVisibilityButton.setImage(UIImage(named: sender.isSelected ? "ic_eye_close": "ic_eye_open"), for: .normal)
        self.confirmPasswordTextField.isSecureTextEntry = sender.isSelected
        sender.isSelected = !sender.isSelected
    }
    
    @IBAction func onRegister(_ sender: UIButton) {
        if let errorMessage = self.isValidate() {
            Utility.showAlert(message: errorMessage)
        } else {
            self.registerUser()
        }
    }
    
    @IBAction func onLogin(_ sender: UIButton) {
        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "LoginScreen") as! LoginScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    @IBAction func onCheck(_ sender: UIButton) {
        self.checkButton.setImage(UIImage(named: sender.isSelected ? "ic_empty_tick" : "ic_select"), for: .normal)
        sender.isSelected = !sender.isSelected
    }
    
    @IBAction func onTAndC(_ sender: Any) {
        self.termsAndConditions()
    }
}

//MARK: - EXTENSION
extension RegistrationScreen{
    
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
        else if !Utility.isValidPassword(stringPassword: passwordTextField.text ?? ""){
            return "Password must contain at least one capital letter, one number and one special character"
        }
        else if (self.confirmPasswordTextField.text ?? "").removeWhiteSpace().isEmpty {
            return "Please enter confirm password"
        }
        else if (self.passwordTextField.text ?? "").removeWhiteSpace() != (self.confirmPasswordTextField.text ?? "").removeWhiteSpace() {
            return "Password and confirm password should be same"
        }
        else if self.checkButton.isSelected == false {
            return "You are not agree to Terms and conitions so you are not able to register"
        }
        return nil
    }
}

//MARK: - API CALL
extension RegistrationScreen {
    
    func registerUser() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = RegisterRequest(email: self.emailTextField.text ?? "", password: self.passwordTextField.text ?? "", conformPassword: self.confirmPasswordTextField.text ?? "")
            
            LoginServices.shared.register(parameters: param.toJSON()) {[weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.logInResponse {
                    Utility.saveUserData(data: data.toJSON())
                    let vc = self?.storyboard?.instantiateViewController(identifier: "OTPVerificationScreen") as! OTPVerificationScreen
                    vc.email = self?.emailTextField.text ?? ""
                    vc.type = "1"
                    self?.navigationController?.pushViewController(vc, animated: true)
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
