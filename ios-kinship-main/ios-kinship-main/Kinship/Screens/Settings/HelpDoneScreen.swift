//
//  HelpDoneScreen.swift
//  Kinship
//
//  Created by iMac on 29/07/24.
//

import UIKit

class HelpDoneScreen: UIViewController {
    
//    MARK: - VARIABLES
    var reason: String?
    var discripiton: String?
    var goToProfile: (()->Void?)?

    override func viewDidLoad() {
        super.viewDidLoad()
        
    }

    @IBAction func onOk(_ sender: Any) {
        self.help()
    }
}
extension HelpDoneScreen {
    func help() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = HelpRequest(reason: self.reason, description: self.discripiton)
            EditProfileService.shared.help(parameters: param.toJSON()) {[weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let message = response.message {
                    Utility.successAlert(message: message)
                    self?.goToProfile?()
                    self?.dismiss(animated: true)
                    
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
