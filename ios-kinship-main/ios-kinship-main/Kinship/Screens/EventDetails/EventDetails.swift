//
//  EventDetails.swift
//  Kinship
//
//  Created by iMac on 10/04/24.
//

import UIKit
import RESegmentedControl
import SafariServices
import DropDown

class MyEventModel {
    var partyOwnerName: String?
    var Date: String?
    var time: String?
    var location: String?
    var yesCount: Int?
    var maybeCount: Int?
}

class EventDetails: UIViewController {
//     MARK: - OUTLETS
    @IBOutlet weak var eventDetailsSegment: RESegmentedControl!{
        didSet {
            eventDetailsSegment.configure(segmentItems: customSegment.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    
    @IBOutlet weak var eventDetailsTableView: UITableView!{
        didSet{
                eventDetailsTableView.register(UINib(nibName: "MyEventCell", bundle: nil), forCellReuseIdentifier: "MyEventCell")
            
                let nib = UINib(nibName: "UpcomingEventsCell", bundle: nil)
                eventDetailsTableView.register(nib, forCellReuseIdentifier: "UpcomingEventsCell")
            }
    }
    @IBOutlet weak var firstSegmentBackground: UILabel!
    @IBOutlet weak var secondSegmentBackground: UILabel!
    @IBOutlet weak var noEventLabel: UILabel!
    
    
    //    MARK: - VARIABLES
    let customSegment: [EventDetailsModel] = [
        EventDetailsModel(title: "My Events", id: 1),
        EventDetailsModel(title: "Upcoming Events", id: 2)
    ]
    var getMyEvent: [GetMyEventResponse] = []
    var getMyUpcomingEvent: [GetMyUpcomingEventResponse] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.getMyEventAPI()
    }
    lazy var customSimple3preset: SegmentedControlPresettable = {
        
        if #available(iOS 11.0, *) { }
        
        var preset = BootstapPreset(backgroundColor: .clear,selectedBackgroundColor: .clear, textColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5), selectedTextColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1))

        preset.textFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        preset.selectedTextFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        return preset
    }()
    
//    MARK: - IB ACTIONS
    @IBAction func onEventDetailsSegment(_ sender: Any) {
        if self.eventDetailsSegment.selectedSegmentIndex == 0{
            self.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.eventDetailsTableView.reloadData()
            self.getMyEventAPI()
//            print("First segment")
        }else{
            self.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.eventDetailsTableView.reloadData()
            self.getUpcomingEvent()
//            print("Second Segment")
        }
    }
    
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}

// MARK: - TABLEVIEW DELEGETA , DATA-SOURSE
extension EventDetails: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.eventDetailsSegment.selectedSegmentIndex == 0{
            if getMyEvent.count == 0 {
                self.eventDetailsTableView.isHidden = true
                self.noEventLabel.isHidden = false
                self.noEventLabel.text = "No Event yet"
                return 0
            }else{
                self.eventDetailsTableView.isHidden = false
                self.noEventLabel.isHidden = true
                return getMyEvent.count
            }
        }else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
            if getMyUpcomingEvent.count == 0 {
                self.eventDetailsTableView.isHidden = true
                self.noEventLabel.isHidden = false
                self.noEventLabel.text = "No Upcoming Event yet"
                return 0
            }else{
                self.eventDetailsTableView.isHidden = false
                self.noEventLabel.isHidden = true
                return getMyUpcomingEvent.count
            }
        }else {
            return 0
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if self.eventDetailsSegment.selectedSegmentIndex == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "MyEventCell") as! MyEventCell
            cell.eventDetails = getMyEvent[indexPath.row]
            cell.zoomButtonLink = { [weak self] value in
//                print(value)
                if Utility.verifyUrl(urlString: value) || value.contains("www."){
                    var velidUrl: String
                    if value.starts(with: "www.") {
                        velidUrl = "https://" + value
                    }else{
                        velidUrl = value
                    }
                    guard let urlLink = URL(string: velidUrl) else{ return }
                    let sfSafari = SFSafariViewController(url: urlLink)
                    self?.present(sfSafari, animated: true)
                }
            }
            cell.viewDetails = { [weak self] in
                let vc = STORYBOARD.eventDetails.instantiateViewController(withIdentifier: "countDetailsScreen") as! countDetailsScreen
                vc.eventID = self?.getMyEvent[indexPath.row].Id
                self?.navigationController?.pushViewController(vc, animated: true)
            }
            cell.editClick = { [weak self] value in
                let vc = STORYBOARD.events.instantiateViewController(withIdentifier: "CreateEventScreen") as! CreateEventScreen
                vc.isFromEditEventScreen = true
                vc.eventId = value
                vc.getMyEvent = self?.getMyEvent[indexPath.row]
                vc.eventDate = String(self?.getMyEvent[indexPath.row].startTime ?? 0)
                vc.startTime = String(self?.getMyEvent[indexPath.row].startTime ?? 0)
                vc.endTime = String(self?.getMyEvent[indexPath.row].endTime ?? 0)
                self?.navigationController?.pushViewController(vc, animated: true)
            }
            cell.deleteClick = { [weak self] value in
                let vc = STORYBOARD.eventDetails.instantiateViewController(withIdentifier: "DeleteEventScreen") as! DeleteEventScreen
                vc.eventId = value
                vc.delClick = { [weak self] value in
                    self?.getMyEvent.remove(at: indexPath.row)
                    self?.eventDetailsTableView.reloadData()
                }
                vc.modalPresentationStyle = .custom
                vc.modalTransitionStyle = .crossDissolve
                self?.present(vc, animated: true, completion: nil)
            }
            return cell
        }
        else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
                let cell = tableView.dequeueReusableCell(withIdentifier: "UpcomingEventsCell") as! UpcomingEventsCell
                cell.upcomingEventData = getMyUpcomingEvent[indexPath.row]
                cell.zoomButton = { [weak self] value in
                    if Utility.verifyUrl(urlString: value) || value.contains("www."){
                        var velidUrl: String
                        if value.starts(with: "www.") {
                            velidUrl = "https://" + value
                        }else{
                            velidUrl = value
                        }
                        guard let urlLink = URL(string: velidUrl) else{ return }
                        let sfSafari = SFSafariViewController(url: urlLink)
                        self?.present(sfSafari, animated: true)
                    }
                }
                return cell
        }else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
            return cell 
        }
    }
}

//MARK: - API CALL
extension EventDetails{
    
//    Get My Event
    func getMyEventAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            GetMyEventDetailsServices.shared.myEvent { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.getMyEventResponse {
                    self?.getMyEvent = data
                    self?.eventDetailsTableView.reloadData()
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
//    Get My Upcoming Event
    func getUpcomingEvent(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            GetMyEventDetailsServices.shared.muUpcomingEvent { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.getMyUpcomingEventResponse {
                    self?.getMyUpcomingEvent = data
                    self?.eventDetailsTableView.reloadData()
                }else{
//                    print("No data print")
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
