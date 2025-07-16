//
//  AboutYourFertility2Screen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit

class AboutYourFertility2Screen: UIViewController {

    //MARK: - OUTLETS
    @IBOutlet weak var bringYouTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "BringYouCell", bundle: nil)
            bringYouTableView.register(nib, forCellReuseIdentifier: "BringYouCell")
        }
    }
    
    //MARK: - VARIABLES
    let bringYouArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "The old-fashioned way"),
       BringYouModel(id: 2, title: "Intra-uterina insemination (IUI)"),
       BringYouModel(id: 3, title: "In-vitro fertizilation (IVF)")
    ]
    var selectedId: Int?
    var profileDataFertility2: ProfileRequest?

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    //MARK: - IBACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onContinue(_ sender: UIButton) {
        if selectedId == nil{
            Utility.showAlert(message: "You must have select any of one option")
        }else {
            addProfile()
        }
    }

}
//MARK: - EXIENSION'S

//MARK: - TABLEVIEW DELEGATE, DATA SOURSE
extension AboutYourFertility2Screen : UITableViewDelegate, UITableViewDataSource{
    
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
        profileDataFertility2?.howYouTrying = selectedId
        self.bringYouTableView.reloadData()
    }
    
}

//    MARK: - API CALL
extension AboutYourFertility2Screen {
    
    func addProfile() {
        
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ProfileRequest(step: profileDataFertility2?.step,
                                       kinshipReason: profileDataFertility2?.kinshipReason,
                                       howLongYouAreTrying: profileDataFertility2?.howLongYouAreTrying,
                                       howYouTrying: profileDataFertility2?.howYouTrying)
//            print(param.toJSON())
            BringYouServices.shared.tryingToConceive(parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.logInResponse {
                    let getUserData = Utility.getUserData()
                    data.auth = getUserData?.auth
                    Utility.saveUserData(data: data.toJSON())
//                    print("About Your Fertility 2 Screen", data.toJSON())
                    let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "CompleteProfileScreen") as! CompleteProfileScreen
                    self?.navigationController?.pushViewController(vc, animated: true)
                }
            } failure: { [weak self] (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
