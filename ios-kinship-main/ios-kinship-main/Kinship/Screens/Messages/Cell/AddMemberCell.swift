//
//  AddMemberCell.swift
//  Kinship
//
//  Created by iMac on 04/06/24.
//

import UIKit

class AddMemberCell: UICollectionViewCell {

//    MARK: - IB OUTLET
    @IBOutlet weak var nameLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    var selectedMemberData: GroupMembers?{
        didSet{
            self.nameLabel.text = "\(selectedMemberData?.firstName ?? "") \(selectedMemberData?.lastName ?? "")"
        }
    }
}
