//
//  MessageCell.swift
//  Kinship
//
//  Created by iMac on 01/05/24.
//

import UIKit

class MessageCell: UITableViewCell {

//    MARK: -IBOUTLETS
    @IBOutlet weak var personNameLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var messageView: UIView!
    @IBOutlet weak var chatImageImageView: dateSportImageView!
    @IBOutlet weak var chatImageView: UIView!
    @IBOutlet weak var profileImageView: UIImageView!
    
//    MARK: - VARIABLES
//    var 
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    var chatData: ChatImageLinkMessageResponse?{
        didSet{
            if chatData?.name == nil{
                self.personNameLabel.text = "You"
            }else{
                self.personNameLabel.text = chatData?.name
            }
            self.messageLabel.text  = chatData?.message
            Utility.setImage(chatData?.profileImage, imageView: self.profileImageView)
            if chatData?.image == nil{
                self.chatImageView.isHidden = true
                self.messageView.isHidden = false
            }else{
                self.chatImageView.isHidden = false
                self.messageView.isHidden = true
                Utility.setImage("\(chatData?.image ?? "")", imageView: self.chatImageImageView)
            }
        }
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
}
