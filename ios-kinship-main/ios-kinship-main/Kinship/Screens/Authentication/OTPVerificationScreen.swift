//
//  OTPVerificationScreen.swift
//  Kinship
//
//  Created by iMac on 18/03/24.
//

import UIKit
import AEOTPTextField

class OTPVerificationScreen: UIViewController {
    
    // MARK: - OUTLETS
    @IBOutlet weak var otpTextField: AEOTPTextField!
    @IBOutlet weak var timerLabel: UILabel!
    @IBOutlet weak var resendOtpButton: UIButton!
    @IBOutlet weak var resendOtpUnderline: UIView!
    @IBOutlet weak var otpInformationLabel: UILabel!
    @IBOutlet weak var backButton: UIButton!
    
//    MARK: - VERIABLES
    var times : Timer?
    var count: Int = 60
    var email: String = ""
    var type: String = ""
    var isFromUpdatePassword: Bool?
    var updatedEmail: String?
    var countryCode: String?
    var oldEmail = ""
    var oldMobileNo = ""
    var updatedMobileNo: String?
    var isFromForgotPassword: Bool?
    var isForgotPasswordClick: (()->Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialize()
    }
    
//MARK: - FUNCTIONS
    func initialize(){
        
//        Border colour and border redius
        self.otpTextField.otpDelegate = self
//        self.otpTextField.layer.borderColor = #colorLiteral(red: 0.9137254902, green: 0.7333333333, blue: 0.7294117647, alpha: 1)
//        self.otpTextField.layer.borderWidth = 1
        self.otpTextField.otpCornerRaduis = self.otpTextField.frame.height/2
        self.otpTextField.otpDefaultBorderWidth = 1
        self.otpTextField.otpDefaultBorderColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.5)
        self.otpTextField.otpBackgroundColor = .clear
        self.otpTextField.otpFilledBorderColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.5)
        self.otpTextField.otpFilledBackgroundColor = #colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)
        self.otpTextField.configure(with: 4)
        
//        Resend button
        self.resendOtpButton.isHidden = true
        self.resendOtpUnderline.isHidden = true
        
//        Timer
        times = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: true)
        
        if isFromForgotPassword == true {
            self.backButton.isHidden = false
        } else {
            if self.isFromUpdatePassword == true {
                self.otpInformationLabel.text = "We will send you an One time password via this \(updatedEmail ?? "") email address."
                self.backButton.isHidden = false
            } else {
                self.otpInformationLabel.text = "Please enter the code we emailed to you."
                self.backButton.isHidden = true
            }
        }
    }
    // MARK: - IBACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onVerify(_ sender: UIButton) {
        if otpTextField.text == "" {
            Utility.showAlert(vc: self, message: "Enter One Time Password")
        }else{
            otpVerification()
        }
    }
    @IBAction func onResendOTP(_ sender: Any) {
        self.resendOtpButton.isHidden = true
        self.resendOtpUnderline.isHidden = true
        self.resendOtpVerification()
        self.otpTextField.clearOTP()
        self.times = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: true)
    }
    
// MARK: - FUNCTIONS
    @objc func updateTimer() {
        self.count -= 1
        if count >= 0 {
//            print(count)
            timerLabel.isHidden = false
            resendOtpButton.isHidden = true
//            resendOtpUnderline.isHidden = true
            timerLabel.text = "\(count)sec"
        } else {
            times?.invalidate()
            count = 60
            timerLabel.isHidden = true
            resendOtpButton.isHidden = false
            resendOtpUnderline.isHidden = false
        }
    }
}
// MARK: - EXTENSION

extension OTPVerificationScreen : AEOTPTextFieldDelegate{
    
    func didUserFinishEnter(the code: String) {
    }
}

// MARK: - API CALL
extension OTPVerificationScreen {
    
    func otpVerification() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            var parameters = OTPVerificationRequest()
            
            if isFromUpdatePassword == true{
                let param = OTPVerificationRequest(email: email, otp: self.otpTextField.text ?? "", type: type, oldEmail: self.oldEmail)
                parameters = param
            } else {
                let param = OTPVerificationRequest(email: email, otp: self.otpTextField.text ?? "", type: type)
                parameters = param
            }
            
            ForgotPasswordServices.shared.otpVerification(parameters: parameters.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if self?.isFromUpdatePassword == true {
                    if let data = response.message {
                        Utility.showAlert(message: response.message ?? "")
                        if let userData = Utility.getUserData() {
                            userData.email = self?.updatedEmail
                            userData.profile?.phoneNumber = self?.updatedMobileNo
                            userData.profile?.countrycode = self?.countryCode
                            Utility.saveUserData(data: userData.toJSON())
                        }
                        self?.navigationController?.popViewController(animated: true)
                    }
                    self?.times?.invalidate() // stop timer
                } else {
                    if self?.isFromForgotPassword == true {
                        self?.navigationController?.popViewController(animated: true)
                        self?.isForgotPasswordClick?()
                    } else {
                        if let data = response.logInResponse {
                            Utility.saveUserData(data: data.toJSON())
                            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "BringYouScreen") as! BringYouScreen
                            self?.navigationController?.pushViewController(vc, animated: true)
                            self?.times?.invalidate() // stop timer
                        }
                    }
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
    
    func resendOtpVerification(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ResendOtpVarification(email: email)
//            print(param.toJSON())
            ForgotPasswordServices.shared.resendOtpVerification(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                
                if let data = response.message {
                    Utility.showAlert(message: data)
                    Utility.hideIndicator()
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
