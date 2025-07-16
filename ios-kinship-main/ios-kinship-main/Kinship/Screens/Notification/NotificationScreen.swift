//
//  NotificationScreen.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import UIKit

class NotificationScreen: UIViewController {
    
    // MARK: - IBOUTLET
    @IBOutlet weak var noRecordLabel: UILabel!
    @IBOutlet weak var notificationTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "NotificationCell", bundle: nil)
            self.notificationTableView.register(nib, forCellReuseIdentifier: "NotificationCell")
        }
    }
    
//    MARK: - VARIABLES
    var notification: [NotificationResponse] = []
    var communityMeta: CommunityListingMeta?
    var perPage = 25
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialize()
    }
    
    func initialize(){
        self.notificationTableView.dataSource = self
        self.notificationTableView.delegate = self
        self.notificationTableView.showsVerticalScrollIndicator = false
        self.notificationTableView.showsHorizontalScrollIndicator = false
        self.getNotificaton(page: 1, perPage: self.perPage, isNotShowloader: true)
    }

    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onNotificationSetting(_ sender: Any) {
        let vc = STORYBOARD.notification.instantiateViewController(withIdentifier: "NotificationSettingScreen") as! NotificationSettingScreen
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - Extension
extension NotificationScreen {
    func noRecord(){
        if self.notification.count == 0 {
            self.noRecordLabel.isHidden = false
        } else {
            self.noRecordLabel.isHidden = true
        }
    }
}

// MARK: - TableView delegate
extension NotificationScreen : UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return notification.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = self.notificationTableView.dequeueReusableCell(withIdentifier: "NotificationCell", for: indexPath) as! NotificationCell
        cell.notificationDetails = notification[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        print("Selected Type --> \(notification[indexPath.row].type ?? 00)")
        if notification[indexPath.row].type ?? 0 == 1 {
            if let vc = STORYBOARD.notification.instantiateViewController(withIdentifier: "MessageSearchScreen") as? MessageSearchScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.subId = self.notification[indexPath.row].subId
                vc.type = self.notification[indexPath.row].type
                vc.isFrom = self.notification[indexPath.row].type
                self.navigationController?.pushViewController(vc, animated: true)
            }
        } else if notification[indexPath.row].type ?? 0 == 2 {
            if let vc = STORYBOARD.notification.instantiateViewController(withIdentifier: "MessageSearchScreen") as? MessageSearchScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.subId = self.notification[indexPath.row].subId
                vc.type = self.notification[indexPath.row].type
                vc.isFrom = self.notification[indexPath.row].type
                self.navigationController?.pushViewController(vc, animated: true)
            }
        } else if notification[indexPath.row].type ?? 0 == 3 {
            if let vc = STORYBOARD.events.instantiateViewController(withIdentifier: "EventDetailsScreen") as? EventDetailsScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.type = 3
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }  else if notification[indexPath.row].type ?? 0 == 4 {
            if let vc = STORYBOARD.events.instantiateViewController(withIdentifier: "EventDetailsScreen") as? EventDetailsScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.type = 4
                self.navigationController?.pushViewController(vc, animated: true)
            }
        } else if notification[indexPath.row].type ?? 0 == 5 {
            if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "SearchCommunityScreen") as? SearchCommunityScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.type = notification[indexPath.row].type
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }else if notification[indexPath.row].type ?? 0 == 6 {
            if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "SearchCommunityScreen") as? SearchCommunityScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.type = notification[indexPath.row].type
                vc.postId = notification[indexPath.row].mainId
                self.navigationController?.pushViewController(vc, animated: true)
            }
        } else if notification[indexPath.row].type ?? 0 == 7 {
            if let vc = STORYBOARD.Community.instantiateViewController(withIdentifier: "commentScreen") as? commentScreen {
                vc.mainId = notification[indexPath.row].mainId
                vc.type = notification[indexPath.row].type
                vc.postId = notification[indexPath.row].mainId
                vc.isCommunityJoin = false
                vc.isFromNotificationScreen = true
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row == self.notification.count - 3 {
            //load more into data here
            if self.communityMeta?.lastPage != self.communityMeta?.currentPage {
                let page = (self.communityMeta?.currentPage ?? 0) + 1
                self.getNotificaton(page: page, perPage: self.perPage, isNotShowloader: false)
            }
        }
     }
}

//MARK: - API CALLING
extension NotificationScreen {
    func getNotificaton(page: Int, perPage: Int, isNotShowloader: Bool){
        if Utility.isInternetAvailable() {
//            if isNotShowloader {
                Utility.showIndicator(view: view)
//            }
            let param = GetNotificationRequest(page: page, perPage: perPage)
            NotificationServices.shared.getNotification(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let metaData = response?.communityListingMeta {
                    self?.communityMeta = metaData
                }
                if let data = response?.notificationResponse {
                    self?.notification.append(contentsOf: data)
                    self?.notificationTableView.reloadData()
                    self?.noRecord()
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
