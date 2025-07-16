//
//  HelpScreen.swift
//  Kinship
//
//  Created by iMac on 26/07/24.
//

import UIKit
import IQKeyboardManagerSwift

class HelpScreen: UIViewController {

//    MARK: - IB-OUTLET
    @IBOutlet weak var reasonTextField: UITextField!
    @IBOutlet weak var commentTextView: IQTextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }

    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onSend(_ sender: Any) {
        if self.reasonTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" {
            Utility.showAlert(message: "Please enter contact support reason")
        } else if self.commentTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" {
            Utility.showAlert(message: "Please enter contact support Description")
        } else {
            let vc = STORYBOARD.settings.instantiateViewController(withIdentifier: "HelpDoneScreen") as! HelpDoneScreen
            vc.modalPresentationStyle = .custom
            vc.modalTransitionStyle = .crossDissolve
            vc.goToProfile = { [weak self] in
                self?.navigationController?.popViewController(animated: true)
            }
            self.present(vc, animated: true, completion: nil)
        }
    }
}
