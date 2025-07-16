//
//  CommunityScreen.swift
//  Kinship
//
//  Created by iMac on 29/10/24.
//

import UIKit

class CommunityScreen: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var communityNameLAbel: UILabel!
    @IBOutlet weak var joinCommunityView: UIView!
    @IBOutlet weak var newPostView: dateSportView!
    @IBOutlet weak var leaveCommunityButton: UIButton!
    @IBOutlet weak var noPostLabel: UILabel!
    @IBOutlet weak var communityTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "CommunityCell", bundle: nil)
            self.communityTableView.register(nib, forCellReuseIdentifier: "CommunityCell")
        }
    }
    
    // MARK: - Variables
    var communityId: String?
    var communityName: String?
    var refreshControl : UIRefreshControl!
    var communityData = [CommunityListingData]()
    var communityMeta: CommunityListingMeta?
    var perPage = 10
    var page = 1
    var seen0: (() -> Void)?
    var leaveCommunity: (()->Void)?
    var joinCommunityClosure: (()->Void)?
    var isFromExploreScreen: Bool?
    var searchClear: (()->Void)?
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.manageUI()
        self.CommunityUIManage()
        self.communityList(communityId: self.communityId ?? "", page: self.page, perPage: self.perPage)
    }
    
    func manageUI() {
        self.pullToRefresh()
        self.communityNameLAbel.text = self.communityName
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.seen0?()
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onAddPost(_ sender: Any) {
        if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "AddPostScreen") as? AddPostScreen {
            vc.communityId = self.communityId
            vc.newPost = { [weak self] postData in
                self?.communityData.insert(CommunityListingData(Id: postData.Id, userId: postData.userId, message: postData.message, file: postData.file, like: 0, isLiked: false, commentCount: postData.commentCount, createdAt: postData.createdAt, updatedAt: postData.updatedAt, profileImage: postData.profileImage, firstName: postData.firstName, lastName: postData.lastName), at: 0)
                self?.communityTableView.scrollToTop()
                self?.noPost()
                self?.communityTableView.reloadData()
            }
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func onLeave(_ sender: Any) {
        let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "leaveCommunityScreen") as! leaveCommunityScreen
        vc.modalPresentationStyle = .overFullScreen
        vc.customerId = self.communityId
        vc.leaveCommunityClosure = {[weak self] in
            self?.leaveCommunity?()
            self?.joinCommunityClosure?()
            self?.navigationController?.popViewController(animated: true)
        }
        self.present(vc, animated: true, completion: nil)
    }
    @IBAction func inJoinCommunity(_ sender: Any) {
        self.joinCommunity()
    }
}

// MARK: - Extension
extension CommunityScreen {
    func pullToRefresh(){
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.black
        self.refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        self.communityTableView.addSubview(self.refreshControl)
    }
    
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        self.communityData.removeAll()
        self.communityTableView.reloadData()
        self.communityList(communityId: self.communityId ?? "", page: 1, perPage: 10)
        refreshControl.endRefreshing()
    }
    
    func CommunityUIManage(){
        if self.isFromExploreScreen == true {
            self.newPostView.isHidden = true
            self.joinCommunityView.isHidden = false
            self.leaveCommunityButton.isHidden = true
        } else {
            self.newPostView.isHidden = false
            self.joinCommunityView.isHidden = true
            self.leaveCommunityButton.isHidden = false
        }
    }
    
    func noPost(){
        if self.communityData.count == 0 {
            self.noPostLabel.isHidden = false
        } else {
            self.noPostLabel.isHidden = true
        }
    }
}

// MARK: - Delegate Method
extension CommunityScreen: UITableViewDelegate, UITableViewDataSource {
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
                vc.isCommunityJoin = self?.isFromExploreScreen
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
            if self?.isFromExploreScreen == false {
                self?.communityData[indexPath.row].isLiked = self?.communityData[indexPath.row].isLiked == true ? false : true
                self?.communityData[indexPath.row].like = self?.communityData[indexPath.row].isLiked == true ? (self?.communityData[indexPath.row].like ?? 0) + 1 : (self?.communityData[indexPath.row].like ?? 0) - 1
                self?.likePost(postId: self?.communityData[indexPath.row].Id, like: self?.communityData[indexPath.row].isLiked == true ? "true" : "false")
            } else {
                Utility.showAlert(message: "You need to join community")
            }
        }
        return cell
    }
   func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
       if indexPath.row == self.communityData.count - 1 {
           //load more into data here
           if self.communityMeta?.lastPage != self.communityMeta?.currentPage {
               let page = (self.communityMeta?.currentPage ?? 0) + 1
               self.communityList(communityId: self.communityId ?? "", page: page, perPage: self.perPage, isNotShowloader: true)
           }
       }
    }
}


// MARK: - API Call
extension CommunityScreen {
    func communityList(communityId: String, page: Int, perPage: Int, isNotShowloader: Bool = false){
        if Utility.isInternetAvailable() {
            if isNotShowloader == false{
                Utility.showIndicator(view: view)
            }
            let param = communityGetPostRequest(communityId: communityId, page: page, perPage: perPage)
            Community.shared.communityGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let metaData = response.communityListingMeta {
                    self?.communityMeta = metaData
                }
                if let data = response.communityListingData {
                    self?.communityData.append(contentsOf: data)
                    self?.noPost()
                    self?.communityTableView.reloadData()
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
    
    func likePost(postId: String?, like: String?){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = postLikeRequest(postId: postId, like: like)
            Community.shared.likeOnPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let _ = response.message {
                    if let index = self?.communityData.firstIndex(where: { $0.Id ?? "" == postId }) {
                        let indexPath = IndexPath(row: index, section: 0)
                        self?.communityTableView.reloadRows(at: [indexPath], with: .none)
                    }
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
    
    func joinCommunity(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = joinCommunityRequest(communityId: self.communityId)
            Community.shared.joinCommunity(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let msessage = response.message {
                    self?.isFromExploreScreen = false
                    Utility.successAlert(message: msessage)
                    self?.joinCommunityClosure?()
                    self?.CommunityUIManage()
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
