//
//  NotificationCell.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import UIKit

class NotificationCell: UITableViewCell {
    
    // MARK: - IBOUTLET
    @IBOutlet weak var notificationNameLabel: UILabel!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var notificationPersonName: UILabel!
    
//    MARK: - VARIABLES

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.profileImageView.layer.cornerRadius = 15
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    var notificationDetails: NotificationResponse? {
        didSet{
            self.notificationNameLabel.text = notificationDetails?.message
//            self.timeLabel.text = Utility.setMultiTimeToTimeZone(timestamp: String(notificationDetails?.createdAt ?? 0))
            self.timeLabel.text = Utility.setTimeToDateFormate(timestamp: String(notificationDetails?.createdAt ?? 0), dateFormate: "hh:mm a")
            self.notificationPersonName.text = "\(notificationDetails?.firstName ?? "") \(notificationDetails?.lastName ?? "")"
            Utility.setImage(notificationDetails?.profileImage, imageView: self.profileImageView)
        }
    }
}
