//
//  EventDetailsScreen.swift
//  Kinship
//
//  Created by iMac on 23/04/24.
//

import UIKit
import SafariServices
import RESegmentedControl

class EventDetailsScreen: UIViewController {
    
//   MARK: - OUTLERS
    @IBOutlet weak var noEventLabel: UILabel!
    @IBOutlet weak var secondSegmentBackground: UILabel!
    @IBOutlet weak var firstSegmentBackground: UILabel!
    @IBOutlet weak var thirdSegmentBackground: UILabel!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var eventsTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "EventsCell", bundle: nil)
            self.eventsTableView.register(nib, forCellReuseIdentifier: "EventsCell")
            
            self.eventsTableView.register(UINib(nibName: "MyEventCell", bundle: nil), forCellReuseIdentifier: "MyEventCell")
            
            self.eventsTableView.register(UINib(nibName: "UpcomingEventsCell", bundle: nil), forCellReuseIdentifier: "UpcomingEventsCell")
        }
    }
    @IBOutlet weak var eventDetailsSegment: RESegmentedControl!{
        didSet {
            self.eventDetailsSegment.configure(segmentItems: customSegment.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    
//    MARK: - VARIABELS
    var getAllEvent: [AllEventResponse] = []
    var getMyEvent: [GetMyEventResponse] = []
    var getMyUpcomingEvent: [GetMyUpcomingEventResponse] = []
    var status: Int?
    var eventId: String?
    var isFirstTime = true
//    var homeData: HomePageResponse?
    var refreshControl : UIRefreshControl!
    var mainId: String?
    var isFromNotificationScreen: Bool = false
    var type: Int?
    let customSegment: [EventDetailsModel] = [
        EventDetailsModel(title: "All Events", id: 1),
        EventDetailsModel(title: "My Events", id: 2),
        EventDetailsModel(title: "Upcoming Events", id: 3)
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.eventsTableView.reloadData()
        self.pullToRefresh()
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.refreshControl.endRefreshing()
        self.forceHideRefreshControl(tableView: self.eventsTableView)
    }
    func scrollToIndex(){
        if self.isFromNotificationScreen {
            if let index = self.getAllEvent.firstIndex(where: { $0.Id ?? "" == self.mainId }) {
                let indexPath = IndexPath(row: index, section: 0)
                self.eventsTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
            } else {
                Utility.showAlert(message: "This event is completed")
            }
            
        }
    }
    lazy var customSimple3preset: SegmentedControlPresettable = {
        
        if #available(iOS 11.0, *) { }
        
        var preset = BootstapPreset(backgroundColor: .clear,selectedBackgroundColor: .clear, textColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5), selectedTextColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1))

        preset.textFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        preset.selectedTextFont = UIFont(name: "OpenSans-Bold", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .semibold)
        return preset
    }()
    func pullToRefresh(){
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.black
        
        self.refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)

        self.eventsTableView.addSubview(self.refreshControl)
    }
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        if self.eventDetailsSegment.selectedSegmentIndex == 0 {
            self.getMySelectableEventAPI()
            self.eventsTableView.reloadData()
        } else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
            self.getMyEventAPI(isPagination: true)
        } else if self.eventDetailsSegment.selectedSegmentIndex == 2 {
            self.getUpcomingEvent(isPagination: true)
        }
        refreshControl.endRefreshing()
    }
//    MARK: - IBACTION
    @IBAction func onAddNewEvent(_ sender: Any) {
        if IS_GROUP_CREATED == true {
            let vc = STORYBOARD.events.instantiateViewController(identifier: "CreateEventScreen") as! CreateEventScreen
            vc.isFromEditEventScreen = false
            vc.onBack = { [weak self] isFromCreate, eventData, eventRequest in
                if isFromCreate == true {
                    self?.getMyEvent.insert(GetMyEventResponse(Id: eventData?.Id, userId: eventData?.userId, eventName: eventData?.eventName, startTime: eventData?.startTime ?? 0, endTime: eventData?.endTime ?? 0, eventDate: Int(eventRequest?.eventDate ?? ""), isAllDay: eventRequest?.isAllDay == "1" ? true : false, location: eventData?.location, lat: eventData?.lat, long: eventData?.long, link: eventData?.link, photo: eventData?.photo, eventDescription: eventData?.eventDescription, yes: 0, no: 0, maybe: 0, createdAt: 00), at: 0)
                    if self?.eventDetailsSegment.selectedSegmentIndex == 1 {
                        self?.eventsTableView.reloadData()
                    }
                }
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }else{
            let vc = STORYBOARD.events.instantiateViewController(withIdentifier: "EventAlertScreen") as! EventAlertScreen
            vc.modalPresentationStyle = .custom
            vc.modalTransitionStyle = .crossDissolve
            self.present(vc, animated: true, completion: nil)
        }
    }
    @IBAction func onEventsSegment(_ sender: Any) {
        if self.eventDetailsSegment.selectedSegmentIndex == 0 {
            self.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.thirdSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            if self.isFirstTime == true {
                self.getMySelectableEventAPI()
                self.getMyEventAPI()
                self.getUpcomingEvent()
                self.isFirstTime = false
            }
            self.eventsTableView.reloadData()
        } else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
            self.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.thirdSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.eventsTableView.reloadData()
        } else if self.eventDetailsSegment.selectedSegmentIndex == 2 {
            self.thirdSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
            self.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
            self.eventsTableView.reloadData()
        }
    }
    @objc func handleRefresh(_ refreshControl: UIRefreshControl) {
        self.eventsTableView.reloadData()
        refreshControl.endRefreshing()
    }
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
// MARK: - Extension
extension EventDetailsScreen {
    func arrayIsEmptyOrNot(arrayCount: Int){
        if arrayCount == 0 {
            self.noEventLabel.isHidden = false
        } else {
            self.noEventLabel.isHidden = true
        }
    }
    
    func forceHideRefreshControl(tableView: UITableView) {
        if tableView.contentOffset.y < 0 { // Move tableView to top
            tableView.setContentOffset(CGPoint.zero, animated: true)
        }
    }
}

//MARK: - UITABLEVIEW DELEGATE AND DATA-SOURSE
extension EventDetailsScreen: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.eventDetailsSegment.selectedSegmentIndex == 0 {
            self.arrayIsEmptyOrNot(arrayCount: self.getAllEvent.count)
            return self.getAllEvent.count
        } else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
            self.arrayIsEmptyOrNot(arrayCount: self.getMyEvent.count)
            return self.getMyEvent.count
        } else if self.eventDetailsSegment.selectedSegmentIndex == 2 {
            self.arrayIsEmptyOrNot(arrayCount: self.getMyUpcomingEvent.count)
            return self.getMyUpcomingEvent.count
        }else {
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if self.eventDetailsSegment.selectedSegmentIndex == 0 {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "EventsCell") as! EventsCell
            self.eventsTableView.separatorStyle = .none
            cell.allEventData = self.getAllEvent[indexPath.row]
            self.noEventLabel.isHidden = true
            cell.onZoomLink = { value in
                var velidUrl: String
                if value.starts(with: "www.") {
                    velidUrl = "https://" + value
                }else{
                    velidUrl = value
                }
                guard let urlLink = URL(string: velidUrl) else { return }
                let sfSafari = SFSafariViewController(url: urlLink)
                self.present(sfSafari, animated: true)
            }
            cell.status = { status, eventID, eventData in
                self.status = status
                self.eventId = eventID
                self.setEventStatusAPI()
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [weak self] in
                    self?.getAllEvent.remove(at: indexPath.row)
                    self?.eventsTableView.reloadData()
                    if self?.getAllEvent.count == 0{
                        self?.noEventLabel.isHidden = false
                        self?.eventsTableView.separatorStyle = .none
                    }else{
                        self?.eventsTableView.separatorStyle  = .singleLine
                        self?.noEventLabel.isHidden = true
                    }
                }
                if status != 2 {
                    self.getMyUpcomingEvent.append(GetMyUpcomingEventResponse(Id: eventData.Id, userId: "", eventName: eventData.eventName, startTime: eventData.startTime as NSNumber?, endTime: eventData.endTime as NSNumber?, eventDate: eventData.eventDate as NSNumber?, isAllDay: 2 as NSNumber?, location: eventData.location, lat: eventData.lat, long: eventData.long, link: eventData.link, photo: eventData.photo, eventDescription: eventData.eventDescription, firstName: eventData.firstName, lastName: eventData.lastName))
                }
            }
            return cell
            
        } else if self.eventDetailsSegment.selectedSegmentIndex == 1 {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "MyEventCell") as! MyEventCell
            self.eventsTableView.separatorStyle = .none
            cell.eventDetails = getMyEvent[indexPath.row]
            cell.zoomButtonLink = { [weak self] value in
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
                vc.onBack = {[weak self] isFromCreate, eventData, eventRequest in
                    if self?.eventDetailsSegment.selectedSegmentIndex == 0 {
//                        print("Selected event API")
    //                    self?.getMySelectableEventAPI()
                    } else if self?.eventDetailsSegment.selectedSegmentIndex == 1 {
                        self?.getMyEventAPI()
                    }
                }
                self?.navigationController?.pushViewController(vc, animated: true)
            }
            cell.deleteClick = { [weak self] value in
                let vc = STORYBOARD.eventDetails.instantiateViewController(withIdentifier: "DeleteEventScreen") as! DeleteEventScreen
                vc.eventId = value
                vc.delClick = { [weak self] value in
                    self?.getMyEvent.remove(at: indexPath.row)
                    if self?.getMyEvent.count == 0 {
                        self?.noEventLabel.isHidden = false
                    } else {
                        self?.noEventLabel.isHidden = true
                    }
                    self?.eventsTableView.reloadData()
                }
                vc.modalPresentationStyle = .custom
                vc.modalTransitionStyle = .crossDissolve
                self?.present(vc, animated: true, completion: nil)
            }
            return cell
            
        } else if self.eventDetailsSegment.selectedSegmentIndex == 2 {
            
            let cell = tableView.dequeueReusableCell(withIdentifier: "UpcomingEventsCell") as! UpcomingEventsCell
            self.eventsTableView.separatorStyle = .singleLine
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
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
            return cell
        }
    }
}

//MARK: - API CALL
extension EventDetailsScreen {
//    Get all event
    func getMySelectableEventAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            GetMyEventDetailsServices.shared.getAllEvents { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.allEventsResponse {
                    DispatchQueue.main.async {
                        self?.getAllEvent = data
                        self?.eventsTableView.reloadData()
                        if self?.type == 3 {
                            self?.backButton.isHidden = false
                            if let index = self?.getAllEvent.firstIndex(where: { $0.Id ?? "" == self?.mainId }) {
                                let indexPath = IndexPath(row: index, section: 0)
                                self?.eventsTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
                            } else {
                                Utility.showAlert(message: "Your RSVP is alredy completed")
                            }
                        }
                    }
                }
            } failure: { (error) in
                Utility.hideIndicator()
                self.noEventLabel.isHidden = false
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
//    GET MY EVENT
    func getMyEventAPI(isPagination: Bool = false){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            GetMyEventDetailsServices.shared.myEvent { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response?.getMyEventResponse {
                    self?.getMyEvent = data
                    if isPagination == true{
                        self?.eventsTableView.reloadData()
                    }
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
    
    // Upcoming event
    func getUpcomingEvent(isPagination: Bool = false){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            GetMyEventDetailsServices.shared.muUpcomingEvent { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response?.getMyUpcomingEventResponse {
                    self?.getMyUpcomingEvent = data
                    if isPagination == true{
                        self?.eventsTableView.reloadData()
                    }
                    if self?.type == 4 {
                        self?.backButton.isHidden = false
                        self?.eventDetailsSegment.selectedSegmentIndex = 2
                        self?.eventDetailsSegment.preset.selectedTextColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
                        self?.thirdSegmentBackground.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
                        self?.firstSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
                        self?.secondSegmentBackground.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.5)
                        self?.eventsTableView.reloadData()
                        if let index = self?.getAllEvent.firstIndex(where: { $0.Id ?? "" == self?.mainId }){
                            let indexPath = IndexPath(row: index, section: 0)
                            self?.eventsTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
                        } else {
                            Utility.showAlert(message: "This event is completed")
                        }
                    }
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
    
//    Update status API
    func setEventStatusAPI(){
            if Utility.isInternetAvailable() {
                let param = StatusUpdateRequest(eventID: self.eventId, status: self.status)
                GetMyEventDetailsServices.shared.setEventStatus(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                    if let data = response.message{
                        Utility.successAlert(message: data)
                    }
                    
                } failure: { [weak self] (error) in
                    Utility.showAlert(vc: self, message: error)
                }
            } else {
                Utility.hideIndicator()
                Utility.showNoInternetConnectionAlertDialog(vc: self)
            }
    }
}
