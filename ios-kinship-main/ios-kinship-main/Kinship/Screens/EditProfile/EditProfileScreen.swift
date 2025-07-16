//
//  EditProfileScreen.swift
//  Kinship
//
//  Created by iMac on 08/04/24.
//

import UIKit
import RESegmentedControl
import SDWebImage

class EditProfileScreen: UIViewController {
  
//    MARK: - IBOUTLETS
    @IBOutlet weak var profileView: UIView!
    @IBOutlet weak var addNewButton: UIButton!
    @IBOutlet weak var addNewButtonView: UIView!
    @IBOutlet weak var updateButton: UIButton!
    @IBOutlet weak var birthDateTableViewConstant: NSLayoutConstraint!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var firstNameTextField: UITextField!
    @IBOutlet weak var lastNameTextField: UITextField!
    @IBOutlet weak var cityTextField: UITextField!
    @IBOutlet weak var bioTextField: UITextView!
    @IBOutlet weak var bioTextFieldView: UIView!
    @IBOutlet weak var birthDateTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "ChildBirthDateCell", bundle: nil)
            birthDateTableView.register(nib, forCellReuseIdentifier: "ChildBirthDateCell")
        }
    }
    @IBOutlet weak var childDetailsSegmentControll: RESegmentedControl!
//    MARK: - VARIABLES
    var profileImageData: Data?
    var userData = Utility.getUserData()
//    var profileImage : ((UIImage)->Void)?
    var isBirthDateDataSet: Bool = false
    var birthDateData: [Int] = []
    var bio: String?
    var city: String?
    
    let segmentItemArray: [SegmentItemModel] = [
        SegmentItemModel(id: 1, title: "Single", image: "ic_user_black"),
        SegmentItemModel(id: 2, title: "Multiple", image: "ic_user3")
    ]
    var dateArray:[BirthdayData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.manageUI()
        self.initialize()
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
    
    
//    MARK: - FUNCTIONS
    func manageUI(){
        self.profileView.layer.cornerRadius = 50
        self.profileView.layer.borderWidth = 1
        self.profileView.layer.borderColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
        
        self.addNewButton.layer.cornerRadius = 15
        self.updateButton.layer.cornerRadius = 25
        
        self.bioTextFieldView.layer.borderWidth = 1
        self.bioTextFieldView.layer.borderColor = #colorLiteral(red: 0.8666666667, green: 0.8666666667, blue: 0.8666666667, alpha: 1)
        self.bioTextFieldView.layer.cornerRadius = 10
        
        self.profileImageView.layer.cornerRadius = 50
        
//        self.childDetailsSegmentControll.configure(segmentItems: segmentItemArray.map({ SegmentModel(title: $0.title, imageName: $0.image) }), preset: customSimple3preset)
        
    }
    
    func initialize(){
        
        self.birthDateTableView.isScrollEnabled = false
        self.firstNameTextField.text = userData?.profile?.firstName
        self.lastNameTextField.text = userData?.profile?.lastName
        self.cityTextField.text = userData?.profile?.city
        self.bioTextField.text = userData?.profile?.bio
        if let profileImage = userData?.profile?.profileImage {
            Utility.setImage(profileImage, imageView: self.profileImageView)
        }
        
        if let birthDateData = self.userData?.profile?.editBabyBornDate {
            self.birthDateData = birthDateData
            if birthDateData.count > 0{
                for i in 1...birthDateData.count{
                    if self.dateArray.count < 1{
                        self.dateArray.append(BirthdayData(title: "1st Child’s Birthdate", dateTimeStamp: "\(birthDateData[i-1])"))
                    }else{
                        let getIndexPath =  i
                        let setNumber = getIndexPath == 1 ? "st" : getIndexPath == 2 ? "nd" : getIndexPath == 3 ? "rd" : "th"
                        self.dateArray.append(BirthdayData(title: "\(i)\(setNumber) Child’s Birthdate", dateTimeStamp: "\(birthDateData[i-1])"))
                    }
                   
                }
            }else{
                self.dateArray = [BirthdayData(title: "Child's Birthdate", dateTimeStamp: nil, isForSingle: true)]
            }
            
            self.isBirthDateDataSet = true
            
            if self.dateArray.count <= 1{
                self.childDetailsSegmentControll.configure(segmentItems: segmentItemArray.map({ SegmentModel(title: $0.title, imageName: $0.image) }), preset: customSimple3preset, selectedIndex: 0)
                self.addNewButtonView.isHidden = true
            }else{
                self.addNewButtonView.isHidden = self.dateArray.count > 2
                self.childDetailsSegmentControll.configure(segmentItems: segmentItemArray.map({ SegmentModel(title: $0.title, imageName: $0.image) }), preset: customSimple3preset, selectedIndex: 1)
            }
//        self.childDetailsSegmentControll.selectedSegmentIndex = 0
            self.totalHeightOfTableView()
            self.birthDateTableView.reloadData()
        }

//        if userData?.profile?.editBabyBornDate?.count == 1 || userData?.profile?.editBabyBornDate == nil {
//            self.childDetailsSegmentControll.selectedSegmentIndex = 0
//        }else if userData?.profile?.editBabyBornDate?.count == 2 {
//            self.childDetailsSegmentControll.selectedSegmentIndex = 1
//        }else if userData?.profile?.editBabyBornDate?.count == 3 {
//            self.childDetailsSegmentControll.selectedSegmentIndex = 1
//            self.addNewButtonView.isHidden = true
//        }
    }
//    MARK: - IBACTION
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSelectProfileImage(_ sender: Any) {
//        let alert = UIAlertController
        let alert = UIAlertController(title: APPLICATION_NAME, message: "Select profile image sores", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Select from Photo library", style: UIAlertAction.Style.default, handler: { _ in
            self.accessPhotoLibrary()       }))
        alert.addAction(UIAlertAction(title: "Click with camera", style: UIAlertAction.Style.default, handler: { _ in
            self.accessCamera()     }))
        alert.addAction(UIAlertAction(title: "Cancel", style: UIAlertAction.Style.cancel, handler: { _ in
            alert.dismiss(animated: true)
        }))
        
        self.present(alert, animated: true, completion: nil)
    }
    
    @IBAction func onBirthDateSegment(_ sender: Any) {
            if self.isBirthDateDataSet{
                self.isBirthDateDataSet = false
            }else{
                if self.childDetailsSegmentControll.selectedSegmentIndex == 0 {
                    
                    //            self.numberOfBirthdate = 1
                    self.dateArray = [BirthdayData(title: "Child's Birthdate", dateTimeStamp: nil, isForSingle: true)]
                    self.addNewButtonView.isHidden = true
                    self.totalHeightOfTableView()
                    self.birthDateTableView.reloadData()
                }else{
                    self.dateArray = [BirthdayData(title: "1st Child's Birthdate", dateTimeStamp: nil),
                                      BirthdayData(title: "2nd Child's Birthdate", dateTimeStamp: nil)
                    ]
                    //            self.numberOfBirthdate = 2
                    self.totalHeightOfTableView()
                    self.addNewButtonView.isHidden = false
                    self.birthDateTableView.reloadData()
                }
            }
    }
    
    @IBAction func onAddNew(_ sender: Any) {
        if self.dateArray.count == 3 {
            Utility.showAlert(message: "You are not able to add more then 3 child's birth")
        }else{
            self.dateArray.append(BirthdayData(title: "3rd Child's Birthdate", dateTimeStamp: nil))
            self.addNewButtonView.isHidden = true
            self.totalHeightOfTableView()
            self.birthDateTableView.reloadData()
        }
    }
    
    @IBAction func onUpdate(_ sender: Any) {
        if let error = checkValidation(){
            Utility.showAlert(message: error)
        }else{
            self.emptyOrNot()
            self.updateProfile()
        }
    }
    
    //      MARK: - FUNCTIONS
    func totalHeightOfTableView(){
        self.birthDateTableViewConstant.constant = CGFloat(100 * self.dateArray.count)
    }
    func accessPhotoLibrary(){
        let imageController = UIImagePickerController()
        imageController.delegate = self
        imageController.sourceType = UIImagePickerController.SourceType.photoLibrary
        self.present(imageController, animated: true)
    }
    func accessCamera(){
        let imageController = UIImagePickerController()
        imageController.delegate = self
        
        imageController.sourceType = UIImagePickerController.SourceType.camera
        self.present(imageController, animated: true)
    }
    
    func checkValidation()->String?{
        if  self.firstNameTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0{
            return "Please enter first name"
        }else if  self.lastNameTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0{
            return "Please enter last name"
        }else if self.childDetailsSegmentControll.selectedSegmentIndex == 1 && self.dateArray.compactMap({ $0.dateTimeStamp}).count < 2{
            return "Please select multiple birthdate"
        }
        return nil
    }
    func emptyOrNot(){
        if self.bioTextField.text.isEmpty {
            self.bio = nil
        }else{
            self.bio = self.bioTextField.text
        }
        if self.cityTextField.text?.isEmpty ?? true {
            self.city = nil
        }else{
            self.city = self.cityTextField.text
        }
    }
}

//  MARK: - TABLEVIEW DELEGATE, DATASOURSE
extension EditProfileScreen: UITableViewDelegate, UITableViewDataSource {
      
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.dateArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ChildBirthDateCell") as! ChildBirthDateCell
        cell.dateClosure = { [weak self] value in
            guard let strongSelf = self  else {return}
            let obj = strongSelf.dateArray[indexPath.row]
            obj.dateTimeStamp = value
            strongSelf.dateArray[indexPath.row] = obj
        }
        cell.childBirthDetails = self.dateArray[indexPath.row]
        return cell
    }
}

// MARK: - IMAGE PICKER DELEGATE
extension EditProfileScreen: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if let selectedImage = info[UIImagePickerController.InfoKey.originalImage] as? UIImage{
            self.profileImageView.image = selectedImage
            
            if let imgData = Utility.getCompressedImageData(selectedImage) { // Right
                self.profileImageData = imgData
            }
            else {
                self.profileImageData = selectedImage.jpegData(compressionQuality:0.5)!
            }
        }
                self.dismiss(animated: true)
    }
        
}
    
    //MARK: - API CALL
extension EditProfileScreen {
    func updateProfile(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = EditProfileRequest(firstName: self.firstNameTextField.text ?? "", lastName: self.lastNameTextField.text ?? "", city: self.city, childBirthday:self.dateArray.compactMap { $0.dateTimeStamp}.joined(separator: ",") == "" ? nil : self.dateArray.compactMap { $0.dateTimeStamp}.joined(separator: ","), bio: self.bio)
            
            EditProfileService.shared.editProfile(parameters : param.toJSON() ,imageData: profileImageData ) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                Utility.successAlert(message: response?.message ?? "")
                if let data = response?.profileResponse{
                    if let userData = Utility.getUserData() {
                        userData.profile = data
                        Utility.saveUserData(data: userData.toJSON())
                    }
                    self?.navigationController?.popViewController(animated: true)
                }
            } failure: { error in
//                print(error)
                Utility.showAlert(message: error)
                Utility.hideIndicator()
            }
        }else{
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
