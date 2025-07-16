//
//  countDetailsScreen.swift
//  Kinship
//
//  Created by iMac on 10/06/24.
//

import UIKit
import RESegmentedControl

class countDetailsScreen: UIViewController {

    @IBOutlet weak var rsvpTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "MemberCell", bundle: nil)
            rsvpTableView.register(nib, forCellReuseIdentifier: "MemberCell")
        }
    }
    @IBOutlet weak var firstLabel: UILabel!
    @IBOutlet weak var secondLabel: UILabel!
    @IBOutlet weak var thirdLabel: UILabel!
    @IBOutlet weak var noMemberLabel: UILabel!
    @IBOutlet weak var rsvpSegment: RESegmentedControl!{
        didSet {
            rsvpSegment.configure(segmentItems: customSegment.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    
    var eventID: String?
    var rsvpData: [RSVPNameResponse]?
    let customSegment: [EventDetailsModel] = [
        EventDetailsModel(title: "Yes", id: 1),
        EventDetailsModel(title: "No", id: 2),
        EventDetailsModel(title: "Maybe", id: 3)
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    lazy var customSimple3preset: SegmentedControlPresettable = {
        
        if #available(iOS 11.0, *) { }
        
        var preset = BootstapPreset(backgroundColor: .clear,selectedBackgroundColor: .clear, textColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5), selectedTextColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1))

        preset.textFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        preset.selectedTextFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        return preset
    }()

    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onRSVPSegment(_ sender: Any) {
        if self.rsvpSegment.selectedSegmentIndex == 0{
            self.firstLabel.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.secondLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.thirdLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.rsvpNameAPI(rsvpType: "1")
//            print("Yes segment")
        }else if self.rsvpSegment.selectedSegmentIndex == 1{
            self.firstLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.secondLabel.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.thirdLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.rsvpNameAPI(rsvpType: "2")
//            print("No Segment")
        }else if self.rsvpSegment.selectedSegmentIndex == 2{
            self.firstLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.secondLabel.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.thirdLabel.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.rsvpNameAPI(rsvpType: "3")
//            print("Maybe Segment")
        }
    }
    func ifnoMember(membercount: Int?, segment: String?){
        
        if membercount == 0{
            self.noMemberLabel.isHidden = false
            if segment == "1"{
                self.noMemberLabel.text = "No attendees confirmed."
            }else if segment == "2"{
                self.noMemberLabel.text = "No declines received."
            }else if segment == "3"{
                self.noMemberLabel.text = "No responses yet."
            }
        }else{
            self.noMemberLabel.isHidden = true
        }
    }
}
// MARK: - TABLEVIEW DELEGATE
extension countDetailsScreen: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.rsvpData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MemberCell") as! MemberCell
        cell.rsvpDetails = rsvpData?[indexPath.row]
        return cell
    }
}

//MARK: - API CALL
extension countDetailsScreen {
    func rsvpNameAPI(rsvpType: String?){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = RSVPNameRequest(eventId: self.eventID, type: rsvpType)
            print(param.toJSON())
            GetMyEventDetailsServices.shared.getRSVPNames(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response?.rsvpNameResponse{
                    self?.rsvpData = data
                    self?.rsvpTableView.reloadData()
                    self?.ifnoMember(membercount: self?.rsvpData?.count, segment: rsvpType)
                }
            } failure: { [weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        } else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
