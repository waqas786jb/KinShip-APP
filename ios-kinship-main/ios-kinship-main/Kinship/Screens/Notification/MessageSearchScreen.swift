//
//  MessageSearchScreen.swift
//  Kinship
//
//  Created by iMac on 18/12/24.
//

import UIKit
import SafariServices

class MessageSearchScreen: UIViewController {
    
    // MARK: - IB Outlets
    @IBOutlet weak var personNameLabel: UILabel!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var directChatView: UIView!
    @IBOutlet weak var groupChatView: UIView!
    @IBOutlet weak var groupNameLabel: UILabel!
    @IBOutlet weak var messageTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "GroupChatCell", bundle: nil)
            self.messageTableView.register(nib, forCellReuseIdentifier: "GroupChatCell")
            
            let nameChangeNib = UINib(nibName: "GroupNameChange", bundle: nil)
            self.messageTableView.register(nameChangeNib, forCellReuseIdentifier: "GroupNameChange")
        }
    }
    
    // MARK: - Variables
    var mainId: String?
    var isFrom: Int?
    var subId: String?
    var type: Int?
    var roomId: String?
    var chatData: [ChatImageLinkMessageResponse] = []
    
    // MARK: - Method’s
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        let groupId = UserDefaults.standard.value(forKey: "GROUP_CHATID") as? String ?? ""
        
        var parameter = [String:Any]()
        
        parameter = ["senderId": Utility.getUserData()?.Id ?? "",
                     "receiverId": "",
                     "groupId": groupId,
                     "roomId": self.roomId ?? ""]
        
        print("parameter: ", parameter)

        SocketHelper.Events.roomDisconnect.emit(params: parameter)
    }
    
    // MARK: - Function’s
    func initialise() {
        self.searchComent(mainId: self.mainId ?? "", type: self.type ?? 0, subId: self.subId ?? "")
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
// MARK: - Extension
extension MessageSearchScreen {
    func roomConnect(senderId: String, groupID: String){
        var parameter = [String:Any]()
        parameter = ["senderId": senderId,
                     "receiverId": "",
                     "groupId": groupID] as [String:Any]
//        print("Get Room id parameter \(parameter)")
        SocketHelper.Events.createRoom.emit(params: parameter)
        
        SocketHelper.Events.roomConnected.listen { [weak self] (result) in
            guard let self = self else { return }
            if let result = result as? String {
                self.roomId = result
                print("ROOM ID:", self.roomId ?? "")
            }
        }
    }
}

// MARK: - Delegate Method
extension MessageSearchScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.chatData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if self.chatData[indexPath.row].type == 5{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GroupNameChange") as! GroupNameChange
            cell.nameChamgeMessageLabel.text = self.chatData[indexPath.row].message
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "GroupChatCell", for: indexPath) as! GroupChatCell
            cell.chatData = self.chatData[indexPath.row]
            cell.likeButton.isHidden = self.type == 2 ? true : false
            cell.image = { value in
                let vc = STORYBOARD.home.instantiateViewController(identifier: "ImageOpenScreen") as! ImageOpenScreen
                vc.imageData = self.chatData[indexPath.row].image
                self.navigationController?.pushViewController(vc, animated: true)
            }
            cell.link = { link in
                var velidUrl: String
                if link.starts(with: "www.") {
                    velidUrl = "https://" + link
                }else{
                    velidUrl = link
                }
                guard let urlLink = URL(string: velidUrl) else{ return }
                let sfSafari = SFSafariViewController(url: urlLink)
                self.present(sfSafari, animated: true)
            }
            return cell
        }
    }
}

// MARK: - API Call
extension MessageSearchScreen {
    func searchComent(mainId: String, type: Int, subId: String){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = searchCommunityGetPostRequest(mainId: mainId, type: type, subId: subId)
            Community.shared.searchCommunityGetPost(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response.chatImageLinkMessageResponse {
                    self?.chatData = data.reversed()
                    self?.messageTableView.reloadData()
                    let index = self?.chatData.firstIndex(where: { $0.Id ?? "" == self?.subId })
                    let indexPath = IndexPath(row: index ?? 0, section: 0)
                    self?.messageTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
                    if self?.isFrom == 1 {
                        self?.directChatView.isHidden = true
                        self?.groupChatView.isHidden = false
                        self?.groupNameLabel.text = UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? ""
                        let groupId = UserDefaults.standard.value(forKey: "GROUP_CHATID") as? String ?? ""
                        self?.roomConnect(senderId: Utility.getUserData()?.Id ?? "", groupID: groupId)
                    } else {
                        if data[0].userId?.count ?? 0 > 2 {
                            self?.directChatView.isHidden = true
                            self?.groupChatView.isHidden = false
                            self?.groupNameLabel.text = response.grpObj?.name ?? ""
                        } else {
                            self?.directChatView.isHidden = false
                            self?.groupChatView.isHidden = true
                            self?.personNameLabel.text = response.grpObj?.name ?? ""
                        }
                        if let userDetails = response.grpObj {
                            if let image = userDetails.profileImage {
                                Utility.setImage(image, imageView: self?.profileImageView)
                            } else {
                                self?.profileImageView.image = UIImage(named: "place_holder_icon")
                            }
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
}
