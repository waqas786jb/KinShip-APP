//
//  EventsCell.swift
//  Kinship
//
//  Created by iMac on 24/04/24.
//

import UIKit
import RESegmentedControl

class EventsCell: UITableViewCell {

//    MARK: - OUTLET
    @IBOutlet weak var rsvpSegmentControll: RESegmentedControl!{
        didSet{
            self.rsvpSegmentControll.configure(segmentItems: segmentItemArray.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var createdByLabel: UILabel!
    @IBOutlet weak var eventDateLabel: UILabel!
    @IBOutlet weak var eventTimeLabel: UILabel!
    @IBOutlet weak var eventLocationLabel: UILabel!
    @IBOutlet weak var eventDescriptionLabel: UILabel!
    @IBOutlet weak var zoomLinkButton: UIButton!
    @IBOutlet weak var locationView: UIView!
    @IBOutlet weak var zoomLinkView: UIView!
    @IBOutlet weak var rsvpSegmentHeight: NSLayoutConstraint!
    
//    MARK: - VARIABLES
    let segmentItemArray: [EventsSegmentModel] = [
        EventsSegmentModel(id: 1, title: "Yes"),
        EventsSegmentModel(id: 2, title: "No"),
        EventsSegmentModel(id: 3, title: "Maybe"),
    ]
    let labelText: String = "Description :"
    var onZoomLink: ((String)->Void)?
    var status: ((Int, String, AllEventResponse) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
    
    var allEventData: AllEventResponse?{
        didSet{
            self.rsvpSegmentControll.selectedSegmentIndex = -1
//            self.rsvpSegmentHeight.constant = 50
            
            if allEventData?.location == "" {
                self.locationView.isHidden = true
            }else{
                self.locationView.isHidden = false
            }
            
            if allEventData?.link == "" {
                self.zoomLinkView.isHidden = true
            }else{
                self.zoomLinkView.isHidden = false
            }
            
            if allEventData?.photo != nil {
                Utility.setImage(allEventData?.photo, imageView: self.profileImageView)
                self.profileImageView.layer.borderWidth = 1
                self.profileImageView.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            }else{
                self.profileImageView.image = UIImage(named: "ic_group_event")
                self.profileImageView.layer.borderWidth = 0
            }
            
            self.eventNameLabel.text = allEventData?.eventName
            self.createdByLabel.text = "\(allEventData?.firstName ?? "") \(allEventData?.lastName ?? "")"
            self.eventDateLabel.text = Utility.setTimeToDateFormate(timestamp: "\(allEventData?.eventDate ?? 0)", dateFormate: "EEEE, MMM d")
            self.eventTimeLabel.text = "\(Utility.setTimeToTime(date: "\(allEventData?.startTime ?? 00)")) to \(Utility.setTimeToTime(date: "\(allEventData?.endTime ?? 00)"))"
            self.eventLocationLabel.text = allEventData?.location
            self.eventDescriptionLabel.text = "\(labelText) \(allEventData?.eventDescription ?? "")"
            
            let convertAttributedText = NSMutableAttributedString(string: eventDescriptionLabel.text ?? "")
            let rangeColour = (self.labelText as NSString).range(of: "Description :")
            let attributedText = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)]
            convertAttributedText.addAttributes(attributedText, range: rangeColour)
            self.eventDescriptionLabel.attributedText = convertAttributedText
        }
    }
    
    lazy var customSimple3preset: SegmentedControlPresettable = {
        
        var selectedBackgroundColor: UIColor = #colorLiteral(red: 0.8669999838, green: 0.8669999838, blue: 0.8669999838, alpha: 1)
        var preset = BootstapPreset(backgroundColor: .clear, selectedBackgroundColor: selectedBackgroundColor, textColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1), selectedTextColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1))
//        DispatchQueue.main.async {
            if #available(iOS 11.0, *) {
                selectedBackgroundColor = #colorLiteral(red: 0.8669999838, green: 0.8669999838, blue: 0.8669999838, alpha: 1)
            }
            
            preset.segmentCornerRadius = 17
            preset.segmentBorderWidth = 2
            preset.segmentBorderColor = #colorLiteral(red: 0.8669999838, green: 0.8669999838, blue: 0.8669999838, alpha: 1)
            preset.selectedSegmentItemCornerRadius = 17
            
            preset.textFont = UIFont(name: "OpenSans-Regular", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .bold)
            preset.selectedTextFont = UIFont(name: "OpenSans-Regular", size: 12)
//        }
        return preset
    }()
    
    @IBAction func onZoomLink(_ sender: UIButton) {
        self.onZoomLink?(allEventData?.link ?? "")
    }
    @IBAction func onSegment(_ sender: Any) {
        if self.rsvpSegmentControll.selectedSegmentIndex == 0 {
            self.status?(1, "\(allEventData?.Id ?? "")", self.allEventData!)
        }else if self.rsvpSegmentControll.selectedSegmentIndex == 1{
            self.status?(2, "\(allEventData?.Id ?? "")", self.allEventData!)
        }else if self.rsvpSegmentControll.selectedSegmentIndex == 2{
            self.status?(3, "\(allEventData?.Id ?? "")", self.allEventData!)
        }
    }
}
