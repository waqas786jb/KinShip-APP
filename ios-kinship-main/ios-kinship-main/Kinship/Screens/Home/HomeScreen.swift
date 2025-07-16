//
//  HomeScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit
import LGSideMenuController

class HomeScreen: UIViewController {
    
    //MARK: - Outlets
    @IBOutlet weak var kinshipTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "KinshipGroupCell", bundle: nil)
            kinshipTableView.register(nib, forCellReuseIdentifier: "KinshipGroupCell")
        }
    }
    @IBOutlet weak var groupLabel: UILabel!
    @IBOutlet weak var letKnowEachOther: UIView!
    @IBOutlet weak var homePageStackView: UIStackView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var errorMessageLabel: UILabel!
    @IBOutlet weak var ConnectMessageLabel: UILabel!
    @IBOutlet weak var notificationButton: UIButton!
    @IBOutlet weak var cityLabel: UILabel!
    
//    MARK: - VARIABLES
    var homeData:HomePageResponse?
    var firstName = Utility.getUserData()?.profile?.firstName ?? ""
    var lastName = Utility.getUserData()?.profile?.lastName ?? ""
    var chatGroupId: String?
    var groupName: String?
    
    // MARK: - METHODS
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialDetail()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.groupAPI()
    }
    
    // MARK: - FUNCTIONS
    func initialDetail(){
        self.tabBarController?.tabBar.isHidden = false
        self.nameLabel.text = "\(Utility.getUserData()?.profile?.firstName ?? "") \(Utility.getUserData()?.profile?.lastName ?? "")"
        if Utility.getUserData()?.profile?.kinshipReason == 1 || Utility.getUserData()?.profile?.kinshipReason == 2{
            self.errorMessageLabel.text = "We're working to connect you with other moms-to-be. This can take up to 24 hours. Be sure to enable push notifications so we can tell you when your kinship is ready!"
            self.ConnectMessageLabel.text = "Connected you with other moms-to-be."
        }else{
            self.errorMessageLabel.text = "We're working to connect you with other moms. This can take up to 24 hours. Be sure to enable push notifications so we can tell you when your kinship is ready!"
            self.ConnectMessageLabel.text = "Connected you with other moms."
        }
    }

    // MARK: - IB - ACTION
    @IBAction func onNotificationSetting(_ sender: Any) {
        let vc = STORYBOARD.notification.instantiateViewController(withIdentifier: "NotificationScreen") as! NotificationScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func onCommunityScreen(_ sender: Any) {
        let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "MyCommunitiesScreen") as! MyCommunitiesScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

//MARK: - TableView Delegates
extension HomeScreen : UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "KinshipGroupCell", for: indexPath) as! KinshipGroupCell
        cell.item = homeData
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ChatScreen") as! ChatScreen
        vc.isFromDirectChat = false
        vc.chatGroupId = self.chatGroupId
        CHAT_GROUP_ID = self.chatGroupId
        self.tabBarController?.navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - API CALL
extension HomeScreen {
    func groupAPI() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            HomePageServices.shared.getMyGroupApi { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.homePageResponse {
                    self?.homeData = data
                    self?.chatGroupId = data.chatGroupId
                    UserDefaults.standard.setValue(data.groupName, forKey: "GROUP_NAME")
                    UserDefaults.standard.setValue(data.chatGroupId, forKey: "GROUP_CHATID")
                    if data.groupId != nil{
                        IS_GROUP_CREATED = true
                        GROUP_MEMBER_COUNT = data.count
                        self?.homePageStackView.isHidden = false
                        self?.letKnowEachOther.isHidden = true
                        if data.notificationCount == 0{
                            self?.notificationButton.setImage(UIImage(named: "ic_notification_blank"), for: .normal)
                        }else{
                            self?.notificationButton.setImage(UIImage(named: "ic_notification"), for: .normal)
                        }
                        self?.cityLabel.text = data.city
                        self?.kinshipTableView.reloadData()
                    }else{
                        self?.homePageStackView.isHidden = true
                        self?.letKnowEachOther.isHidden = false
//                        Utility.showAlert(message: response?.message ?? "")
                    }
                }
                else{
                    self?.homePageStackView.isHidden = true
                    self?.letKnowEachOther.isHidden = false
//                    Utility.showAlert(message: response?.message ?? "")
                }
            } failure: { (error) in
                Utility.hideIndicator()
                
                self.homePageStackView.isHidden = true
                self.letKnowEachOther.isHidden = false
                
//                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
