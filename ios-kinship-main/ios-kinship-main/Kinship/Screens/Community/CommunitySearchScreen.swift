//
//  CommunitySearchScreen.swift
//  Kinship
//
//  Created by iMac on 18/11/24.
//

import UIKit

class CommunitySearchScreen: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var noCommunityLabel: UILabel!
    @IBOutlet weak var communitySearchTextField: UITextField!
    @IBOutlet weak var searchCommunityTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "MyCommunityCell", bundle: nil)
            self.searchCommunityTableView.register(nib, forCellReuseIdentifier: "MyCommunityCell")
        }
    }
    
    
    // MARK: - Variables
    var timer: Timer?
    var searchData: [ExploreCommunity]?
    var isFromCommunityScreen: Bool?
    var myCommunityData: [MyCommunityResponse]?
    var joinCommunityClosure: (()->Void)?
    var searchClosure: ((String)->Void)?
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.manageUI()
    }
    
    func manageUI() {
        self.communitySearchTextField.addTarget(self, action: #selector(SearchMessageScreen.textFieldDidChange(_:)), for: .editingChanged)
    }
    
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
}

// MARK: - Extension
extension CommunitySearchScreen {
    @objc func textFieldDidChange(_ textField: UITextField) {
        self.timer?.invalidate()  // Cancel any previous timer
        self.timer = Timer.scheduledTimer(timeInterval: 0.5, target: self, selector: #selector(performSearch), userInfo: nil, repeats: false)
    }
    
    @objc func performSearch() {
        if self.communitySearchTextField.text?.count ?? 0 != 0 {
            if self.isFromCommunityScreen == true {
                self.myCommunityList(searchText: self.communitySearchTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? "")
            } else {
                self.exploreCommunityList(searchText: self.communitySearchTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? "")
            }
        }else{
            self.searchData?.removeAll()
            self.noCommunity()
            self.searchCommunityTableView.reloadData()
        }
    }
    
    func noCommunity(){
        if self.isFromCommunityScreen == true {
            if self.myCommunityData?.count == 0 {
                self.noCommunityLabel.isHidden = false
            } else {
                self.noCommunityLabel.isHidden = true
            }
        } else {
            if self.searchData?.count == 0 {
                self.noCommunityLabel.isHidden = false
            } else {
                self.noCommunityLabel.isHidden = true
            }
        }
    }
}
// MARK: - Delegate Method
extension CommunitySearchScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.isFromCommunityScreen == true {
            return self.myCommunityData?.count ?? 0
        } else {
            return self.searchData?.count ?? 0
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCommunityCell") as! MyCommunityCell
        if self.isFromCommunityScreen == true {
            cell.data = self.myCommunityData?[indexPath.row]
        } else {
            cell.exploreCommunityData = self.searchData?[indexPath.row]
            cell.labelText = self.communitySearchTextField.text
        }
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "CommunityScreen") as? CommunityScreen {
            if self.isFromCommunityScreen == true {
                vc.communityId = self.myCommunityData?[indexPath.row].Id
                vc.communityName = self.myCommunityData?[indexPath.row].name
                vc.isFromExploreScreen = false
            } else {
                vc.communityId = self.searchData?[indexPath.row].Id
                vc.communityName = self.searchData?[indexPath.row].name
                vc.isFromExploreScreen = true
            }
            vc.joinCommunityClosure = { [weak self] in
                self?.communitySearchTextField.text = ""
                self?.myCommunityData?.removeAll()
                self?.searchData?.removeAll()
                self?.searchCommunityTableView.reloadData()
                self?.joinCommunityClosure?()
            }
            vc.seen0 = { [weak self] in
                self?.searchClosure?(self?.myCommunityData?[indexPath.row].Id ?? "")
                self?.communitySearchTextField.text = ""
                self?.myCommunityData?.removeAll()
                self?.searchData?.removeAll()
                self?.searchCommunityTableView.reloadData()
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

// MARK: - API Call
extension CommunitySearchScreen {
    func exploreCommunityList(searchText: String){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = exploreCommunityRequest(type: 2, search: searchText)
            Community.shared.exploreCommunity(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response.exploreCommunity {
                    self?.searchData = data
                    self?.searchCommunityTableView.reloadData()
                    self?.noCommunity()
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
    
    func myCommunityList(searchText: String){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = myCommunityRequest(search: searchText)
            Community.shared.myCommunitySearch(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response?.myCommunityResponse {
                    self?.myCommunityData = data
                    self?.searchCommunityTableView.reloadData()
                    self?.noCommunity()
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
