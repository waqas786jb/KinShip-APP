//
//  ImageOpenScreen.swift
//  Kinship
//
//  Created by iMac on 21/05/24.
//

import UIKit

class ImageOpenScreen: UIViewController {

//    MARK: - IBOUTLET
    @IBOutlet weak var selectedImageView: UIImageView!
    
//    MARK: - VARIABLES
//    var imageData: ChatImageLinkMessageResponse?
    var imageData: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Utility.setGalleryImage(imageData, imageView: self.selectedImageView)
    }
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
