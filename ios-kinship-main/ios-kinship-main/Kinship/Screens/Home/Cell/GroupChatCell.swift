//
//  GroupChatCell.swift
//  Kinship
//
//  Created by iMac on 16/05/24.
//

import UIKit
import ActiveLabel

class GroupChatCell: UITableViewCell {

//    MARK: - IBOUTLET
    @IBOutlet weak var personNameLabel: UILabel!
//    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var messageLabel: ActiveLabel!
    
    @IBOutlet weak var chatImageView: UIView!
    @IBOutlet weak var profileImageView: dateSportImageView!
    @IBOutlet weak var chatImageImageView: dateSportImageView!
    @IBOutlet weak var messagView: UIView!
    @IBOutlet weak var likeButton: UIButton!
    
//    MARK: - VARIABLES
    var isFromLikeScreen: Bool = false
    var isFromDirectMessageScreen: Bool?
    var likeOrNot : ((Bool) -> Void)?
    var image : ((String) -> Void)?
    var link : ((String) -> Void)?
    var disLikeMessage: ((String) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.initilize()
    }
    func initilize(){
        self.manageLinkTap()
    }
    
    var chatData: ChatImageLinkMessageResponse?{
        didSet{
            Utility.setImage(chatData?.profileImage, imageView: self.profileImageView)
            self.messageLabel.text  = chatData?.message
            if isFromDirectMessageScreen == true{
                self.likeButton.isHidden = true
            }else{
                self.likeButton.isHidden = false
            }
            
            if chatData?.name == "" {
                self.personNameLabel.text = "You"
            }else{
                if self.chatData?.senderId == Utility.getUserData()?.profile?.userId {
                    self.personNameLabel.text = "You"
                    Utility.setImage(Utility.getUserData()?.profile?.profileImage, imageView: self.profileImageView)
                }else{
                    self.personNameLabel.text = chatData?.name
                }
            }
            
            if chatData?.type == 1{
                self.chatImageView.isHidden = true
                self.messagView.isHidden = false
                self.messageLabel.textColor = UIColor.black
            }else if chatData?.type == 2 {
                if chatData?.image == nil{
                    self.chatImageView.isHidden = false
                    self.messagView.isHidden = true
                    Utility.setGalleryImage(chatData?.message, imageView: self.chatImageImageView)
                }else{
                    self.chatImageView.isHidden = false
                        self.messagView.isHidden = true
                    Utility.setGalleryImage("\(chatData?.image ?? "")", imageView: self.chatImageImageView)
                }
            }else if chatData?.type == 3{
                self.chatImageView.isHidden = true
                self.messagView.isHidden = false
//                self.messageLabel.textColor = UIColor.link
            }else if chatData?.type == 4{
                self.messagView.isHidden = false
                self.chatImageView.isHidden = false
                Utility.setGalleryImage("\(chatData?.image ?? "")", imageView: self.chatImageImageView)
            }
            
            if isFromLikeScreen == true {
                self.likeButton.setImage(UIImage(named: "ic_heart_fill"), for: .normal)
                self.likeButton.isSelected = true
            }else {
                if chatData?.isgrpLiked == true {
                    self.likeButton.setImage(UIImage(named: "ic_heart_fill"), for: .normal)
                    self.likeButton.isSelected = true
                    chatData?.isgrpLiked = true
                }else{
                    self.likeButton.setImage(UIImage(named: "ic_heart"), for: .normal)
                    self.likeButton.isSelected = false
                    chatData?.isgrpLiked = false
                }
            }
        }
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    @IBAction func onLike(_ sender: UIButton) {
        self.likeButton.setImage(UIImage(named:(sender as AnyObject).isSelected ? "ic_heart": "ic_heart_fill"), for: .normal)
        sender.isSelected = !sender.isSelected
        self.disLikeMessage?("\(chatData?.Id ?? "")")
        self.likeMessageAPI()
    }
    @IBAction func onImageClick(_ sender: UIButton) {
        self.image?(chatData?.image ?? "")
    }
    func manageLinkTap(){
        self.messageLabel.handleURLTap { value in
            self.link?("\(value)")
        }
    }
}

// MARK: - API CALL
extension GroupChatCell {
    func likeMessageAPI(){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: contentView)
            let param = LikeMessageRequest(messageId: self.chatData?.Id)
            GroupChatServices.shared.likeMessage(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response.likeMessageResponse {
                    self?.chatData?.isgrpLiked = data.isgrpLiked
                    print(data.isgrpLiked ?? "")
                }
            } failure: { (error) in
                Utility.hideIndicator()
            }
        }
    }
}
