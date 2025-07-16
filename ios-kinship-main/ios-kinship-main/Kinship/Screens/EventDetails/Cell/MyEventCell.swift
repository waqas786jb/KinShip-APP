//
//  MyEventCell.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit
import DropDown

struct MenuItem {
    var title : String?
    var image : String?
    
    init(title: String? = nil, image: String? = nil) {
        self.title = title
        self.image = image
    }
}
class MyEventCell: UITableViewCell {

//    MARK: - IBOUTLET
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var locationLabel: UILabel!
    @IBOutlet weak var locationView: UIView!
    @IBOutlet weak var zoomLinkView: UIView!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var yesCountLabel: UILabel!
    @IBOutlet weak var maybeCountLabel: UILabel!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var createdDateLabel: UILabel!
    @IBOutlet weak var moreButton: UIButton!
    @IBOutlet weak var clickToJoinLabel: UIButton!
    
    
//    MARK: - VARIABLES
    let labelText: String = "Description :"
    var zoomButtonLink : ((String)->Void)?
    var viewDetails : (()->Void)?
    var editClick : ((String)->Void)?
    var deleteClick : ((String)->Void)?
    let menuDropDown = DropDown()
    var menuItemList = [MenuItem(title: "Edit Event", image: "ic_edit_event"),
                        MenuItem(title: "Delete Event", image: "ic_delete")
    ]
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.manageUI()
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
    
//    MARK: - FUNCTION
    func manageUI(){
        self.yesCountLabel.layer.cornerRadius = 9
        self.yesCountLabel.layer.masksToBounds = true
        self.maybeCountLabel.layer.cornerRadius = 9
        self.maybeCountLabel.layer.masksToBounds = true
        
        self.profileImageView.layer.cornerRadius = 5
    }
    
    var eventDetails: GetMyEventResponse?{
        didSet {
            if eventDetails?.photo == nil{
                self.profileImageView.layer.borderWidth = 0
                self.profileImageView.image = UIImage(named: "ic_group_event")
            }else{
                self.profileImageView.layer.borderWidth = 1
                self.profileImageView.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
                Utility.setImage(eventDetails?.photo, imageView: self.profileImageView)
            }
            self.eventNameLabel.text = eventDetails?.eventName
            self.dateLabel.text = Utility.setTimeToDateFormate(timestamp: "\(eventDetails?.eventDate ?? 0)", dateFormate: "EEEE, MMM d")
            self.timeLabel.text = "\(Utility.setTimeToTime(date: "\(eventDetails?.startTime ?? 0)")) to \(Utility.setTimeToTime(date: "\(eventDetails?.endTime ?? 0)"))"
            self.descriptionLabel.text = "\(labelText) \(eventDetails?.eventDescription ?? "")"
            self.yesCountLabel.text = "\(eventDetails?.yes ?? 0)"
            self.maybeCountLabel.text = "\(eventDetails?.maybe ?? 0)"
            self.createdDateLabel.text = Utility.timeZoneToDate(timeZone: Int(eventDetails?.createdAt ?? 0))
            
            if eventDetails?.location == "" {
                self.locationView.isHidden = true
            }else{
                self.locationView.isHidden = false
                self.locationLabel.text = eventDetails?.location
            }
            if eventDetails?.link == "" {
                self.zoomLinkView.isHidden = true
            }else{
                self.zoomLinkView.isHidden = false
            }
            
            let convertAttributedText = NSMutableAttributedString(string: descriptionLabel.text ?? "")
            let rangeColour = (self.labelText as NSString).range(of: "Description :")
            let attributedText = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)]
            convertAttributedText.addAttributes(attributedText, range: rangeColour)
            self.descriptionLabel.attributedText = convertAttributedText
        }
    }
    func displayDropDown(view: UIView?){
      
        self.menuDropDown.anchorView = view
        self.menuDropDown.dataSource = menuItemList.map{$0.title ?? ""}
        self.menuDropDown.bottomOffset = CGPoint(x: 200, y: 40)
        self.menuDropDown.cornerRadius = 10
        self.menuDropDown.width = 134
        self.menuDropDown.direction = .bottom
        self.menuDropDown.backgroundColor = UIColor.white
        self.menuDropDown.textColor = UIColor.black
        self.menuDropDown.textFont = UIFont(name: "OpenSans-SemiBold", size: 12.0) ?? UIFont()
        self.menuDropDown.selectionBackgroundColor = UIColor.white
        self.menuDropDown.cellNib = UINib(nibName: "MenuItemCell", bundle: nil)
        
        self.menuDropDown.customCellConfiguration = { (index: Index, item: String, cell: DropDownCell) -> Void in
           guard let cell = cell as? MenuItemCell else { return }

           // Setup your custom UI components
            cell.optionLabel.text = self.menuItemList[index].title
            cell.menuItemImageView.image = UIImage(named: self.menuItemList[index].image ?? "")
            if index == self.menuItemList.count-1 {
                cell.lineLabel.isHidden = true
            }else {
                cell.lineLabel.isHidden = false
            }
        }
        self.menuDropDown.selectionAction = { [weak self] (index, item) in
            if index == 0 {
//                print("Edit click")
                self?.editClick?("\(self?.eventDetails?.Id ?? "")")
            }else if index == 1 {
//                print("Delete click")
                self?.deleteClick?("\(self?.eventDetails?.Id ?? "")")
            }
        }
        self.menuDropDown.show()
    }
    
//    MARK: - IB-ACTION METHOD
    @IBAction func onZoomLink(_ sender: Any) {
        self.zoomButtonLink?(eventDetails?.link ?? "")
    }
    @IBAction func onViewDetails(_ sender: Any) {
        self.viewDetails?()
    }
    @IBAction func onMenu(_ sender: Any) {
        self.displayDropDown(view: contentView)
    }
}
