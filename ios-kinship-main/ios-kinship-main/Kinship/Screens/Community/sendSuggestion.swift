//
//  sendSuggestion.swift
//  Kinship
//
//  Created by iMac on 29/10/24.
//

import UIKit
import IQKeyboardManagerSwift

class sendSuggestion: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var communityNameTextField: UITextField!
    @IBOutlet weak var communityCityTextField: UITextField!
    @IBOutlet weak var ideaTextView: IQTextView!
    
    
    // MARK: - Variables
    
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.manageUI()
    }
    
    func manageUI() {
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSend(_ sender: Any) {
        if self.communityNameTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
            Utility.showAlert(message: "Please enter Community name")
        } else if self.communityCityTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
            Utility.showAlert(message: "Please enter Community city")
        } else if self.ideaTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0{
            Utility.showAlert(message: "Please enter Community idea")
        } else {
            self.myCommunityList()
        }
    }
}

// MARK: - Extension

// MARK: - Delegate Method

// MARK: - API Call
extension sendSuggestion {
    func myCommunityList(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = sendSuggestionRequest(name: self.communityNameTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines), city: self.communityCityTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines), idea: self.ideaTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines))
            Community.shared.sendSuggestion(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let message = response.message {
                    Utility.successAlert(message: message)
                    self?.navigationController?.popViewController(animated: true)
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
}
