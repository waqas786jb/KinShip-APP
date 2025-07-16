//
//  ForgotPasswordScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import UIKit

class ForgotPasswordScreen: UIViewController {
    
    //MARK: - OUTLET
    @IBOutlet weak var emailTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    //MARK: - IBACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSendVerificationLink(_ sender: UIButton) {
        if let errorMessage = self.isValidate() {
            Utility.showAlert(message: errorMessage)
        }else{
            forgotPassword()
        }
    }
}

//MARK: - EXTENSION
extension ForgotPasswordScreen{
    
    func isValidate() -> String? {
        
        if (self.emailTextField.text ?? "").removeWhiteSpace().isEmpty {
            return "Please enter email"
        }
        return nil
    }
}

//MARK: - API CALL

extension ForgotPasswordScreen {
    
    func forgotPassword() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = ForgotPasswordRequest(email: self.emailTextField.text ?? "")
            
            ForgotPasswordServices.shared.forgotPassword(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                
                if let data = response.message {
                    Utility.hideIndicator()
                    Utility.showAlert(vc: self, message: response.message ?? "")
                    let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "LoginScreen") as! LoginScreen
                    self?.navigationController?.pushViewController(vc, animated: true)
                    
                    //                Utility.setTabRoot()
                }
                Utility.hideIndicator()
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
