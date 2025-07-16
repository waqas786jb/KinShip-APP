//
//  BringYouScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import UIKit

class BringYouScreen: UIViewController {
    
    //MARK: - OUTLETS
    @IBOutlet weak var bringYouTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "BringYouCell", bundle: nil)
            bringYouTableView.register(nib, forCellReuseIdentifier: "BringYouCell")
        }
    }
    
    //MARK: - VARAIBLES
    let bringYouArray: [BringYouModel] = [
        BringYouModel(id: 1, title: "I’m trying to conceive"),
        BringYouModel(id: 2, title: "I’m pregnant"),
        BringYouModel(id: 3, title: "I had a baby"),
        BringYouModel(id: 4, title: "I adopted")
    ]
    var selectedId: Int?
    var profileData: ProfileRequest?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initialize()
    }
//    MARK: - FUNCTIONS
    func initialize(){
        profileData = ProfileRequest(step: 1, kinshipReason: nil, howLongYouAreTrying: nil, howYouTrying: nil, whenIsYourDueDate: nil, singleOrMultiplePregnancy: nil, singleGender: nil, multipleGender: nil, singleOrMultipleBirth: nil, babyBornDate: nil, childHasSpecialNeed: nil, firstTimeMom: nil, firstName: nil, lastName: nil, dateOfBirth: nil, countrycode: nil, phoneNumber: nil, zipcode: nil, lat: nil, long: nil, hobbies: nil)
    }
    
//    MARK: - IBACTIONS
    @IBAction func onContinue(_ sender: UIButton) {
        
        if selectedId == nil{
            Utility.showAlert(message: "You must have select any of one option")
        }else{
            self.setNavigation()
        }
    }
    
//    MARK: - FUNCTIONS
    func setNavigation(){
        switch self.selectedId{
        case 1:
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "AboutYourFertility1Screen") as! AboutYourFertility1Screen
            vc.profileDataFertility1 = self.profileData
            self.navigationController?.pushViewController(vc, animated: true)
            break
        case 2:
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "AboutYourPregnancyScreen") as! AboutYourPregnancyScreen
            vc.profileDataAboutPregnancy = self.profileData
            self.navigationController?.pushViewController(vc, animated: true)
            break
        case 3:
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "AboutYourBabyScreen") as! AboutYourBabyScreen
            vc.profileDataAboutBaby = self.profileData
            vc.isFromAdoptedScreen = false
            self.navigationController?.pushViewController(vc, animated: true)
            break
        case 4:
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "AboutYourBabyScreen") as! AboutYourBabyScreen
            vc.isFromAdoptedScreen = true
            vc.profileDataAboutBaby = self.profileData
            self.navigationController?.pushViewController(vc, animated: true)
            break
        default:
            break
        }
    }
    
}
// MARK: - EXTENSION

//MARK: - TABLEVIEW DELEGTE, DATA SOURSE
extension BringYouScreen : UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return bringYouArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BringYouCell", for: indexPath) as! BringYouCell
        cell.bringYouModel = bringYouArray[indexPath.row]
        cell.containerView.backgroundColor = self.selectedId ==  bringYouArray[indexPath.row].id ? #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.2) : .clear
        cell.tickView.isHidden = self.selectedId != bringYouArray[indexPath.row].id
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.selectedId = self.bringYouArray[indexPath.row].id
        profileData?.kinshipReason = selectedId
        self.bringYouTableView.reloadData()
    }
    
}
