//
//  KinshipGroupCell.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit
import SDWebImage

class KinshipGroupCell: UITableViewCell {

//    MARK: - IBOUTLETS
    @IBOutlet weak var groupNameLabel: UILabel!
    @IBOutlet weak var membersCountLabel: UILabel!
    @IBOutlet weak var profileImageImageView: dateSportImageView!
    @IBOutlet weak var nextPageButton: UIButton!
    @IBOutlet weak var messageCountLabel: dateSportLabel!
    @IBOutlet weak var messageCountLabelWidth: NSLayoutConstraint!
    
//  MARK: - VARIABLES
    var onClickNext : (() -> Void)?
    
    var item: HomePageResponse? {
        didSet{
            self.groupNameLabel.text = item?.groupName
            self.membersCountLabel.text = "\(item?.count ?? 0) members"
            TOTAL_GROUP_MEMBRS = item?.count
            self.profileImageImageView.sd_setImage(with: URL(string: (item?.image) ?? ""))
            
            if item?.kinshipCount == 0{
                self.messageCountLabel.isHidden = true
            }else{
                self.messageCountLabel.text = "\(item?.kinshipCount ?? 0)"
                self.messageCountLabel.isHidden = false
                if Int(truncating: item?.kinshipCount ?? 0) > 99 {
                    self.messageCountLabel.text = "\(99)+"
                    self.messageCountLabelWidth.constant = CGFloat(28)
                }else{
                    if self.messageCountLabel.text?.count == 2 {
                        self.messageCountLabelWidth.constant = CGFloat(20)
                    }
                }
            }
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
//    MARK: - IBACTION
    @IBAction func onClickToNextButton(_ sender: Any) {
        self.onClickNext?()
    }
    
}
