//
//  MenubarScreen.swift
//  Kinship
//
//  Created by iMac on 04/04/24.
//

import UIKit
import LGSideMenuController

class funcanalityName {
    var id: Int
    var logoImage: String
    var name: String
    
    init(id: Int, logoImage: String, name: String) {
        self.id = id
        self.logoImage = logoImage
        self.name = name
    }
}
class MenubarScreen: UIViewController {
    
//    MARK: - IBOUTLET
    @IBOutlet weak var editButton: UIImageView!
    @IBOutlet weak var nameChangeButton: UIButton!
    @IBOutlet weak var groupNameLabel: UILabel!
    @IBOutlet weak var menubaarTableView: UITableView!{
        didSet{
            let dateNib = UINib(nibName: "GroupMenubaarCell", bundle: nil)
            self.menubaarTableView.register(dateNib, forCellReuseIdentifier: "GroupMenubaarCell")
            
            let nib = UINib(nibName: "MemberCell", bundle: nil)
            self.menubaarTableView.register(nib, forCellReuseIdentifier: "MemberCell")
        }
    }
    
//    MARK: - VARIABLES
    var isFromDirectChatGroup: Bool?
    var groupId: String?
    var memberList: [SubGroupMembersResponse]?
//    var newKinshipName: String? = nil
    var menuItemList: [funcanalityName] = []
//    var menuItemList = [funcanalityName(id: 2, logoImage: "ic_gallery", name: "Gallery"),
//                        funcanalityName(id: 3, logoImage: "ic_link-circle", name: " Links"),
//                        funcanalityName(id: 4, logoImage: "ic_search", name: "Search Messages"),
//                        funcanalityName(id: 5, logoImage: "ic_heart_red", name: "Liked Messages")]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.groupNameLabel.text = UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? ""
    }
    func initilize(){
//        self.groupAPI()
        self.notificationObserver()
    }
    
    func notificationObserver(){
        NotificationCenter.default.addObserver(self, selector: #selector(self.kinshipNameChange(_:)), name: .nameChangeNotification, object: nil)
    }
    
    @objc func kinshipNameChange(_ notification: NSNotification) {
        if let name = notification.userInfo?["name"] as? String {
            self.groupNameLabel.text = name
        }
    }
    
//    MARK: - IB-ACTION METHOD
    @IBAction func onNameChange(_ sender: Any) {
        let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "KinshipNameChangeScreen") as! KinshipNameChangeScreen
        vc.groupId = self.groupId
        vc.modalPresentationStyle = .overFullScreen
        self.present(vc, animated: true, completion: nil)
    }
}
//MARK: - SIDEMENU EXTENSION
extension MenubarScreen: LGSideMenuDelegate{
    func didTransformRootView(sideMenuController: LGSideMenuController, percentage: CGFloat) {
        print(#function)
    }
    
    func didTransformLeftView(sideMenuController: LGSideMenuController, percentage: CGFloat) {
        print(#function)
    }
    
    func didTransformRightView(sideMenuController: LGSideMenuController, percentage: CGFloat) {
        print(#function)
    }
    
    func willShowRightView(sideMenuController: LGSideMenuController) {
        self.groupNameLabel.text = UserDefaults.standard.value(forKey: "GROUP_NAME") as? String ?? ""
        self.menubaarTableView.reloadData()
        if sideMenuController.sideMenuType == 2{
            self.getAPIName(groupId: sideMenuController.groupId)
        } else {
            print("From Home chat screen")
            self.menuItemList = [funcanalityName(id: 1, logoImage: "ic_people", name: "\(GROUP_MEMBER_COUNT ?? 0) Members"),
                                  funcanalityName(id: 2, logoImage: "ic_gallery", name: "Gallery"),
                                  funcanalityName(id: 3, logoImage: "ic_link-circle", name: " Links"),
                                  funcanalityName(id: 4, logoImage: "ic_search", name: "Search Messages"),
                                  funcanalityName(id: 5, logoImage: "ic_heart_red", name: "Liked Messages")]
            self.menubaarTableView.reloadData()
        }
    }
}

//MARK: - TABLEVIEW DELEGATE
extension MenubarScreen: UITableViewDataSource,  UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        var count: Int?
        if sideMenuController?.sideMenuType == 1{
            self.editButton.isHidden = false
            count = self.menuItemList.count
            self.nameChangeButton.isHidden = false
        }else if sideMenuController?.sideMenuType == 2{
            self.editButton.isHidden = true
            self.groupNameLabel.text = "Members [\(self.memberList?.count ?? 0)]"
            self.nameChangeButton.isHidden = true
            count = self.memberList?.count
        }
        return count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if sideMenuController?.sideMenuType == 1{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GroupMenubaarCell", for: indexPath) as! GroupMenubaarCell
            cell.menuData = self.menuItemList[indexPath.row]
            return cell
        }else{
            let cell = tableView.dequeueReusableCell(withIdentifier: "MemberCell", for: indexPath) as! MemberCell
            cell.subgroupMemberDetails = self.memberList?[indexPath.row]
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if sideMenuController?.sideMenuType == 1{
            if self.menuItemList[indexPath.row].id == 1{
                self.sideMenuController?.hideRightView(animated: true,completion: { [weak self] in
                    let vc = STORYBOARD.members.instantiateViewController(withIdentifier: "MembersScreen") as! MembersScreen
                    ChatViewController?.navigationController?.pushViewController(vc, animated: true)
                })
            }else if self.menuItemList[indexPath.row].id == 2{
                self.sideMenuController?.hideRightView(animated: true,completion: { [weak self] in
                    let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "GalleryScreen") as! GalleryScreen
                    ChatViewController?.navigationController?.pushViewController(vc, animated: true)
                })
            }else if self.menuItemList[indexPath.row].id == 3{
                self.sideMenuController?.hideRightView(animated: true,completion: { [weak self] in
                    let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "LinksScreen") as! LinksScreen
                    ChatViewController?.navigationController?.pushViewController(vc, animated: true)
                })
            }else if self.menuItemList[indexPath.row].id == 4{
                self.sideMenuController?.hideRightView(animated: true,completion: { [weak self] in
                    let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "SearchMessageScreen") as! SearchMessageScreen
                    ChatViewController?.navigationController?.pushViewController(vc, animated: true)
                })
            }else if self.menuItemList[indexPath.row].id == 5{
                self.sideMenuController?.hideRightView(animated: true,completion: { [weak self] in
                    let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "LikeScreen") as! LikeScreen
                    ChatViewController?.navigationController?.pushViewController(vc, animated: true)
                })
            }
        }
    }
}



//MARK: - API CALL
extension MenubarScreen {
//    func groupAPI() {
//        if Utility.isInternetAvailable() {
//            Utility.showIndicator(view: view)
//            
//            HomePageServices.shared.getMyGroupApi { [weak self] (statusCode, response) in
//                Utility.hideIndicator()
//                if let data = response?.homePageResponse {
//                    self?.groupId = data.groupId
//                    self?.menuItemList.insert(funcanalityName(id: 1, logoImage: "ic_people", name: "\(data.count ?? 0) Members"), at: 0)
//                    self?.menubaarTableView.reloadData()
//                }
//            } failure: { (error) in
//                print(error)
//            }
//        }
//        else {
//            Utility.hideIndicator()
//            Utility.showNoInternetConnectionAlertDialog(vc: self)
//        }
//    }
    
    func getAPIName(groupId: String?){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = SubgroupMemberRequest(groupId: groupId)
//            print(param.toJSON())
            DirectMessageServices.shared.subgroupMemberList(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response?.subGroupMembersResponse{
                    self?.memberList = data
                    self?.menubaarTableView.reloadData()
                }
            } failure: { [weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        } else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
