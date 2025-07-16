//
//  GalleryCell.swift
//  Kinship
//
//  Created by iMac on 24/04/24.
//

import UIKit

class GalleryCell: UICollectionViewCell {

//    MARK: - IB OUTLETS
    @IBOutlet weak var photoImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    var imageData: ChatImageLinkMessageResponse?{
        didSet{
            if let image = imageData?.image {
                Utility.setGalleryImage("\(image)", imageView: self.photoImageView)
            }
        }
    }
}
