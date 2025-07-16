//
//  ExploreCommunityScreen.swift
//  Kinship
//
//  Created by iMac on 18/11/24.
//

import UIKit

class ExploreCommunityScreen: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var exploreCommunityTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "MyCommunityCell", bundle: nil)
            self.exploreCommunityTableView.register(nib, forCellReuseIdentifier: "MyCommunityCell")
        }
    }
    
    
    // MARK: - Variables
    var exploreCommunityData: [ExploreCommunity]?
    var isAPICall = false
    var homeScreenAPI: (()->Void)?
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.exploreCommunityList()
        self.manageUI()
    }
    
    func manageUI() {
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        if self.isAPICall == true {
            self.homeScreenAPI?()
        }
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSerach(_ sender: Any) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "CommunitySearchScreen") as? CommunitySearchScreen {
            vc.isFromCommunityScreen = false
            vc.joinCommunityClosure = { [weak self] in
                self?.isAPICall = true
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

// MARK: - Extension

// MARK: - Delegate Method
extension ExploreCommunityScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.exploreCommunityData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCommunityCell") as! MyCommunityCell
        cell.exploreCommunityData = self.exploreCommunityData?[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "CommunityScreen") as? CommunityScreen {
            vc.communityId = self.exploreCommunityData?[indexPath.row].Id
            vc.communityName = self.exploreCommunityData?[indexPath.row].name
            vc.isFromExploreScreen = true
            vc.joinCommunityClosure = { [weak self] in
                self?.isAPICall = true
                if self?.exploreCommunityData?.count ?? 0 != indexPath.row {
                    self?.exploreCommunityData?.remove(at: indexPath.row)
                }
                self?.exploreCommunityTableView.reloadData()
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

// MARK: - API Call
extension ExploreCommunityScreen {
    func exploreCommunityList(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = exploreCommunityRequest(type: 2)
            Community.shared.exploreCommunity(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response.exploreCommunity {
                    self?.exploreCommunityData = data
                    self?.exploreCommunityTableView.reloadData()
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
