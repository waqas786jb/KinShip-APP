//
//  UpcomingEventsCell.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit
import SafariServices

class UpcomingEventsCell: UITableViewCell {

//    MARK: - OUTLETS
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var ownerNameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var locationLabel: UILabel!
    @IBOutlet weak var locationView: UIView!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var zoomLinkButton: UIButton!
    @IBOutlet weak var zoomLinkView: UIView!
    
//    MARK: - VARIABELS
    let labelText: String = "Description :"
    var zoomButton : ((String)->Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.manageUI()
    }
    
//    MARK: - FUNCTION
    func manageUI(){
        self.profileImageView.layer.cornerRadius = 5
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    var upcomingEventData: GetMyUpcomingEventResponse?{
        didSet{
            if upcomingEventData?.photo == nil{
                self.profileImageView.layer.borderWidth = 0
                self.profileImageView.image = UIImage(named: "ic_group_event")
            }else{
                self.profileImageView.layer.borderWidth = 1
                self.profileImageView.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
                Utility.setImage(upcomingEventData?.photo, imageView: self.profileImageView)
            }
            self.eventNameLabel.text = upcomingEventData?.eventName
            self.ownerNameLabel.text = "\(upcomingEventData?.firstName ?? "") \(upcomingEventData?.lastName ?? "")"
//            print(upcomingEventData?.startTime)
//            self.dateLabel.text = Utility.setTimeToDate(timestamp: "\(upcomingEventData?.startTime ?? 0)")
            self.dateLabel.text = Utility.setTimeToDateFormate(timestamp: "\(upcomingEventData?.eventDate ?? 0)", dateFormate: "EEEE, MMM d")
            self.timeLabel.text = "\(Utility.setTimeToTime(date: "\(upcomingEventData?.startTime ?? 0)")) to \(Utility.setTimeToTime(date: "\(upcomingEventData?.endTime ?? 0)"))"
            self.locationLabel.text = upcomingEventData?.location
            
            if upcomingEventData?.location == "" {
                self.locationView.isHidden = true
            }else {
                self.locationView.isHidden = false
            }
            if upcomingEventData?.link == "" {
                self.zoomLinkView.isHidden = true
            }else{
                self.zoomLinkView.isHidden = false
            }
            
            self.descriptionLabel.text = "\(labelText) \(upcomingEventData?.eventDescription ?? "")"
            
            let convertAttributedText = NSMutableAttributedString(string: descriptionLabel.text ?? "")
            let rangeColour = (self.labelText as NSString).range(of: "Description :")
            let attributedText = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)]
            convertAttributedText.addAttributes(attributedText, range: rangeColour)
            self.descriptionLabel.attributedText = convertAttributedText
        }
    }
    
//    MARK: - ACTION METHOD
    @IBAction func onZoomLinkClick(_ sender: Any) {
        self.zoomButton?(upcomingEventData?.link ?? "")
    }
}
