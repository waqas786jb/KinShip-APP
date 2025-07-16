//
//  MyCommunitiesScreen.swift
//  Kinship
//
//  Created by iMac on 28/10/24.
//

import UIKit

class MyCommunitiesScreen: UIViewController {
    // MARK: - IB Outlets
    @IBOutlet weak var noCommunityLabel: UILabel!
    @IBOutlet weak var communityTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "MyCommunityCell", bundle: nil)
            self.communityTableView.register(nib, forCellReuseIdentifier: "MyCommunityCell")
        }
    }
    
    
    // MARK: - Variables
    var myCommunityData: [MyCommunityResponse]?
    
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.manageUI()
        self.myCommunityList()
    }
    
    func manageUI() {
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSendSuggestion(_ sender: Any) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "sendSuggestion") as? sendSuggestion {
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func onExploreCommunity(_ sender: Any) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "ExploreCommunityScreen") as? ExploreCommunityScreen {
            vc.homeScreenAPI = { [weak self] in
                self?.myCommunityList()
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func onSearch(_ sender: Any) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "CommunitySearchScreen") as? CommunitySearchScreen {
            vc.isFromCommunityScreen = true
            vc.joinCommunityClosure = { [weak self] in
                self?.myCommunityList()
            }
            vc.searchClosure = { [weak self] id in
                if let indexPath = self?.myCommunityData?.filter({$0.Id == id}) {
                    indexPath.first?.unseenCount = 0
                    if let id = self?.myCommunityData?.firstIndex(where: {$0.Id == id}) {
                        let indexPath = IndexPath(row: id, section: 0)
                        self?.communityTableView.reloadRows(at: [indexPath], with: .none)
                    }
                }
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

// MARK: - Extension
extension MyCommunitiesScreen {
    func manageNoComment(){
        if self.myCommunityData?.count == 0 {
            self.noCommunityLabel.isHidden = false
        } else {
            self.noCommunityLabel.isHidden = true
        }
    }
}

// MARK: - Delegate Method
extension MyCommunitiesScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.myCommunityData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCommunityCell") as! MyCommunityCell
        cell.data = self.myCommunityData?[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "CommunityScreen") as? CommunityScreen {
            vc.communityId = self.myCommunityData?[indexPath.row].Id
            vc.communityName = self.myCommunityData?[indexPath.row].name
            vc.isFromExploreScreen = false
            vc.seen0 = { [weak self] in
                self?.myCommunityData?[indexPath.row].unseenCount = 0
                if let index = self?.myCommunityData?.firstIndex(where: { $0.Id ?? "" == self?.myCommunityData?[indexPath.row].Id }) {
                    let indexPath = IndexPath(row: index, section: 0)
                    self?.communityTableView.reloadRows(at: [indexPath], with: .none)
                }
            }
            vc.leaveCommunity = { [weak self] in
                self?.myCommunityData?.remove(at: indexPath.row)
                self?.communityTableView.reloadData()
                self?.manageNoComment()
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

// MARK: - API Call
extension MyCommunitiesScreen {
    func myCommunityList(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            Community.shared.myCommunity { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.myCommunityResponse {
                    self?.myCommunityData = data
                    self?.communityTableView.reloadData()
                    self?.manageNoComment()
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
