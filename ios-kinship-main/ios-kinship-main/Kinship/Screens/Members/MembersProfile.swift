//
//  MembersProfile.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import UIKit

class MembersProfile: UIViewController {

//   MARK: - OUTLETS
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameTextLabel: UILabel!
    @IBOutlet weak var cityTextLabel: UILabel!
    @IBOutlet weak var childBirthdateTextLabel: UILabel!
    @IBOutlet weak var bioTextView: UITextView!
    @IBOutlet weak var hearedNameLabel: UILabel!
    @IBOutlet weak var messageButton: UIButton!
    
//    MARK: - VARIABLES
    var profileData: GroupMembers?
    var chatGroupId: String?
    
//    MARK: - VARIABLES
    override func viewDidLoad() {
        super.viewDidLoad()
        manageUI()
        initialize()
    }
    func initialize(){
        self.nameTextLabel.text = "\(profileData?.firstName ?? "") \(profileData?.lastName ?? "")"
        self.hearedNameLabel.text = "\(profileData?.firstName ?? "") \(profileData?.lastName ?? "")"
        self.cityTextLabel.text = "\(profileData?.city ?? "-")"
        self.childBirthdateTextLabel.text = Utility.setTimeToDateFormate(timestamp: "\(profileData?.dateOfBirth ?? 0)", dateFormate: "MMM d, YYYY")
        self.bioTextView.text = "\(profileData?.bio ?? "")"
        Utility.setImage(profileData?.profileImage, imageView: self.profileImageView)
    }
    
//    MARK: - FUNCTIONS
    func manageUI(){
        profileImageView.layer.cornerRadius = 50
        bioTextView.layer.borderWidth = 1
        bioTextView.layer.borderColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.2)
        bioTextView.layer.cornerRadius = 10
        if profileData?.userId == Utility.getUserData()?.profile?.userId {
            self.messageButton.isHidden = true
        }else{
            self.messageButton.isHidden = false
        }
    }
    
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onMessage(_ sender: Any) {
        let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "ChatScreen") as! ChatScreen
        vc.isFromDirectChat = true
        vc.fromMemberScreen = true
        if self.profileData?.chatGroupId == nil{
            var userId = [String]()
            userId.append(self.profileData?.userId ?? "")
            self.createSubgroupAPI(userId: userId)
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) { [weak self] in
                vc.chatGroupId = self?.chatGroupId
                vc.receiverId = self?.profileData?.userId
                vc.profileImage = self?.profileData?.profileImage
                vc.userName = "\(self?.profileData?.firstName ?? "") \(self?.profileData?.lastName ?? "")"
                self?.navigationController?.pushViewController(vc, animated: true)
            }
        }else{
            vc.chatGroupId = self.profileData?.chatGroupId
            vc.receiverId = self.profileData?.userId
            vc.profileImage = self.profileData?.profileImage
            vc.userName = "\(self.profileData?.firstName ?? "") \(self.profileData?.lastName ?? "")"
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}

extension MembersProfile {
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
