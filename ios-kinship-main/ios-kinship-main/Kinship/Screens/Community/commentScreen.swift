//
//  commentScreen.swift
//  Kinship
//
//  Created by iMac on 07/11/24.
//

import UIKit
import IQKeyboardManagerSwift

class commentScreen: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var profileImage: dateSportImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var postImageView: UIImageView!
    @IBOutlet weak var tableViewHeight: NSLayoutConstraint!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var imageHideView: UIView!
    @IBOutlet weak var likeCountLabel: UILabel!
    @IBOutlet weak var commentsCount: UILabel!
    @IBOutlet weak var noCommentLabel: UILabel!
    @IBOutlet weak var messageTextView: IQTextView!
    @IBOutlet weak var commmentTaxtView: dateSportView!
    @IBOutlet weak var likeImageView: UIImageView!
    @IBOutlet weak var commentScrollView: UIScrollView!
    @IBOutlet weak var likeBackGroundView: dateSportView!
    @IBOutlet weak var contentUIView: UIView!
    @IBOutlet weak var bottomContainerToSafeAreaConstraint: NSLayoutConstraint!
    @IBOutlet weak var commentTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "CommunityCell", bundle: nil)
            self.commentTableView.register(nib, forCellReuseIdentifier: "CommunityCell")
        }
    }
    
    
    // MARK: - Variables
    var postDetails: CommunityListingData?
    var commentData = [CommentListingData]()
    var commentMeta: CommentListingMeta?
    var perPage = 10
    var isCommunityJoin: Bool?
    var addCommentCloser: ((Int)->Void)?
    var countComment: Int?
    var refreshControl : UIRefreshControl!
    var isFromNotificationScreen: Bool = false
    var mainId: String?
    var type: Int?
    var postId: String?
    var heightToscroll: CGPoint?
    var isCountingHeight: Bool = true
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    // MARK: - Function’s
    func initialise() {
        
        if isFromNotificationScreen {
            self.searchComent(mainId: self.mainId ?? "", type: self.type ?? 0)
//            self.commentList(postId: self.mainId ?? "", page: 1, perPage: 10)
            self.manageUI()
        } else {
            self.commentList(postId: self.postDetails?.Id ?? "", page: 1, perPage: 10)
            self.manageUI()
        }
        self.pullToRefresh()
        self.setUpKeyboard()
    }
    
    func manageUI() {
        if self.isCommunityJoin == true {
            self.commmentTaxtView.isHidden = true
        } else {
            self.commmentTaxtView.isHidden = false
        }
        Utility.setImage(self.postDetails?.profileImage, imageView: self.profileImage)
        Utility.setImage(self.postDetails?.file, imageView: self.postImageView)
        self.nameLabel.text = "\(self.postDetails?.firstName ?? "") \(self.postDetails?.lastName ?? "")"
        self.dateLabel.text = "\(Utility.setTimeToDateFormate(timestamp: "\(self.postDetails?.createdAt ?? 0)", dateFormate: "d MMM, hh:mm a"))"
        self.messageLabel.text = self.postDetails?.message
        self.likeCountLabel.text = "\(self.postDetails?.like ?? 0)"
        
        
        if self.postDetails?.file == nil {
            self.imageHideView.isHidden = true
        } else {
            self.imageHideView.isHidden = false
        }
        
        if self.postDetails?.isLiked == true {
            self.likeImageView.image = UIImage(named: "ic_heart_fill")
            self.likeBackGroundView.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 0.200000003)
        } else {
            self.likeImageView.image = UIImage(named: "ic_heart")
            self.likeBackGroundView.backgroundColor = #colorLiteral(red: 0.9411764741, green: 0.9411764741, blue: 0.9411764741, alpha: 1)
        }
    }
    
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        if self.countComment != 0 {
            self.addCommentCloser?(self.countComment ?? 0)
        }
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSend(_ sender: Any) {
        if self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
            Utility.showAlert(message: "Please add comment text")
        } else {
            self.addComment()
        }
    }
    @IBAction func onPost(_ sender: Any) {
        if let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ImageOpenScreen") as? ImageOpenScreen {
            vc.imageData = self.postDetails?.file
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func onLike(_ sender: Any) {
        if self.isCommunityJoin == false {
            self.postDetails?.isLiked = self.postDetails?.isLiked == true ? false : true
            self.postDetails?.like = self.postDetails?.isLiked == true ? (self.postDetails?.like ?? 0) + 1 : (self.postDetails?.like ?? 0) - 1
            self.likePost(postId: self.postDetails?.Id, like: self.postDetails?.isLiked == true ? "true" : "false")
        } else {
            Utility.showAlert(message: "You need to join community")
        }
    }
}

// MARK: - Extension
extension commentScreen {
    func commentOrNot(){
        if self.commentData.count == 0 {
            self.noCommentLabel.isHidden = false
        } else {
            self.noCommentLabel.isHidden = true
        }
    }
    func pullToRefresh(){
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.black
        self.refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        self.commentTableView.addSubview(self.refreshControl)
    }
    
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        self.commentData.removeAll()
        self.commentTableView.reloadData()
        self.commentList(postId: self.postDetails?.Id ?? "", page: 1, perPage: 10, isNotShowloader: true)
        refreshControl.endRefreshing()
    }
    
    fileprivate func setUpKeyboard(){
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if ((notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.height) != nil {
            if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                if messageTextView.isFirstResponder == true{
                    self.bottomContainerToSafeAreaConstraint.constant = topSafeArea == 0 ? (keyboardSize.height) : (keyboardSize.height) - 20
                } else{
                    self.messageTextView.resignFirstResponder()
                }
            }
        }
    }
    @objc func keyboardWillHide(notification: NSNotification) {
        self.bottomContainerToSafeAreaConstraint.constant = 2
    }
    func scrollToSelectedRow() {
        self.commentTableView.isScrollEnabled = true
        let scrollPath = self.commentData.firstIndex(where: {$0.Id == self.postId}) ?? 0
        let indexPath = IndexPath(row: scrollPath, section: 0)
        self.commentTableView.scrollToRow(at: indexPath, at: .top, animated: true)
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.commentTableView.isScrollEnabled = false
        }
    }
    func estimatedHeightOfLabel(text: String) -> CGFloat {

        let size = CGSize(width: view.frame.width - 16, height: 1000)

        let options = NSStringDrawingOptions.usesFontLeading.union(.usesLineFragmentOrigin)

        let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 10)]

        let rectangleHeight = String(text).boundingRect(with: size, options: options, attributes: attributes, context: nil).height

        return rectangleHeight
    }
}

// MARK: - Delegate Method
extension commentScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.commentData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CommunityCell", for: indexPath) as! CommunityCell
        cell.commentData = self.commentData[indexPath.row]
        cell.onLike = {[weak self] in
            if self?.isCommunityJoin == false {
                self?.commentData[indexPath.row].isLiked = self?.commentData[indexPath.row].isLiked == true ? false : true
                self?.commentData[indexPath.row].like = self?.commentData[indexPath.row].isLiked == true ? (self?.commentData[indexPath.row].like ?? 0) + 1 : (self?.commentData[indexPath.row].like ?? 0) - 1
                self?.likeComment(commentId: self?.commentData[indexPath.row].Id, like: self?.commentData[indexPath.row].isLiked)
            } else {
                Utility.showAlert(message: "you need to join community")
            }
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        DispatchQueue.main.async {
            self.tableViewHeight.constant = tableView.contentSize.height
        }
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        if ((scrollView.contentOffset.y + scrollView.frame.size.height) >= scrollView.contentSize.height) {
            if self.commentMeta?.lastPage != self.commentMeta?.currentPage {
                let page = (self.commentMeta?.currentPage ?? 0) + 1
                self.commentList(postId: self.postDetails?.Id ?? "", page: page, perPage: self.perPage, isNotShowloader: true)
            }
        }
    }
}

// MARK: - API Call
extension commentScreen {
    func commentList(postId: String, page: Int, perPage: Int, isNotShowloader: Bool = false){
        if Utility.isInternetAvailable() {
            if isNotShowloader == false{
                Utility.showIndicator(view: view)
            }
            let param = commentGetPostRequest(postId: postId, page: page, perPage: perPage)
            Community.shared.commentGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let metaData = response.commentListingMeta {
                    self?.commentMeta = metaData
                    self?.commentsCount.text = "\(metaData.total ?? 0) Comments"
                }
                if let data = response.commentListingData {
                    self?.commentData.append(contentsOf: data)
                    self?.commentTableView.reloadData()
                    self?.commentOrNot()
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
    
    // MARK: - Sedd comment
    func addComment(){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = addCommentRequest(postId: self.postDetails?.Id, message: self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines))
            Community.shared.addNewPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let data = response.commentListingData2 {
                    let addedPostDetails = CommentListingData(Id: data.Id, userId: data.userId, message: data.text, like: 0, createdAt: data.createdAt, updatedAt: data.updatedAt, profileImage: data.profileImage, firstName: data.firstName, lastName: data.lastName)
                    self?.messageTextView.text = ""
                    self?.commentData.insert(addedPostDetails, at: 0)
                    self?.commentTableView.reloadData()
                    self?.commentsCount.text = "\(self?.commentData.count ?? 0) Comments"
                    self?.commentOrNot()
                    self?.countComment = (self?.countComment ?? 0) + 1
                    self?.view.endEditing(true)
                    let desiredOffset = CGPoint(x: 0, y: -(self?.commentScrollView.contentInset.top ?? 0) - -(self?.contentUIView.frame.size.height ?? 0) - 100)
                    self?.commentScrollView.setContentOffset(desiredOffset, animated: true)
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
    
    func likeComment(commentId: String?, like: Bool?){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = commentLikeRequest(commentId: commentId, like: like)
            Community.shared.likeOnComment(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let _ = response.message {
                    if let index = self?.commentData.firstIndex(where: { $0.Id ?? "" == commentId }) {
                        let indexPath = IndexPath(row: index, section: 0)
                        self?.commentTableView.reloadRows(at: [indexPath], with: .none)
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
    
    func likePost(postId: String?, like: String?){
        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
            let param = postLikeRequest(postId: postId, like: like)
            Community.shared.likeOnPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
                if let _ = response.message {
                    if self?.postDetails?.isLiked == true {
                        self?.likeImageView.image = UIImage(named: "ic_heart_fill")
                        self?.likeBackGroundView.backgroundColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 0.200000003)
                    } else {
                        self?.likeImageView.image = UIImage(named: "ic_heart")
                        self?.likeBackGroundView.backgroundColor = #colorLiteral(red: 0.9411764741, green: 0.9411764741, blue: 0.9411764741, alpha: 1)
                    }
                    self?.likeCountLabel.text = "\(self?.postDetails?.like ?? 0)"
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
    
    func searchComent(mainId: String, type: Int){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = searchCommunityGetPostRequest(mainId: mainId, type: type)
            Community.shared.searchCommunityGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let post = response.postResponse {
                    self?.postDetails = post
                }
                if let data = response.commentResponse {
                    self?.commentData.append(contentsOf: data)
                    self?.commentTableView.reloadData()
                    self?.commentOrNot()
                    self?.manageUI()
                    self?.commentsCount.text = "\(data.count) Comments"
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
