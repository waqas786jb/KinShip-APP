//
//  EventSearchScreen.swift
//  Kinship
//
//  Created by iMac on 18/12/24.
//

import UIKit
import SafariServices

class EventSearchScreen: UIViewController {
    
    // MARK: - IB Outlets
    @IBOutlet weak var eventNameLabel: UILabel!
    @IBOutlet weak var eventTableView: UITableView!{
        didSet{
            self.eventTableView.register(UINib(nibName: "MyEventCell", bundle: nil), forCellReuseIdentifier: "MyEventCell")
        
            let nib = UINib(nibName: "UpcomingEventsCell", bundle: nil)
            self.eventTableView.register(nib, forCellReuseIdentifier: "UpcomingEventsCell")
        }
    }
    
    // MARK: - Variables
    var type: Int?
    var mainId: String?
    var getMyEvent: [GetMyEventResponse] = []
    var getMyUpcomingEvent: [GetMyUpcomingEventResponse] = []
    
    // MARK: - Method’s
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    func initialise() {
        self.communityList(mainId: self.mainId ?? "", type: self.type ?? 0)
    }
    
    func manageUI() {
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    
}
// MARK: - Extension


// MARK: - Delegate Method
extension EventSearchScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.type == 3{
            return self.getMyEvent.count
        }else if self.type == 4 {
            return self.getMyUpcomingEvent.count
        }else {
            return 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        if self.type == 3 {
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
            return cell
        } else if self.type == 4 {
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

// MARK: - API Call
extension EventSearchScreen {
    func communityList(mainId: String, type: Int){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = searchCommunityGetPostRequest(mainId: mainId, type: type)
            Community.shared.searchCommunityGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if self?.type == 3 {
                    if let data = response.getMyEventResponse {
                        self?.getMyEvent = data
                        self?.eventNameLabel.text = "All Events"
                        self?.eventTableView.reloadData()
                    }
                } else {
                    if let data = response.getMyUpcomingEventResponse {
                        self?.getMyUpcomingEvent = data
                        self?.eventNameLabel.text = "Upcoming Events"
                        self?.eventTableView.reloadData()
                        let index = self?.getMyUpcomingEvent.firstIndex(where: { $0.Id ?? "" == self?.mainId })
                        let indexPath = IndexPath(row: index ?? 0, section: 0)
                        self?.eventTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
                    }
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
                if self.type == 3 {
                    self.navigationController?.popViewController(animated: true)
                }
                
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
