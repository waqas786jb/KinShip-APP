//
//  UpdateContactDetailsScreen.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit
import CountryPickerView

class UpdateContactDetailsScreen: UIViewController {
    
//    MARK: - OUTLETS
    @IBOutlet weak var updateButton: UIButton!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var mobileNoTextField: UITextField!
    @IBOutlet weak var countryCodeLabel: UILabel!
    
    
//    MARK: - VARIABLES
    let countryPickerView = CountryPickerView()
    let getEmail = Utility.getUserData()?.email ?? ""
    let getMobileNo = Utility.getUserData()?.profile?.phoneNumber ?? ""

    override func viewDidLoad() {
        super.viewDidLoad()
        inilization()
    }
    func inilization(){
//        tabBarController?.tabBar.isHidden = true
        self.updateButton.layer.cornerRadius = 25
        countryPickerView.delegate = self
        if let countryCode = (Locale.current as NSLocale).object(forKey: .countryCode) as? String {
        let countryName = countryPickerView.getCountryByCode(countryCode)
        countryPickerView.setCountryByName(countryName?.name ?? "")
            
        self.emailTextField.text = Utility.getUserData()?.email
        self.mobileNoTextField.text = Utility.getUserData()?.profile?.phoneNumber
        self.countryCodeLabel.text = "\(Utility.getUserData()?.profile?.countrycode ?? "")"
        }
    }
    
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onCountryCode(_ sender: Any) {
        self.view.endEditing(true)
        countryPickerView.showCountriesList(from: self)
    }
    @IBAction func onUpdate(_ sender: Any) {
        if self.emailTextField.text == "" || self.mobileNoTextField.text == "" {
            Utility.showAlert(message: "You must fill all the fields")
        }else if !Utility.validateEmail(emailTextField.text ?? "") {
            Utility.showAlert(message: "Please enter valid email")
        }else if !Utility.isValidPhoneNumber(phone: self.mobileNoTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? "") {
            Utility.showAlert(message: "Please enter Valid Phone number")
        }else{
            self.updateProfile()
        }
    }
}
extension UpdateContactDetailsScreen: CountryPickerViewDelegate{
    
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        countryCodeLabel.text = country.phoneCode
    }

}

extension UpdateContactDetailsScreen {
    func updateProfile(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)

            let param = EditProfileRequest(email: self.emailTextField.text ?? "",mobileNo: self.mobileNoTextField.text ?? "", countryCode: self.countryCodeLabel.text ?? "")
            
            EditProfileService.shared.changePassword(parameters : param.toJSON()) { [weak self]  (statusCode, response) in
                Utility.hideIndicator()
//                print(response.toJSON())
                if let data = response.profileResponse {
//                    if let userData = Utility.getUserData() {
//                        userData.email = data.newEmail
//                        Utility.saveUserData(data: userData.toJSON())
//                    }
                    let vc = STORYBOARD.authentication.instantiateViewController(identifier: "OTPVerificationScreen") as! OTPVerificationScreen
                    vc.email = self?.emailTextField.text ?? ""
                    vc.oldEmail = self?.getEmail ?? ""
                    vc.updatedMobileNo = self?.mobileNoTextField.text ?? ""
                    vc.oldMobileNo = self?.getMobileNo ?? ""
                    vc.type = "2"
                    vc.isFromUpdatePassword = true
                    vc.updatedEmail = self?.emailTextField.text
                    vc.countryCode = self?.countryCodeLabel.text
                    self?.navigationController?.pushViewController(vc, animated: true)
                }else{
                    print("No data print")
                }
            } failure: { error in
                print(error)
                Utility.showAlert(message: error)
                Utility.hideIndicator()
            }
        }else{
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
