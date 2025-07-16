//
//  AboutYourFertilityScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import UIKit

class AboutYourFertility1Screen: UIViewController {

    //MARK: - OUTLETS
    @IBOutlet weak var bringYouTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "BringYouCell", bundle: nil)
            bringYouTableView.register(nib, forCellReuseIdentifier: "BringYouCell")
        }
    }
    
    //MARK: - VARIABLES
    let bringYouArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "Less than a year"),
       BringYouModel(id: 2, title: "Between 1 and 2 years"),
       BringYouModel(id: 3, title: "More than 2 years")
    ]
    var selectedId: Int?
    var profileDataFertility1: ProfileRequest?

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    //MARK: - IBACTIONS
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onContinue(_ sender: UIButton) {
        
        if selectedId == nil{
            Utility.showAlert(message: "You must have select any of one option")
        }else {
            let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "AboutYourFertility2Screen") as! AboutYourFertility2Screen
            vc.profileDataFertility2 = self.profileDataFertility1
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }

}

//MARK: - EXTENSION

//MARK: - TABLEVIEW DELEGATE, DATA SOURSE
extension AboutYourFertility1Screen : UITableViewDelegate, UITableViewDataSource{
    
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
        profileDataFertility1?.howLongYouAreTrying = selectedId       // set a value in model
        self.bringYouTableView.reloadData()
    }
    
}
