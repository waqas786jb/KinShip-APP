//
//  groupInformationScreen.swift
//  Kinship
//
//  Created by iMac on 07/06/24.
//

import UIKit

class groupInformationScreen: UIViewController {
    
//  MARK: - IB-OUTLET METHOD
    @IBOutlet weak var informationLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
    }
    func initilize() {
        if Utility.getUserData()?.profile?.kinshipReason == 1 || Utility.getUserData()?.profile?.kinshipReason == 2{
            self.informationLabel.text = "We’ve matched you with \((TOTAL_GROUP_MEMBRS ?? 0)-1) other mom-to-be who we think you'll relate to."
        }else{
            self.informationLabel.text = "We’ve matched you with \((TOTAL_GROUP_MEMBRS ?? 0)-1) other moms who we think you'll relate to."
        }
    }
    
//    MARK: - IB-ACTION METHOD
    @IBAction func onOk(_ sender: Any) {
        self.removePopUp()
    }
}
extension groupInformationScreen {
    func removePopUp() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            GroupChatServices.shared.removePopUp{ [weak self] (statusCode, response) in
                Utility.hideIndicator()
                self?.dismiss(animated: true, completion: nil)
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
    }
}
