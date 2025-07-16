//
//  LeaveKinshipScreen.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit

class LeaveKinshipScreen: UIViewController {

//    MARK: - OUTLETS
    @IBOutlet weak var leaveKinshipButton: UIButton!
    @IBOutlet weak var headerLabel: UILabel!
    @IBOutlet weak var headerNotesLabel: UILabel!
    @IBOutlet weak var alertLabel: UILabel!
    @IBOutlet weak var alertTextView: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var timeTextView: UILabel!
    
//    MARK: - VARIABLES
    var isFromDeleteScreen: Bool?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        manageUI()
        initialize()
    }
    
//    MARK: - FUNCTION
    func manageUI(){
        self.leaveKinshipButton.layer.cornerRadius = 25
    }
    func initialize(){
        if isFromDeleteScreen == true{
//            For Delete account
            self.headerLabel.text = "Delete Your Account"
            self.headerNotesLabel.text = "Are you sure you want to delete your account?"
            self.alertLabel.text = "Deleting your account is permanent"
            self.alertTextView.text = "Your profile, photos, messages, and events will be permanently deleted. You will not be able to rejoin your current Kinship."
            self.timeLabel.text = "You will have to wait 30 days to join a new Kinship"
            self.timeTextView.text = "To protect our community from abuse, if you create a new account, you will not be able to join a new Kinship for 30 days."
            self.leaveKinshipButton.setTitle("Delete Account", for: .normal)
        }else{
//            For Leave your Kinship
            self.headerLabel.text = "Leave your Kinship"
            self.headerNotesLabel.text = "Are you sure you want to leave your Kinship?"
            self.alertLabel.text = "Leaving your Kinship is permanent"
            self.alertTextView.text = "If you leave your Kinship, you will not be able to rejoin. You will have to join a new Kinship with new people and your connections in your current Kinship will be lost"
            self.timeLabel.text = "You can only join a new Kinship once every 30 days"
            self.timeTextView.text = "To protect our community from abuse, users can be members of only one Kinship at a time and can only join a new Kinship once every 30 days."
            self.leaveKinshipButton.setTitle("Leave \(UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? "")", for: .normal)
        }
    }
    
//    MARK: - IB ACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onLeaveKinship(_ sender: Any) {
//        print("Leave Button Click")
        self.view.endEditing(true)
        let vc = STORYBOARD.leaveKinship.instantiateViewController(withIdentifier: "alertScreen") as! alertScreen
        vc.isFromDeleteScreen = self.isFromDeleteScreen
        vc.modalPresentationStyle = .custom
        vc.modalTransitionStyle = .crossDissolve
        vc.back = { value in
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "BringYouScreen") as! BringYouScreen
            self.navigationController?.pushViewController(vc, animated: true)
//            print(value)
        }
        vc.goToregister = { value in
            let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "RegistrationScreen") as! RegistrationScreen
            self.navigationController?.pushViewController(vc, animated: true)
//            print(value)
        }
        self.present(vc, animated: true, completion: nil)
    }
}
