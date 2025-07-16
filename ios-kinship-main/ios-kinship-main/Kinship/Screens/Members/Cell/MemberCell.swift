//
//  MemberCell.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import UIKit
import SDWebImage

class MemberCell: UITableViewCell {

//    MARK: - IB-OUTLET
    @IBOutlet weak var profilePictureImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var messageButton: UIButton!
    
//   MARK: - VARIABLES
    var memberDetails: HomePageResponse?
    var isFromrsvpScreen: Bool?
    var message: ((String) -> Void)?
    var rsvpDetails: RSVPNameResponse?{
        didSet{
            self.nameLabel.text = "\(rsvpDetails?.firstName ?? "") \(rsvpDetails?.lastName ?? "")"
            Utility.setImage(rsvpDetails?.profileImage, imageView: self.profilePictureImageView)
            self.messageButton.isHidden = true
        }
    }
    var groupDetails: GroupMembers?{
        didSet{
            self.nameLabel.text = "\(groupDetails?.firstName ?? "") \(groupDetails?.lastName ?? "")"
            Utility.setImage(groupDetails?.profileImage, imageView: self.profilePictureImageView)
            if groupDetails?.userId == Utility.getUserData()?.profile?.userId{
                self.messageButton.isHidden = true
            }else{
                self.messageButton.isHidden = false
            }
        }
    }
    var subgroupMemberDetails: SubGroupMembersResponse?{
        didSet{
            self.nameLabel.text = "\(subgroupMemberDetails?.name ?? "")"
            Utility.setImage(subgroupMemberDetails?.profileImage, imageView: self.profilePictureImageView)
            self.messageButton.isHidden = true
        }
    }
    override func awakeFromNib() {
        super.awakeFromNib()
        self.manageUI()
    }
    @IBAction func onMessageClick(_ sender: Any) {
        self.message?("value")
    }
    func manageUI(){
        profilePictureImageView.layer.cornerRadius = 15
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}
