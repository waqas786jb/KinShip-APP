//
//  SearchCommunityScreen.swift
//  Kinship
//
//  Created by iMac on 16/12/24.
//

import UIKit

class SearchCommunityScreen: UIViewController {
    
    // MARK: - IB Outlets
    @IBOutlet weak var communityTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "CommunityCell", bundle: nil)
            self.communityTableView.register(nib, forCellReuseIdentifier: "CommunityCell")
        }
    }
    
    // MARK: - Variables
    var communityData = [CommunityListingData]()
    var mainId: String?
    var type: Int?
    var postId: String?
    
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
extension SearchCommunityScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.communityData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CommunityCell", for: indexPath) as! CommunityCell
        cell.data = self.communityData[indexPath.row]
        cell.onImage = {[weak self] in
            if let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ImageOpenScreen") as? ImageOpenScreen {
                vc.imageData = self?.communityData[indexPath.row].file
                self?.navigationController?.pushViewController(vc, animated: true)
            }
        }
        cell.onComment = {[weak self] in
            if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "commentScreen") as? commentScreen {
                vc.postDetails = self?.communityData[indexPath.row]
                vc.mainId = self?.mainId
                vc.type = self?.type
                vc.isFromNotificationScreen = false
                vc.isCommunityJoin = false
                vc.addCommentCloser = { [weak self] comment in
                    self?.communityData[indexPath.row].commentCount = (self?.communityData[indexPath.row].commentCount ?? 0) + comment
                    if let index = self?.communityData.firstIndex(where: { $0.Id ?? "" == self?.communityData[indexPath.row].Id }) {
                        let indexPath = IndexPath(row: index, section: 0)
                        self?.communityTableView.reloadRows(at: [indexPath], with: .none)
                    }
                }
                self?.navigationController?.pushViewController(vc, animated: true)
            }
        }
        cell.onLike = {[weak self] in
            self?.likePost(postId: self?.communityData[indexPath.row].Id, like: self?.communityData[indexPath.row].isLiked == true ? "false" : "true") { response in
                self?.communityData[indexPath.row].isLiked = self?.communityData[indexPath.row].isLiked == true ? false : true
                self?.communityData[indexPath.row].like = self?.communityData[indexPath.row].isLiked == true ? (self?.communityData[indexPath.row].like ?? 0) + 1 : (self?.communityData[indexPath.row].like ?? 0) - 1
                self?.communityTableView.reloadRows(at: [indexPath], with: .none)
            }
        }
        return cell
    }
}

// MARK: - API Call
extension SearchCommunityScreen {
    func communityList(mainId: String, type: Int){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = searchCommunityGetPostRequest(mainId: mainId, type: type)
            Community.shared.searchCommunityGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response.communityListingData {
                    self?.communityData.append(contentsOf: data)
                    self?.communityTableView.reloadData()
                    let index = self?.communityData.firstIndex(where: { $0.Id ?? "" == self?.mainId })
                    let indexPath = IndexPath(row: index ?? 0, section: 0)
                    self?.communityTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
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
    
    func likePost(postId: String?, like: String?, complation: @escaping (String) -> ()){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = postLikeRequest(postId: postId, like: like)
            Community.shared.likeOnPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let message = response.message {
                    complation(message)
                }
            } failure: { (error) in
//                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
