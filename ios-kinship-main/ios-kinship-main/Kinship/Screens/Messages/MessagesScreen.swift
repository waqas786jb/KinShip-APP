//
//  MessagesScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit

class MessagesScreen: UIViewController {

//    MARK: - IBOUTLETS
    @IBOutlet weak var messageDetailsTableView: UITableView!{
        didSet{
            self.messageDetailsTableView.register(UINib(nibName: "DirectMessageCell", bundle: nil), forCellReuseIdentifier: "DirectMessageCell")
            
            self.messageDetailsTableView.register(UINib(nibName: "DateCell", bundle: nil), forCellReuseIdentifier: "DateCell")
        }
    }
    @IBOutlet weak var editButton: UIButton!
    @IBOutlet weak var noChatLabel: UILabel!
    
//    MARK: - VARIABLES
    var userGroupData: [UserGroupListResponse] = []
    var refreshControl : UIRefreshControl!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.getUserGroupList()
    }
    func initilize(){
        self.pullToRefresh()
        self.messageDetailsTableView.showsHorizontalScrollIndicator = false
        self.messageDetailsTableView.showsVerticalScrollIndicator = false
    }
    func pullToRefresh(){
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.black
        self.refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        self.messageDetailsTableView.addSubview(self.refreshControl)
        self.noMessage()
    }
    func noMessage(){
        if self.userGroupData.count == 0 {
            self.noChatLabel.isHidden = false
        } else {
            self.noChatLabel.isHidden = true
        }
    }
    @objc func refresh(_ refreshControl: UIRefreshControl) {
        // Do your job, when done:
        self.getUserGroupList()
        refreshControl.endRefreshing()
    }
//    MARK: - IBACTION
    @IBAction func onEdit(_ sender: Any) {
        let vc = STORYBOARD.messages.instantiateViewController(withIdentifier: "NewMessageScreen") as! NewMessageScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

extension MessagesScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.userGroupData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DirectMessageCell") as! DirectMessageCell
        cell.userGroupListData = self.userGroupData[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ChatScreen") as! ChatScreen
        vc.isFromDirectChat = true
        vc.chatData = self.userGroupData[indexPath.row]
        vc.chatGroupId = self.userGroupData[indexPath.row].Id
        vc.receiverId = self.userGroupData[indexPath.row].receiverId
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - API CALLING
extension MessagesScreen {
    
    func getUserGroupList(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            DirectMessageServices.shared.getUserGroupList { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.userGroupListResponse {
                    self?.userGroupData = data
                    self?.noMessage()
                    self?.messageDetailsTableView.reloadData()
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
