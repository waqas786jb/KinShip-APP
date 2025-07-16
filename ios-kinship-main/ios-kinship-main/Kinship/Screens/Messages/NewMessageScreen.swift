//
//  NewMessageScreen.swift
//  Kinship
//
//  Created by iMac on 04/06/24.
//

import UIKit

class NewMessageScreen: UIViewController {

//    MARK: - IB-OUTLET
    @IBOutlet weak var addedMembersCollectionView: UICollectionView!{
        didSet{
            addedMembersCollectionView.register(UINib(nibName: "AddMemberCell", bundle: nil), forCellWithReuseIdentifier: "AddMemberCell")
        }
    }
    @IBOutlet weak var membersTableView: UITableView!{
        didSet{
            self.membersTableView.register(UINib(nibName: "newMwmberCell", bundle: nil), forCellReuseIdentifier: "newMwmberCell")
        }
    }
    
//    MARK: - VARIABLES
    var memberList: [GroupMembers]? = []
    var homeData:HomePageResponse?
//    var selectedMembers: [String]?
    var selectedMembers: [GroupMembers]?
    var heightLabel: UILabel?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialDetail()
    }
    
    func initialDetail(){
        self.membersTableView.delegate = self
        self.memberListAPI()
        self.membersTableView.showsHorizontalScrollIndicator = false
        self.membersTableView.showsVerticalScrollIndicator = false
    }
    
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onContinue(_ sender: Any) {
        let data = self.memberList?.filter({$0.isSelected == true}).compactMap({$0.userId})
        if data?.count ?? 0 > 0{
            self.createSubgroupAPI()
        }else{
            Utility.showAlert(message: "Please select Member")
        }
    }
    
}
//MARK: - FUNCTION'S
extension NewMessageScreen {
    func personOrSubgroup(){
        self.navigationController?.popViewController(animated: true)
        
        if self.memberList?.filter({$0.isSelected == true}).compactMap({$0.userId}).count ?? 0 == 1{
            Utility.successAlert(message: "Member add successfully!")
        }else{
            Utility.successAlert(message: "Group Created successfully!")
        }
    }
}

//MARK: - TABLE VIEW DELEGATE
extension NewMessageScreen: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.memberList?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "newMwmberCell") as! newMwmberCell
        cell.addMembersData = self.memberList?[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let obj = self.memberList?[indexPath.row] else { return }
        obj.isSelected = obj.isSelected == true ? false : true
        self.memberList?[indexPath.row] = obj
        self.selectedMembers = self.memberList?.filter({$0.isSelected == true})
        self.membersTableView.reloadRows(at: [IndexPath(row: indexPath.row, section: 0)], with: .automatic)
        self.addedMembersCollectionView.reloadData()
    }
}

//MARK: - COLLECTION VIEW DELEGATE
extension NewMessageScreen: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        self.selectedMembers?.count ?? 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "AddMemberCell", for: indexPath) as! AddMemberCell
        cell.selectedMemberData = self.selectedMembers?[indexPath.row]
        return cell
    }
}
extension NewMessageScreen: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let fullName = "\(self.selectedMembers?[indexPath.row].firstName ?? "") \(self.selectedMembers?[indexPath.row].lastName ?? "")"
        let labelWidth = Utility.labelWidth(height: 48, font: UIFont(name: "Helvetica Neue", size:  8) ?? UIFont(), text: fullName)
        return CGSize(width: labelWidth + 28, height: 48)
    }
}
//MARK: - API CALL
extension NewMessageScreen {
    func memberListAPI() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            HomePageServices.shared.getMyGroupApi { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.homePageResponse {
                    self?.memberList = data.groupMembers
                    self?.memberList?.removeAll(where: ({$0.userId == Utility.getUserData()?.profile?.userId}))
                    self?.membersTableView.reloadData()
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
    
    func createSubgroupAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = CreateSubgroupResponse(userId: self.memberList?.filter({$0.isSelected == true}).compactMap({$0.userId}) ?? [])
//            print(param.toJSON())
            DirectMessageServices.shared.subgroupCreate(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.subGroupCreateResponse {
//                    print(data.toJSON())
                }
                self?.personOrSubgroup()
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
