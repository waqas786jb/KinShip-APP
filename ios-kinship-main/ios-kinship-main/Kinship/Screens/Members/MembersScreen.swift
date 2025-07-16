//
//  MembersScreen.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import UIKit

class MembersScreen: UIViewController {

    @IBOutlet weak var membersTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "MemberCell", bundle: nil)
            membersTableView.register(nib, forCellReuseIdentifier: "MemberCell")
        }
    }
    @IBOutlet weak var memberCountLabel: UILabel!
    
//    MARK: - VARIABLES
    var memberData: HomePageResponse?
    var groupMembersData = [GroupMembers]()
    var chatGroupId: String?

    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
    }
//    MARK: - FUNCTION
    func initilize(){
        self.membersTableView.showsHorizontalScrollIndicator = false
        self.membersTableView.showsVerticalScrollIndicator = false
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.groupAPI()
    }
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
//        self.groupAPI()
    }
    
//    MARK: - FUNCTIONS
    func setUpData(){
        self.groupMembersData = (self.memberData?.groupMembers!)!
        self.memberCountLabel.text = "Members \(memberData?.count ?? 0)"
//        Utility.setImage(self.memberData?.image, imageView: )
        self.membersTableView.reloadData()
    }
}

//MARK: - TABLEVIEW DELEGATE, DATA SOURSE
extension MembersScreen: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return memberData?.count ?? 0
    }
    		
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let groupData = self.groupMembersData[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "MemberCell") as! MemberCell
        cell.groupDetails = groupData
        cell.message = { value in
            let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ChatScreen") as! ChatScreen
            vc.isFromDirectChat = true
            vc.fromMemberScreen = true
            if self.groupMembersData[indexPath.row].chatGroupId == nil{
                var userId = [String]()
                userId.append(self.groupMembersData[indexPath.row].userId ?? "")
                self.createSubgroupAPI(userId: userId)
                DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) { [weak self] in
                    vc.chatGroupId = self?.chatGroupId
                    vc.receiverId = self?.groupMembersData[indexPath.row].userId
                    vc.profileImage = self?.groupMembersData[indexPath.row].profileImage
                    vc.userName = "\(self?.groupMembersData[indexPath.row].firstName ?? "") \(self?.groupMembersData[indexPath.row].lastName ?? "")"
                    self?.navigationController?.pushViewController(vc, animated: true)
                }
            }else{
                vc.chatGroupId = self.groupMembersData[indexPath.row].chatGroupId
                vc.receiverId = self.groupMembersData[indexPath.row].userId
                vc.profileImage = self.groupMembersData[indexPath.row].profileImage
                vc.userName = "\(self.groupMembersData[indexPath.row].firstName ?? "") \(self.groupMembersData[indexPath.row].lastName ?? "")"
                self.navigationController?.pushViewController(vc, animated: true)
            }

        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let groupData = self.groupMembersData[indexPath.row]
        let vc = STORYBOARD.members.instantiateViewController(withIdentifier: "MembersProfile") as! MembersProfile
        vc.profileData = groupData
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

//MARK: - API CALL
extension MembersScreen {
    func groupAPI() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            HomePageServices.shared.getMyGroupApi { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.homePageResponse {
                    self?.memberData = data
                    self?.setUpData()
                    
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
   
    func createSubgroupAPI(userId: [String]?){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = CreateSubgroupResponse(userId: userId ?? [])
//            print(param.toJSON())
            DirectMessageServices.shared.subgroupCreate(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.subGroupCreateResponse {
                    self?.chatGroupId = data.Id
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
