//
//  CommunityCell.swift
//  Kinship
//
//  Created by iMac on 29/10/24.
//

import UIKit

class CommunityCell: UITableViewCell {

    // MARK: - IB Outlet
    @IBOutlet weak var profilePictureImageView: dateSportImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var postPictureImageView: dateSportImageView!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var likeImageView: UIImageView!
    @IBOutlet weak var likeCountLabel: UILabel!
    @IBOutlet weak var commentCountLabel: UILabel!
    @IBOutlet weak var imageHideView: UIView!
    @IBOutlet weak var likeBackgroundView: dateSportView!
    @IBOutlet weak var commentView: dateSportView!
    @IBOutlet weak var heightFindView: UIView!
    
    // MARK: - Variabels
    var onImage: (() -> Void)?
    var onComment: (() -> Void)?
    var onLike: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    // MARK: - Data set for Community
    var data: CommunityListingData? {
        didSet{
            Utility.setImage(data?.profileImage, imageView: self.profilePictureImageView)
            self.nameLabel.text = "\(data?.firstName ?? "") \(data?.lastName ?? "")"
//            self.dateLabel.text = "\(data?.createdAt ?? 0)"
            self.dateLabel.text = "\(Utility.setTimeToDateFormate(timestamp: "\(data?.createdAt ?? 0)", dateFormate: "d MMM, hh:mm a"))"
            Utility.setImage(data?.file, imageView: self.postPictureImageView)
            self.messageLabel.text = data?.message
            self.likeCountLabel.text = "\(data?.like ?? 0)"
            self.commentCountLabel.text = "\(data?.commentCount ?? 0)"
            
            if data?.file == nil {
                self.imageHideView.isHidden = true
            } else {
                self.imageHideView.isHidden = false
            }
            
            if data?.isLiked == true {
                self.likeImageView.image = UIImage(named: "ic_heart_fill")
                self.likeBackgroundView.backgroundColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.2)
            } else {
                self.likeImageView.image = UIImage(named: "ic_heart")
                self.likeBackgroundView.backgroundColor = #colorLiteral(red: 0.9411764741, green: 0.9411764741, blue: 0.9411764741, alpha: 1)
            }
        }
    }
    
    // MARK: - Data set for Comments
    var commentData: CommentListingData? {
        didSet{
            self.imageHideView.isHidden = true
            self.commentView.isHidden = true
            Utility.setImage(commentData?.profileImage, imageView: self.profilePictureImageView)
            self.nameLabel.text = "\(commentData?.firstName ?? "") \(commentData?.lastName ?? "")"
//            self.dateLabel.text = "\(data?.createdAt ?? 0)"
            self.dateLabel.text = "\(Utility.setTimeToDateFormate(timestamp: "\(commentData?.createdAt ?? 0)", dateFormate: "d MMM, hh:mm"))"
            self.messageLabel.text = commentData?.message
            self.likeCountLabel.text = "\(commentData?.like ?? 0)"
            
            if commentData?.isLiked == true {
                self.likeImageView.image = UIImage(named: "ic_heart_fill")
                self.likeBackgroundView.backgroundColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.2)
            } else {
                self.likeImageView.image = UIImage(named: "ic_heart")
                self.likeBackgroundView.backgroundColor = #colorLiteral(red: 0.9411764741, green: 0.9411764741, blue: 0.9411764741, alpha: 1)
            }
        }
    }
    
    // MARK: - IB Actions
    @IBAction func onImageClick(_ sender: Any) {
        self.onImage?()
    }
    @IBAction func onComment(_ sender: Any) {
        self.onComment?()
    }
    @IBAction func onLike(_ sender: Any) {
        self.onLike?()
    }
}
