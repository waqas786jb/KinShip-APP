//
//  AboutYourBabyScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 03/03/24.
//

import UIKit
import RESegmentedControl

class AboutYourBabyScreen: UIViewController {
    
    //MARK: - OUTLETS
    @IBOutlet weak var segmentedControl: RESegmentedControl!{
        didSet {
            segmentedControl.configure(segmentItems: segmentItemArray.map({ SegmentModel(title: $0.title, imageName: $0.image) }), preset: customSimple3preset)
        }
    }
    
    @IBOutlet weak var firstTimeMomSegmentedControl: RESegmentedControl!{
        didSet {
            firstTimeMomSegmentedControl.configure(segmentItems: firstTimeMomArray.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    
    @IBOutlet weak var specialNeedSegmentedControl: RESegmentedControl!{
        didSet {
            specialNeedSegmentedControl.configure(segmentItems: specialNeedArray.map({ SegmentModel(title: $0.title) }), preset: customSimple3preset)
        }
    }
    
    @IBOutlet weak var havingChildTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "BringYouCell", bundle: nil)
            havingChildTableView.register(nib, forCellReuseIdentifier: "BringYouCell")
        }
    }
    @IBOutlet weak var havingChildTableViewHeight: NSLayoutConstraint!
    @IBOutlet weak var birthDateTextField: UITextField!
    @IBOutlet weak var multipleBabyDateTextField: UITextField!
    @IBOutlet weak var birthView: UIView!
    @IBOutlet weak var genderView: UIView!
    @IBOutlet weak var firstTimeMomView: UIView!
    @IBOutlet weak var multipleBabyDateView: UIView!
    @IBOutlet weak var childBirthDateLabel: UILabel!
    @IBOutlet weak var childSexLabel: UILabel!
    @IBOutlet weak var tellusAboutBabyLabel: UILabel!
    @IBOutlet weak var childNeedLabel: UILabel!
    @IBOutlet weak var birthLabel: UILabel!
    
    //MARK: - VARIABLES
    var profileDataAboutBaby: ProfileRequest?
    var isFromAdoptedScreen : Bool = false
    var havingChildArray: [BringYouModel] = [
    ]
    
    var havingSingleChildArray: [BringYouModel] = [
        BringYouModel(id: 1, title: "Girl"),
        BringYouModel(id: 2, title: "Boy")
        // BringYouModel(id: 3, title: "It’s a surprise")
    ]
    
    var havingMultipleChildArray : [BringYouModel] = [
        BringYouModel(id: 1, title: "Girls"),
        BringYouModel(id: 2, title: "Boys"),
        BringYouModel(id: 3, title: "Both")
    ]
    
    let firstTimeMomArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "No"),
       BringYouModel(id: 2, title: "Yes"),
    ]
    
    let specialNeedArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "No"),
       BringYouModel(id: 2, title: "Yes"),
    ]

    var selectedId: Int?
    
    let segmentItemArray: [SegmentItemModel] = [
        SegmentItemModel(id: 1, title: "Single", image: "ic_user_black"),
        SegmentItemModel(id: 2, title: "Multiple", image: "ic_user3"),
    ]
    
    var datePicker = UIDatePicker()
    var secondDatePicker = UIDatePicker()
    
    var birthDate : Date?
    var childBirthDateText : String?
    var childSexText : String?
    
    var babyBornDate = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.firstTimeMomSegmentedControl.selectedSegmentIndex = 1
        if isFromAdoptedScreen ?? false{
            
            self.birthView.isHidden = true
            self.firstTimeMomView.isHidden = true
//            self.childBirthDateLabel.text = self.childBirthDateText
//            self.childSexLabel.text = self.childSexText
//            self.segmentedControl.isHidden = true
        }

        self.setupDatePicker()
        self.setupMultipleChildDatePicker()
    }
    
    lazy var customSimple3preset: SegmentedControlPresettable = {

        var selectedBackgroundColor: UIColor = #colorLiteral(red: 0.8666666667, green: 0.8666666667, blue: 0.8666666667, alpha: 1)

        if #available(iOS 11.0, *) {
            selectedBackgroundColor = #colorLiteral(red: 0.8666666667, green: 0.8666666667, blue: 0.8666666667, alpha: 1)
        }

        var preset = BootstapPreset(backgroundColor: .clear, selectedBackgroundColor: selectedBackgroundColor, textColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1), selectedTextColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1))
        
        preset.segmentCornerRadius = 20
        preset.segmentBorderWidth = 2
        preset.segmentBorderColor = #colorLiteral(red: 0.8666666667, green: 0.8666666667, blue: 0.8666666667, alpha: 1)
        preset.selectedSegmentItemCornerRadius = 20

        preset.textFont = UIFont(name: "OpenSans-Regular", size: 12) ??  UIFont.systemFont(ofSize: 12, weight: .bold)
        preset.selectedTextFont = UIFont(name: "OpenSans-Regular", size: 12)
        return preset
    }()
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        self.setTableViewHeight()
        
    }
    
//    MARK: - FUNCTIONS
    func setTableViewHeight(){
        var tableViewHeight: CGFloat = 0.0
        
        for i in 0..<self.havingChildTableView.numberOfRows(inSection: 0) {
            tableViewHeight += self.havingChildTableView.rectForRow(at: IndexPath(row: i, section: 0)).height
        }
        self.havingChildTableViewHeight.constant = tableViewHeight
        
        self.view.layoutIfNeeded()
    }
    
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onBirthSegment(_ sender: RESegmentedControl) {
//
            self.havingChildArray.removeAll()
            
            if self.segmentedControl.selectedSegmentIndex == 0 {
                
                self.havingChildArray.append(contentsOf: self.havingSingleChildArray)
                self.multipleBabyDateView.isHidden = true
                if isFromAdoptedScreen == false{
//                    self.birthLabel.text = "Did you have a single or multiple birth?"
                    self.childBirthDateLabel.text = "When was your baby born?"
                    self.childNeedLabel.text = "Does your child have special needs?"
                    self.childSexLabel.text = "What is your baby’s sex?"
                }else{
//                    self.birthLabel.text = "What is your child’s date of birth?"
                    self.childBirthDateLabel.text = "What is your child’s date of birth?"
                    self.childNeedLabel.text = "Does your child have special needs?"
                    self.childSexLabel.text = "Did you adopt a boy or girl?"
                }
             
                self.tellusAboutBabyLabel.text = "Tell us about your baby"
                
                
                self.profileDataAboutBaby?.singleOrMultipleBirth = 1
                self.havingChildTableView.reloadData()
                
            }else if self.segmentedControl.selectedSegmentIndex == 1{
                
                self.havingChildArray.append(contentsOf: self.havingMultipleChildArray)
                self.multipleBabyDateView.isHidden = false
                self.childBirthDateLabel.text = "When were your babies born?"
                self.childSexLabel.text = "Did you have boys or girls (or both)?"
                self.tellusAboutBabyLabel.text = "Tell us about your baby(s)"
                self.childNeedLabel.text = "Do any of your children have special needs?"
                
                self.profileDataAboutBaby?.singleOrMultipleBirth = 2
                self.havingChildTableView.reloadData()
            }
        self.havingChildTableView.reloadData()
        self.setTableViewHeight()
    }
    
    @IBAction func OnChildSegment(_ sender: Any) {
        if self.specialNeedSegmentedControl.selectedSegmentIndex == 0{
            self.profileDataAboutBaby?.childHasSpecialNeed = 2
        }else if self.specialNeedSegmentedControl.selectedSegmentIndex == 1{
            self.profileDataAboutBaby?.childHasSpecialNeed = 1
        }
    }
    
    @IBAction func OnFirstTimeMomSegment(_ sender: Any) {
        if self.firstTimeMomSegmentedControl.selectedSegmentIndex == 0{
            
            self.profileDataAboutBaby?.firstTimeMom = 2
            
        }else if self.firstTimeMomSegmentedControl.selectedSegmentIndex == 1{
            
            self.profileDataAboutBaby?.firstTimeMom = 1
            
        }
    }
    
    @IBAction func onContinue(_ sender: UIButton) {
        if self.segmentedControl.selectedSegmentIndex == 0 {
            
            if birthDateTextField.text == "" {
                Utility.showAlert(message: "Please select baby born date")
            }else if selectedId == nil{
                Utility.showAlert(message: "Please Select baby's sex")
            }else {
                self.dateToTimeStemp()
                self.addProfile()
            }
        }else if self.segmentedControl.selectedSegmentIndex == 1 {
            
            if birthDateTextField.text == "" || multipleBabyDateTextField.text == ""{
                Utility.showAlert(message: "Please select babies born date")
            }else if selectedId == nil{
                Utility.showAlert(message: "Please Select babies sex")
            }else {
                dateToTimeStemp()
                addProfile()
            }
        }
    }
    //    MARK: - FUNCTIONS
    func dateToTimeStemp() {
        if segmentedControl.selectedSegmentIndex == 0 {
            
            babyBornDate.append(Utility.getTimeStampInMillisecond(date: self.datePicker.date ))
            
        }else if segmentedControl.selectedSegmentIndex == 1 {
            
            babyBornDate.append(Utility.getTimeStampInMillisecond(date: self.datePicker.date ))
            babyBornDate.append(Utility.getTimeStampInMillisecond(date: self.secondDatePicker.date ))

        }
    }
}

    
//MARK: - EXTENSION

extension AboutYourBabyScreen {
    
//    MARK: - DATE PICKER
    func setupDatePicker() {
        let toolbar = UIToolbar()
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
        toolbar.items = [cancelButton, flexSpace, doneButton]
        toolbar.sizeToFit()
        self.birthDateTextField.inputAccessoryView = toolbar
        self.birthDateTextField.inputView = self.datePicker
        
    }
    @objc func doneButtonTapped() {
        
        self.birthDate = self.datePicker.date
        let formatedDate =  dateToString(Formatter: MM_DD_YYYY, date: self.datePicker.date)
        self.birthDateTextField.text = "\(formatedDate)"
        
        view.endEditing(true)
     }
    @objc func cancelButtonTapped() {
        // Dismiss the DatePicker
        view.endEditing(true)
    }
    
    func setupMultipleChildDatePicker() {
        let toolbar = UIToolbar()
        self.secondDatePicker.datePickerMode = .date
        self.secondDatePicker.locale = Locale(identifier: "en_US")
        if #available(iOS 13.4, *) {
            self.secondDatePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        self.secondDatePicker.maximumDate =  Date()
//        self.secondDatePicker.minimumDate = Calendar.current.date(byAdding: .year, value: -100, to: Date())
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(secondDatePickerDoneClick))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        toolbar.items = [cancelButton, flexSpace, doneButton]
        toolbar.sizeToFit()
        self.multipleBabyDateTextField.inputAccessoryView = toolbar
        self.multipleBabyDateTextField.inputView = self.secondDatePicker
    }
    @objc func secondDatePickerDoneClick() {
        
        self.birthDate = self.secondDatePicker.date
        let formatedDate =  dateToString(Formatter: MM_DD_YYYY, date: self.secondDatePicker.date)
        self.multipleBabyDateTextField.text = "\(formatedDate)"
        
        view.endEditing(true)
     }
}

//MARK: - TABLEVIEW DELEGATE, DATA SOURSE
extension AboutYourBabyScreen : UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return havingChildArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BringYouCell", for: indexPath) as! BringYouCell
        cell.bringYouModel = self.havingChildArray[indexPath.row]
        cell.containerView.backgroundColor = self.selectedId ==  self.havingChildArray[indexPath.row].id ? #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 0.2) : .clear
        cell.tickView.isHidden = self.selectedId != self.havingChildArray[indexPath.row].id
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.selectedId = self.havingChildArray[indexPath.row].id
        if self.segmentedControl.selectedSegmentIndex == 0 {
            
            self.profileDataAboutBaby?.singleGender = selectedId
            
        }else if self.segmentedControl.selectedSegmentIndex == 1 {
            
            self.profileDataAboutBaby?.multipleGender = selectedId
    
        }
        self.havingChildTableView.reloadData()
    }
}

//    MARK: - API CALL
extension AboutYourBabyScreen {
    
    func addProfile() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            var parameter = ProfileRequest()
            
            if isFromAdoptedScreen ?? false {
                profileDataAboutBaby?.singleOrMultipleBirth = 1
                profileDataAboutBaby?.firstTimeMom = nil
                let  param = ProfileRequest(step: profileDataAboutBaby?.step,
                                            kinshipReason: profileDataAboutBaby?.kinshipReason,
                                            singleGender: profileDataAboutBaby?.singleGender,
                                            multipleGender: profileDataAboutBaby?.multipleGender,
                                            singleOrMultipleBirth: profileDataAboutBaby?.singleOrMultipleBirth,
                                            babyBornDate: self.babyBornDate, // array
                                            childHasSpecialNeed: profileDataAboutBaby?.childHasSpecialNeed,
                                            firstTimeMom: profileDataAboutBaby?.firstTimeMom)
                parameter = param
            }else {
                let  param = ProfileRequest(step: profileDataAboutBaby?.step,
                                            kinshipReason: profileDataAboutBaby?.kinshipReason,
                                            singleGender: profileDataAboutBaby?.singleGender,
                                            multipleGender: profileDataAboutBaby?.multipleGender,
                                            singleOrMultipleBirth: profileDataAboutBaby?.singleOrMultipleBirth,
                                            babyBornDate: self.babyBornDate, // array
                                            childHasSpecialNeed: profileDataAboutBaby?.childHasSpecialNeed,
                                            firstTimeMom: profileDataAboutBaby?.firstTimeMom)
                parameter = param
            }
            
            self.babyBornDate.removeAll()
            
            BringYouServices.shared.tryingToConceive (parameters: parameter.toJSON()) { [weak self] (StatusCode, response) in
                
                if let data = response.aboutConceiveResponse {
                    
                    let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "CompleteProfileScreen") as! CompleteProfileScreen
//                    vc.profileDataCompleteProfile = self?.profileDataAboutBaby
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
