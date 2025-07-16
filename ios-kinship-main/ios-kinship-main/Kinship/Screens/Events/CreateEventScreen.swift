//
//  EventsScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit
import MobileCoreServices
import AVFoundation
import IQKeyboardManagerSwift

class CreateEventScreen: UIViewController {
    
    // MARK: - IBOUTLETS
    @IBOutlet weak var addPhotoView: UIView!
    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var photoImageView: UIImageView!
    @IBOutlet weak var allDaySwitch: UISwitch!
    @IBOutlet weak var allDaySwitchView: UIView!
    @IBOutlet weak var allDaySecondSwitch: UISwitch!
    @IBOutlet weak var allDaySwitchSecondView: UIView!
    @IBOutlet weak var eventNameTextField: UITextField!
    @IBOutlet weak var startTimeTextField: UITextField!
    @IBOutlet weak var endTimeTextField: UITextField!
    @IBOutlet weak var linkTextField: UITextField!
    @IBOutlet weak var locationTextField: UITextField!
    @IBOutlet weak var locationTextFieldButton: UIButton!
    @IBOutlet weak var eventDescriptionTextView: IQTextView!
    @IBOutlet weak var endTimeLabel: UILabel!
    @IBOutlet weak var startEndTimeView: UIView!
    @IBOutlet weak var startTimeLabel: UILabel!
    @IBOutlet weak var allDayLabel: UILabel!
    @IBOutlet weak var dateTextField: UITextField!
    @IBOutlet weak var evenTaitle: UILabel!
    
    // MARK: - VARIABLES
    var eventImageData : Data?
    var datePicker = UIDatePicker()
    var startTimePicker = UIDatePicker()
    var endTimePicker = UIDatePicker()
    let toolBar = UIToolbar()
    var startTime: String?
    var endTime: String?
    var allDay: String?
    var latitude: String?
    var longitude: String?
    var LocationName: String?
    var link: String?
    var location: String?
    var isFromEditEventScreen: Bool?
    var eventId: String?
    var getMyEvent: GetMyEventResponse?
    var eventDate: String?
    var onBack: ((Bool, CreateEventResponse?, CreateEventRequest?)->Void)?

    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialDetail()
    }
    
    // MARK: - FUNCTION
    
    func initialDetail() {
        
        self.submitButton.layer.cornerRadius = self.submitButton.frame.height/2
        self.setupDatePicker()
        self.allDay = "2"
        
        // From Edit event
        if self.isFromEditEventScreen == true {
            self.evenTaitle.text = "Edit Event"
            self.submitButton.setTitle("Update", for: .normal)
            
            self.eventNameTextField.text = self.getMyEvent?.eventName
            self.dateTextField.text = "\(Utility.setTimeToDateFormate(timestamp: "\(self.getMyEvent?.eventDate ?? Int(0))", dateFormate: MM_DD_YYYY))"
            
            self.startTimeTextField.text = "\(Utility.setTimeToTime(date: "\(self.getMyEvent?.startTime ?? Int(0))"))"
            self.endTimeTextField.text = "\(Utility.setTimeToTime(date: "\(self.getMyEvent?.endTime ?? Int(0))"))"
            self.startTime = "\(self.getMyEvent?.startTime ?? 0)"
            self.endTime = "\(self.getMyEvent?.endTime ?? 0)"
            self.eventDate = "\(self.getMyEvent?.eventDate ?? 0)"
            
            if self.getMyEvent?.location != ""{
                self.locationTextField.text = self.getMyEvent?.location
                self.location = self.getMyEvent?.location
            }
            if self.getMyEvent?.link != "" {
                self.linkTextField.text = self.getMyEvent?.link
                self.link = self.getMyEvent?.link
            }
            if let image = getMyEvent?.photo{
                Utility.setImage(image, imageView: self.photoImageView)
            }
            if getMyEvent?.isAllDay == true{
                self.startEndTimeView.isHidden = true
                self.allDaySecondSwitch.setOn(true, animated: true)
            }else{
                self.startEndTimeView.isHidden = false
                self.allDaySecondSwitch.setOn(false, animated: true)
            }
            
            if getMyEvent?.lat == "" && getMyEvent?.long == ""{
                self.latitude = nil
                self.longitude = nil
            }else{
                self.latitude = self.getMyEvent?.lat
                self.longitude = self.getMyEvent?.long
            }
            self.eventDescriptionTextView.text = self.getMyEvent?.eventDescription
            
            self.datePicker.date = Utility.utcToLocalDate(timeInterval: self.eventDate ?? "")
        }
        
        if self.isFromEditEventScreen == false {
            self.startTimeTextField.isUserInteractionEnabled = false
            self.endTimeTextField.isUserInteractionEnabled = false
        } else {
            self.startTimeTextField.isUserInteractionEnabled = true
            self.endTimeTextField.isUserInteractionEnabled = true
            self.setUpStartTime()
            self.setupEndTime()
        }
    }
    
    // MARK: - IBACTION
    @IBAction func onBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onSubmit(_ sender: UIButton) {
        if self.linkTextField.text == ""{
            self.link = nil
        }else{
            self.link = self.linkTextField.text
        }
        if self.locationTextField.text == ""{
            self.location = nil
        }else{
            self.location = self.locationTextField.text
        }
        
        if self.eventNameTextField.text == "" {
            Utility.showAlert(message: "Please enter event name")
        }else if self.dateTextField.text == "" {
            Utility.showAlert(message: "Please enter event date")
        } else{
            if self.allDaySecondSwitch.isOn {
                self.allDay = "1"
                if self.linkTextField.text != "" {
                    if !Utility.validateUrl(urlString: self.linkTextField.text ?? "") {
                        Utility.showAlert(message: "Please enter valid link")
                    } else {
                        self.allDayON()
                    }
                } else {
                    self.allDayON()
                }
                
            }else{
                self.allDay = "2"
                if self.linkTextField.text != "" {
                    if !Utility.validateUrl(urlString: self.linkTextField.text ?? "") {
                        Utility.showAlert(message: "Please enter valid link")
                    } else {
                        self.allDayOff()
                    }
                } else {
                    self.allDayOff()
                }
               
            }
        }
    }
    func allDayON(){
        if self.eventDescriptionTextView.text == "" {
            Utility.showAlert(message: "Please enter event description")
        }else{
            if isFromEditEventScreen == true{
                let eventStartDate = Utility.setTimeToDateConvert(timestamp: "\(self.eventDate ?? "")", dateFormate: MM_DD_YYYY)
                self.startTime = Utility.getTimeStampInMillisecond(date: eventStartDate.startOfDay)
                self.endTime = Utility.getTimeStampInMillisecond(date: eventStartDate.endOfDay)
                self.updateEventAPI()
            }else if isFromEditEventScreen == false{
                self.startTime = Utility.getTimeStampInMillisecond(date: self.datePicker.date.startOfDay)
                self.endTime = Utility.getTimeStampInMillisecond(date: self.datePicker.date.endOfDay)
                self.createEventAPI()
            }
        }
    }
    
    func allDayOff(){
        if self.startTimeTextField.text == "" || self.endTimeTextField.text == "" {
            Utility.showAlert(message: "Please enter Start and End time")
        }else if self.eventDescriptionTextView.text == "" {
            Utility.showAlert(message: "Please enter event description")
        } else {
            if isFromEditEventScreen == true{
                self.updateEventAPI()
            }else if isFromEditEventScreen == false{
                self.startTime = Utility.getTimeStampInMillisecond(date: self.startTimePicker.date)
                self.endTime = Utility.getTimeStampInMillisecond(date: self.endTimePicker.date)
                self.createEventAPI()
            }
        }
    }
    
    @IBAction func onAddPhoto(_ sender: UIButton) {
        self.selectProfilePictureOrCapture()
    }
    
    @IBAction func onAllDay(_ sender: UISwitch) { }
    
    @IBAction func onAllDaySecondSwitch(_ sender: UISwitch) {
        
        if sender.isOn{
            self.startEndTimeView.isHidden = true
            self.allDayLabel.textColor = UIColor.black
        }else{
            self.startEndTimeView.isHidden = false
            self.startTimeTextField.text = ""
            self.endTimeTextField.text = ""
            self.allDayLabel.textColor = UIColor.black.withAlphaComponent(0.50)
        }
    }
    @IBAction func onLocation(_ sender: Any) {
        let vc = STORYBOARD.events.instantiateViewController(withIdentifier: "LocationSearchScreen") as! LocationSearchScreen
        self.present(vc, animated: true, completion: nil)
        vc.back = { [self] lat, long, name in
            self.latitude = String(lat)
            self.longitude = String(long)
            self.locationTextField.text = name
            vc.dismiss(animated: true, completion: nil)
        }
    }
    
//    MARK: - FUNCTIONS
    func selectProfilePictureOrCapture() {
        
        let alert = UIAlertController(title: APPLICATION_NAME , message: "Please Select an Option", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Camera", style: .default , handler:{ (UIAlertAction)in
            self.proceedWithCameraAccess()
        }))
        
        alert.addAction(UIAlertAction(title: "Library", style: .default , handler:{ (UIAlertAction)in
            ImagePicker.photoLibrary(target: self, edit: true)
        }))
        
        alert.addAction(UIAlertAction(title: "Dismiss", style: .cancel))
        
        //uncomment for iPad Support
        //alert.popoverPresentationController?.sourceView = self.view
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    
    func proceedWithCameraAccess() {
        
        AVCaptureDevice.requestAccess(for: .video) { success in
            
            if success {
                DispatchQueue.main.async {
                    ImagePicker.cameraPhoto(target: self, edit: true)
                }
            }
            else {
                let alert = UIAlertController(title: "Camera", message: "Camera access is necessary to take photo.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Open Setting", style: .default, handler: { action in
                    UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
                }))
                
                alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { action in
                    
                }))
                
                DispatchQueue.main.async {
                    self.present(alert, animated: true)
                }
            }
        }
    }
}

      // MARK: - EXENSION
extension CreateEventScreen {
    //For birthday date
    func setupDatePicker() {
        let toolbar = UIToolbar()
        self.datePicker.datePickerMode = .date
        self.datePicker.locale = Locale(identifier: "en_US")
        if #available(iOS 13.4, *) {
            self.datePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        self.datePicker.minimumDate =  Date()
        //self.datePicker.minimumDate = Calendar.current.date(byAdding: .year, value: -100, to: Date())
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(doneButtonTapped))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        toolbar.items = [cancelButton, flexSpace, doneButton]
        toolbar.sizeToFit()
        self.dateTextField.inputAccessoryView = toolbar
        self.dateTextField.inputView = self.datePicker
        
    }
    @objc func doneButtonTapped() {
        self.dateTextField.text = dateToString(Formatter: MM_DD_YYYY, date: self.datePicker.date)
        self.startTimeTextField.isUserInteractionEnabled = true
        
        if self.allDaySecondSwitch.isOn {
            self.startTime = Utility.getTimeStampInMillisecond(date: self.datePicker.date.startOfDay)
            self.endTime = Utility.getTimeStampInMillisecond(date: self.datePicker.date.endOfDay)
        }
        self.eventDate = Utility.getTimeStampInMillisecond(date: self.datePicker.date)
        self.startTimePicker.date = self.datePicker.date
        self.endTimePicker.date = self.datePicker.date
        let calendar = Calendar.current
        if  calendar.isDateInToday(datePicker.date) == true{
            self.allDaySecondSwitch.isEnabled = false
            self.startEndTimeView.isHidden = false
            self.allDayLabel.textColor = UIColor.black.withAlphaComponent(0.50)
            self.allDaySecondSwitch.isOn = false
            self.startTimeTextField.text = ""
            self.endTimeTextField.text = ""
            Utility.showAlert(message: "You can create an event after the current time")
        }else{
            self.allDaySecondSwitch.isEnabled = true
        }
        self.setUpStartTime()
        view.endEditing(true)
    }
    @objc func cancelButtonTapped() {
        view.endEditing(true)
    }
    
    // For Start time
    func setUpStartTime() {
        let toolbar = UIToolbar()
        self.startTimePicker.datePickerMode = .time
        let calendar = Calendar.current

        if self.isFromEditEventScreen == false {
            if calendar.isDateInToday(datePicker.date) == true {
                self.startTimePicker.minimumDate = self.datePicker.date + 60*60
            }
        } else {
            if calendar.isDateInToday(Utility.setTimeToDateConvert2(timestamp: self.eventDate ?? "")) == true {
                self.startTimePicker.minimumDate = Date() + 60*60
            }
        }
        self.startTimePicker.locale = NSLocale(localeIdentifier: "en_US") as Locale
        if #available(iOS 13.4, *) {
            self.startTimePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(timeDoneButtonTapped))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        toolbar.items = [cancelButton, flexSpace, doneButton]
        toolbar.sizeToFit()
        self.startTimeTextField.inputAccessoryView = toolbar
        self.startTimeTextField.inputView = self.startTimePicker
    }
    @objc func timeDoneButtonTapped() {
        self.endTimeTextField.isUserInteractionEnabled = true
//        self.selectedTime = self.datePicker.date
        let formatedDate =  dateToString(Formatter: H_MM_A, date: self.startTimePicker.date)
        self.startTimeTextField.text = "\(formatedDate)"
//        let value = self.startTimePicker.date.adding(minutes: 20)
        self.startTime = Utility.getTimeStampInMillisecond(date: self.startTimePicker.date)
//        self.startTime = Utility.getTimeStampInMillisecond(date: value)
        view.endEditing(true)
        self.setupEndTime()
     }
    
//    For End time
    func setupEndTime() {
        let toolbar = UIToolbar()
        self.endTimePicker.datePickerMode = .time
        if self.isFromEditEventScreen == false {
            self.endTimePicker.minimumDate = self.startTimePicker.date + 60*60
        } else {
            self.endTimePicker.minimumDate = (Utility.utcToLocalDate(timeInterval: self.startTime ?? "")) + 60*60
        }
       
        self.endTimePicker.locale = Locale(identifier: "en_US")
        if #available(iOS 13.4, *) {
            self.endTimePicker.preferredDatePickerStyle = .wheels
        } else {
            // Fallback on earlier versions
        }
        
        let doneButton = UIBarButtonItem(title: "Done", style: .done, target: self, action: #selector(endTimeDoneButtonTapped))
        let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
        let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        toolbar.items = [cancelButton, flexSpace, doneButton]
        toolbar.sizeToFit()
        self.endTimeTextField.inputAccessoryView = toolbar
        self.endTimeTextField.inputView = self.endTimePicker
    }
    
    @objc func endTimeDoneButtonTapped() {
//        self.selectedTime = self.datePicker.date
        let formatedDate =  dateToString(Formatter: H_MM_A, date: self.endTimePicker.date)
        self.endTimeTextField.text = "\(formatedDate)"
        self.endTime = Utility.getTimeStampInMillisecond(date: self.endTimePicker.date)
        view.endEditing(true)
     }
}
// MARK: - PICKET VIEW DELEDATE METHOD
extension CreateEventScreen:  UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        guard let mediaType = info[UIImagePickerController.InfoKey.mediaType] as? String else {return}
        
        if mediaType == kUTTypeImage as String {
            
            if let image = info[.editedImage] as? UIImage {
                self.photoImageView.image = image
                if let imgData = Utility.getCompressedImageData(image) {
                    self.eventImageData = imgData
                } else {
                    let imageData = image.jpegData(compressionQuality:0.8)!
                    self.eventImageData = imageData
                }
                self.dismiss(animated: true, completion: nil)
                
            } else if let image = info[.originalImage] as? UIImage  {
                self.photoImageView.image = image
                self.dismiss(animated: true, completion: nil)
            }
            
            self.dismiss(animated: true, completion: nil)
        }
    }
}

//       MARK: - API CALL
extension CreateEventScreen{
//     Create event
    func createEventAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = CreateEventRequest(eventName: self.eventNameTextField.text,
                                           eventDate: self.eventDate,
                                           startTime: self.startTime,
                                           endTime: self.endTime,
                                           isAllDay: self.allDay,
                                           link: self.link,
                                           location: self.location,
                                           eventDescription: self.eventDescriptionTextView.text,
                                           lat: self.latitude,
                                           long: self.longitude)
            GetMyEventDetailsServices.shared.createEvent(parameters : param.toJSON(), imageData: self.eventImageData) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.createEventResponse{
                    Utility.successAlert(message: response?.message ?? "")
                    self?.navigationController?.popViewController(animated: true)
                    self?.onBack?(true, data, param)
                }
            } failure: { [weak self] error in
                Utility.hideIndicator()
                Utility.showAlert(message: error)
            }
        } else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
    // Edit Event
    func updateEventAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = CreateEventRequest(eventId: self.eventId,
                                           eventName: self.eventNameTextField.text,
                                           eventDate: self.eventDate,
                                           startTime: self.startTime,
                                           endTime: self.endTime,
                                           isAllDay: self.allDay,
                                           link: self.link,
                                           location: self.location,
                                           eventDescription: self.eventDescriptionTextView.text,
                                           lat: self.latitude,
                                           long: self.longitude)
            
            GetMyEventDetailsServices.shared.updateEventStatus(parameters : param.toJSON() ,imageData: self.eventImageData) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.message{
                    Utility.successAlert(message: data)
                    self?.navigationController?.popViewController(animated: true)
                    self?.onBack?(false, nil, nil)
                }
            } failure: { error in
                Utility.showAlert(message: error)
                Utility.hideIndicator()
            }
        }else{
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
