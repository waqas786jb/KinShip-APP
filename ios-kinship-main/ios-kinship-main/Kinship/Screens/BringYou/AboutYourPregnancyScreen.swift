//
//  AboutYourPregnancyScreen.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import UIKit
import RESegmentedControl

class AboutYourPregnancyScreen: UIViewController {
    
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
    
    @IBOutlet weak var havingChildTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "BringYouCell", bundle: nil)
            havingChildTableView.register(nib, forCellReuseIdentifier: "BringYouCell")
        }
    }
    @IBOutlet weak var havingChildTableViewHeight: NSLayoutConstraint!
    @IBOutlet weak var dueDateTextField: UITextField!
    
    //MARK: - VARIABLES
    var profileDataAboutPregnancy: ProfileRequest?
    var havingChildArray: [BringYouModel] = []
    
    let havingSingleChildArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "Girl"),
       BringYouModel(id: 2, title: "Boy"),
       BringYouModel(id: 3, title: "It’s a surprise")
    ]
    
    let havingMultipleChildArray : [BringYouModel] = [
        BringYouModel(id: 1, title: "Girls"),
        BringYouModel(id: 2, title: "Boys"),
        BringYouModel(id: 3, title: "Both"),
        BringYouModel(id: 4, title: "It’s a surprise")
     ]
    
    let firstTimeMomArray: [BringYouModel] = [
       BringYouModel(id: 1, title: "No"),
       BringYouModel(id: 2, title: "Yes"),
    ]
    
    var selectedId: Int?
    
    let segmentItemArray: [SegmentItemModel] = [
        SegmentItemModel(id: 1, title: "Single", image: "ic_user_black"),
        SegmentItemModel(id: 2, title: "Multiple", image: "ic_people_black"),
    ]
    
    var datePicker = UIDatePicker()
    let toolbar = UIToolbar()
    var birthDate : Date?
    var dueDate: Date?
    
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setupDatePicker()
        self.firstTimeMomSegmentedControl.selectedSegmentIndex = 1
    }
    
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
    
    // MARK: - IBACTION
    
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onContinue(_ sender: UIButton) {
        if dueDateTextField.text == "" {
            Utility.showAlert(message: "Please select due date")
        }else if selectedId == nil{
            Utility.showAlert(message: "Select are you having a boy or girl")
        }else {
            self.addProfile()
        }
        
    }
    
    @IBAction func onPregnancyTypeSegment(_ sender: RESegmentedControl) {
        
        self.havingChildArray.removeAll()
        
        if self.segmentedControl.selectedSegmentIndex == 0 {
            profileDataAboutPregnancy?.singleOrMultiplePregnancy = 1
            self.havingChildArray.append(contentsOf: self.havingSingleChildArray)
           
        }else if self.segmentedControl.selectedSegmentIndex == 1{
            profileDataAboutPregnancy?.singleOrMultiplePregnancy = 2
            self.havingChildArray.append(contentsOf: self.havingMultipleChildArray)

        }
        
        self.havingChildTableView.reloadData()
        self.setTableViewHeight()
    }

    @IBAction func onFirstTimeMomSegment(_ sender: Any) {
        
        if firstTimeMomSegmentedControl.selectedSegmentIndex == 0 {
            profileDataAboutPregnancy?.firstTimeMom = 2
            
        }else if firstTimeMomSegmentedControl.selectedSegmentIndex == 1 {
            profileDataAboutPregnancy?.firstTimeMom = 1
        }
        self.havingChildTableView.reloadData()
        self.setTableViewHeight()
    }
}
//MARK: - EXTENSION

extension AboutYourPregnancyScreen{
    
    //MARK: - DATE PICKER
    func setupDatePicker() {
        self.datePicker.datePickerMode = .date
        self.datePicker.locale = Locale(identifier: "en_US")
        if #available(iOS 13.4, *) {
            self.datePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        self.datePicker.minimumDate =  Date()//Calendar.current.date(byAdding: .year, value: 0, to: Date())
//        self.datePicker.maximumDate = Calendar.current.date(byAdding: .year, value: -18, to: Date())
//        self.datePicker.minimumDate = Calendar.current.date(byAdding: .year, value: -100, to: Date())
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(doneButtonTapped))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        self.toolbar.items = [cancelButton, flexSpace, doneButton]
        self.toolbar.sizeToFit()
        self.dueDateTextField.inputAccessoryView = self.toolbar
        self.dueDateTextField.inputView = self.datePicker
        
    }
//    MARK: - DONE BUTTON
    @objc func doneButtonTapped() {
        
        self.birthDate = self.datePicker.date
        
        let formatedDate =  dateToString(Formatter: MM_DD_YYYY, date: self.datePicker.date)
        self.dueDateTextField.text = "\(formatedDate)"
        view.endEditing(true)
     }
    
//    MARK: - CANCEL BUTTON
    @objc func cancelButtonTapped() {
        view.endEditing(true) // Dismiss the DatePicker
    }
}

//MARK: - TableView Delegates
extension AboutYourPregnancyScreen : UITableViewDelegate, UITableViewDataSource{
    
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
            self.profileDataAboutPregnancy?.singleGender = selectedId
            
        }else if self.segmentedControl.selectedSegmentIndex == 1 {
            self.profileDataAboutPregnancy?.multipleGender = selectedId
            
        }
        
        self.havingChildTableView.reloadData()
    }
    
}

//    MARK: - API CALL
extension AboutYourPregnancyScreen{

    func addProfile(){
        
        let myTimeStamp = Utility.getTimeStampFromDate(date: self.datePicker.date )  // convert date to time stamp
        
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ProfileRequest(step: profileDataAboutPregnancy?.step,
                                       kinshipReason: profileDataAboutPregnancy?.kinshipReason,
                                       whenIsYourDueDate: myTimeStamp,
                                       singleOrMultiplePregnancy: profileDataAboutPregnancy?.singleOrMultiplePregnancy,
                                       singleGender: profileDataAboutPregnancy?.singleGender,
                                       multipleGender: profileDataAboutPregnancy?.multipleGender,
                                       firstTimeMom: profileDataAboutPregnancy?.firstTimeMom)
            
            BringYouServices.shared.tryingToConceive (parameters: param.toJSON()) { [weak self] (StatusCode, response) in
                
                if let data = response.aboutConceiveResponse {
                    Utility.hideIndicator()
                    let vc = STORYBOARD.bringYou.instantiateViewController(withIdentifier: "CompleteProfileScreen") as! CompleteProfileScreen
//                    vc.profileDataCompleteProfile = self?.profileDataAboutPregnancy
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
