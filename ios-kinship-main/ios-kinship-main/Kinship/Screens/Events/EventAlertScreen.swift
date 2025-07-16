//
//  EventAlertScreen.swift
//  Kinship
//
//  Created by iMac on 08/05/24.
//

import UIKit

class EventAlertScreen: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func onOkay(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
}
