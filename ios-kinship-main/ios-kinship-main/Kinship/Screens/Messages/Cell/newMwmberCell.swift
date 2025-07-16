//
//  newMwmberCell.swift
//  Kinship
//
//  Created by iMac on 04/06/24.
//

import UIKit

class newMwmberCell: UITableViewCell {

//    MARK: - IB-OUTLET
    @IBOutlet weak var personNameLabel: UILabel!
    @IBOutlet weak var profileImageView: dateSportImageView!
    @IBOutlet weak var selectImageView: UIImageView!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    var addMembersData: GroupMembers?{
        didSet{
            self.selectImageView.image = UIImage(named: self.addMembersData?.isSelected == true ? "ic_box_fill" : "ic_box")
            self.personNameLabel.text = "\(addMembersData?.firstName ?? "") \(addMembersData?.lastName ?? "")"
            Utility.setImage(addMembersData?.profileImage, imageView: self.profileImageView)
        }
    }
}
