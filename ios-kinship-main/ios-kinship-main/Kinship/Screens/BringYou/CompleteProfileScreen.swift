//
//  CompleteProfileScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit
import TagListView
import CountryPickerView
import MapKit
import Alamofire

class CompleteProfileScreen: UIViewController {
    
    //MARK: - OUTLETS
    @IBOutlet weak var obsessedTagListView: TagListView!
    @IBOutlet weak var countryNameLabel: UILabel!
    @IBOutlet weak var countryName: UIButton!
    @IBOutlet weak var firstNameTextField: UITextField!
    @IBOutlet weak var lastNameTextField: UITextField!
    @IBOutlet weak var phoneNumberTextField: UITextField!
    @IBOutlet weak var dateOfBirthTextField: UITextField!
    @IBOutlet weak var zipCodeTextField: UITextField!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var cityTextField: UITextField!
    
    //MARK: - VARIABLES
    let countryPickerView = CountryPickerView()
    var datePicker = UIDatePicker()
    let toolbar = UIToolbar()
    var birthDate : Date?
    var profileData: ProfileRequest?
    var latitude: String = ""
    var longitude: String = ""
    var tagListArray : [HobbiesResponse] = []
    var hobbiesArray: [String] = []
    var nameTagListArray: [String] = []
    var idArray: [String] = []
    var isFromRoot:Bool? = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.getHobbies()
        self.inilization()
    }
    
//    MARK: - FUNCTION
    func inilization(){
//        For date picker
        self.setupDatePicker()
        
        countryPickerView.delegate = self
        if let countryCode = (Locale.current as NSLocale).object(forKey: .countryCode) as? String {
        let countryName = countryPickerView.getCountryByCode(countryCode)
        countryPickerView.setCountryByName(countryName?.name ?? "")
        }
        
        self.backButton.isHidden = isFromRoot ?? false
//      for profile Data values
        self.profileData = ProfileRequest(step: 2, kinshipReason: nil, howLongYouAreTrying: nil, howYouTrying: nil, whenIsYourDueDate: nil, singleOrMultiplePregnancy: nil, singleGender: nil, multipleGender: nil, singleOrMultipleBirth: nil, babyBornDate: nil, childHasSpecialNeed: nil, firstTimeMom: nil, firstName: nil, lastName: nil, dateOfBirth: nil, countrycode: nil, phoneNumber: nil, zipcode: nil, lat: nil, long: nil, hobbies: nil)
        
//        for TegList View
        self.obsessedTagListView.delegate = self
        self.firstNameTextField.delegate = self
        self.lastNameTextField.delegate = self
    }

//    MARK: - TAGLISTVIEW
    func configeObsessedTagListVie() {
        self.obsessedTagListView.removeAllTags()
        self.obsessedTagListView.textFont = UIFont(name: "OpenSans-Regular", size: 14)!
        self.obsessedTagListView.addTags(self.nameTagListArray)
        self.obsessedTagListView.tagLineBreakMode = .byTruncatingTail
        self.obsessedTagListView.alignment = .leading
    }
    
//    MARK: - IB-ACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onCountryCode(_ sender: UIButton) {
        
        self.view.endEditing(true)
        countryPickerView.showCountriesList(from: self)
    }
    
    @IBAction func onContinue(_ sender: Any) {

        self.idArray.removeAll()
        self.idArray = tagListArray.filter { tag in hobbiesArray.contains(tag.name ?? "") }.compactMap { $0.Id }
       
        if firstNameTextField.text == "" {
            Utility.showAlert(message: "Please enter first name")
        }else if lastNameTextField.text == ""{
            Utility.showAlert(message: "Please enter last name")
        }else if phoneNumberTextField.text == "" {
            Utility.showAlert(message: "Please enter Phone number")
        }else if !Utility.isValidPhoneNumber(phone: self.phoneNumberTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) ?? "") {
            Utility.showAlert(message: "Please enter Valid Phone number")
        }else if dateOfBirthTextField.text == ""{
            Utility.showAlert(message: "Please enter Date of birth")
        }else if zipCodeTextField.text == "" {
            Utility.showAlert(message: "Please enter zipcode")
        }else if self.cityTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 {
            Utility.showAlert(message: "Please enter City")
        }else if zipCodeTextField.text?.count == 0 || zipCodeTextField.text?.count ?? 0 > 6 || zipCodeTextField.text?.count ?? 0 < 4{
            Utility.showAlert(message: "Zip code is must 4 to 6 character")
        }else if self.idArray.count < 3 || self.idArray.count > 5 {
            Utility.showAlert(message: "You must select hobbies between 3 to 5")
        }else {
            getLocation(pinCode: zipCodeTextField.text ?? "")
        }
    }
}

// MARK: - EXTENSION

extension CompleteProfileScreen : CountryPickerViewDelegate {
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        countryNameLabel.text = country.phoneCode
        
    }
}

extension CompleteProfileScreen: UITextFieldDelegate {
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            
//            if string.containsEmoji {
//                return false
//            }
            
            if textField == firstNameTextField || textField == lastNameTextField {
                let allowedCharacters = CharacterSet.letters.union(CharacterSet.whitespaces)
                let characterSet = CharacterSet(charactersIn: string)
                
                return allowedCharacters.isSuperset(of: characterSet)
            }
            
            return true
        }
}

extension CompleteProfileScreen: TagListViewDelegate {
    
    @objc func tagPressed(_ title: String, tagView: TagView, sender: TagListView) -> Void {
        
        if tagView.isSelected == false {
            
            if self.hobbiesArray.count < 5 {
                hobbiesArray.append(title)
                tagView.isSelected = true
                tagView.selectedTextColor = UIColor.black
            }
            else {
                Utility.showAlert(message: "You must select Hobbies Between 3 to 5")
            }

        }else if tagView.isSelected == true {
            
            tagView.isSelected = false
            self.hobbiesArray = hobbiesArray.filter { $0 != title }
        }
    }
}

//MARK: - EXTENSION

extension CompleteProfileScreen{
    
    //MARK: - DATE PICKER
    func setupDatePicker() {
        self.datePicker.datePickerMode = .date
        self.datePicker.locale = Locale(identifier: "en_US")
        if #available(iOS 13.4, *) {
            self.datePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        self.datePicker.maximumDate =  Date()
//        self.datePicker.minimumDate = Calendar.current.date(byAdding: .year, value: -100, to: Date())
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(doneButtonTapped))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        self.toolbar.items = [cancelButton, flexSpace, doneButton]
        self.toolbar.sizeToFit()
        self.dateOfBirthTextField.inputAccessoryView = self.toolbar
        self.dateOfBirthTextField.inputView = self.datePicker
        
    }
    
    @objc func doneButtonTapped() {
        self.birthDate = self.datePicker.date
        let formatedDate =  dateToString(Formatter: MM_DD_YYYY, date: self.datePicker.date)
        self.dateOfBirthTextField.text = "\(formatedDate)"
        self.view.endEditing(true)
     }
    
    @objc func cancelButtonTapped() {
        self.view.endEditing(true) // Dismiss the DatePicker
    }
}

extension CompleteProfileScreen {
//MARK: - FUNCTION'S
    func getLocation(pinCode:String){
        let searchRequest = MKLocalSearch.Request()
        searchRequest.naturalLanguageQuery = pinCode
        let search = MKLocalSearch(request: searchRequest)
        search.start { response, error in
            guard let response = response else {
                Utility.showAlert(message: "Please enter valid zip code")
                return
            }
            
            if let coordinate = response.mapItems.first?.placemark.coordinate{
                self.latitude = String(coordinate.latitude)
                self.longitude = String(coordinate.longitude)
                self.addProfile()
            } else {
                Utility.showAlert(message: "Please enter valid zip code")
            }
//          print(response.mapItems.first?.placemark.title ?? "No Placemarks")
        }
    }
}

//    MARK: - API CALL

extension CompleteProfileScreen {
    
//    COMPLATE PROFILE
    func addProfile() {
        
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let myTimeStamp = Utility.getTimeStampInMillisecond(date:  self.datePicker.date )
//            print(myTimeStamp)
            let param = ProfileRequest(step: profileData?.step,
                                       firstName: self.firstNameTextField.text,
                                       lastName: self.lastNameTextField.text,
                                       dateOfBirth: myTimeStamp,
                                       countrycode: self.countryNameLabel.text,
                                       phoneNumber: self.phoneNumberTextField.text,
                                       zipcode: Int(zipCodeTextField.text ?? ""),
                                       lat: latitude,
                                       long: longitude,
                                       city: self.cityTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
                                       hobbies: idArray)
//            print(param.toJSON())
            BringYouServices.shared.tryingToConceive (parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                Utility.hideIndicator()
                if let data = response.logInResponse {
                    let getUserData = Utility.getUserData()
                    data.auth = getUserData?.auth
                    Utility.saveUserData(data: data.toJSON())
//                    Utility.showAlert(vc: self, message: "Data Save successfully!")
                    Utility.setTabBarRoot()
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
    
    //    MARK: - HOBBIES API
    func getHobbies() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            BringYouServices.shared.hobbiesApi { [weak self] (statusCode, response) in
                if let data = response?.hobbiesResponse {
                    self?.tagListArray = data
                    self?.nameTagListArray = data.map({$0.name ?? ""})
                    self?.configeObsessedTagListVie()
                    Utility.hideIndicator()
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
