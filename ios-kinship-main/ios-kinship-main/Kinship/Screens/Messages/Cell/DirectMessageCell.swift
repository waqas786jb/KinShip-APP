//
//  DirectMessageCell.swift
//  Kinship
//
//  Created by iMac on 29/04/24.
//

import UIKit

class DirectMessageCell: UITableViewCell {
    
//   MARK: - IBOUTLET
    @IBOutlet weak var personNameLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var messageCountLabel: UILabel!
    @IBOutlet weak var messageCountLabelWidth: NSLayoutConstraint!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var selectButton: UIButton!
    @IBOutlet weak var groupCountLabel: dateSportLabel!
    
//    MARK: - VARIABLES
    var isFromNewMessage: Bool?
    
    var userGroupListData: UserGroupListResponse?{
        didSet{
            self.personNameLabel.text = userGroupListData?.name
            self.messageLabel.text = userGroupListData?.message
            self.groupCountLabel.text = "\(userGroupListData?.userId?.count ?? 0)"
            Utility.setImage(userGroupListData?.profileImage, imageView: self.profileImageView)
            
            if userGroupListData?.name == "" {
                self.personNameLabel.text = "You"
            }
            if userGroupListData?.count ?? 0 == 0 {
                self.messageCountLabel.isHidden = true
            } else if userGroupListData?.count ?? 0 > 99{
                self.messageCountLabel.text = "\(99)+"
                self.messageCountLabelWidth.constant = CGFloat(28)
            } else {
                self.messageCountLabel.text = "\(userGroupListData?.count ?? 0)"
                self.messageCountLabel.isHidden = false
                if self.messageCountLabel.text?.count == 2 {
                    self.messageCountLabelWidth.constant = CGFloat(20)
                }else{
                    self.messageCountLabelWidth.constant = CGFloat(15)
                }
            }
            
            if userGroupListData?.userId?.count ?? 0 > 2{
                self.profileImageView.isHidden = true
                self.groupCountLabel.isHidden = false
            }else{
                self.profileImageView.isHidden = false
                self.groupCountLabel.isHidden = true
            }
        }
    }
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.manageUI()
    }
    
    func manageUI(){
        self.messageCountLabel.layer.cornerRadius = 7
        self.messageCountLabel.layer.masksToBounds = true
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}
