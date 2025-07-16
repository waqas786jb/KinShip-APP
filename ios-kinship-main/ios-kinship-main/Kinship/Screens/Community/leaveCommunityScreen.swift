//
//  leaveCommunityScreen.swift
//  Kinship
//
//  Created by iMac on 18/11/24.
//

import UIKit

class leaveCommunityScreen: UIViewController {

    // MARK: - IB Outlets
    
    
    // MARK: - Variables
    var leaveCommunityClosure: (()->Void)?
    var customerId: String?
    
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
    @IBAction func onOkay(_ sender: Any) {
        self.leaveCommunity()
    }
    
    @IBAction func onNo(_ sender: Any) {
        self.dismiss(animated: true)
    }
}

// MARK: - Extension

// MARK: - Delegate Method

// MARK: - API Call
extension leaveCommunityScreen {
    func leaveCommunity(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = leaveCommunityRequest(communityId: self.customerId)
            Community.shared.leaveCommunity(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let _ = response.message {
                    self?.leaveCommunityClosure?()
                    self?.dismiss(animated: true)
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
