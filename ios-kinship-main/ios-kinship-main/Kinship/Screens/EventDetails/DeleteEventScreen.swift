//
//  DeleteEventScreen.swift
//  Kinship
//
//  Created by iMac on 12/06/24.
//

import UIKit

class DeleteEventScreen: UIViewController {

    
    @IBOutlet weak var deleteButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    
    var eventId: String?
    var delClick : ((String)->Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    @IBAction func onDeleteButton(_ sender: Any) {
        self.deleteAccountAPI()
    }
    @IBAction func onCancleButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
}
extension DeleteEventScreen {
    func deleteAccountAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = deleteEventResponse(eventId: self.eventId)
            GetMyEventDetailsServices.shared.deleteEvent(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                if let data = response?.message {
                    Utility.hideIndicator()
                    Utility.successAlert(message: data)
                    self?.delClick?("delete")
                    self?.dismiss(animated: true, completion: nil)
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
