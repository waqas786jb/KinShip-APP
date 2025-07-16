//
//  ChildBirthDateCell.swift
//  Kinship
//
//  Created by iMac on 09/04/24.
//

import UIKit
class ChildBirthDateCell: UITableViewCell {

    @IBOutlet weak var birthDateTextField: UITextField!
    @IBOutlet weak var titleLabel: UILabel!
    
    var datePicker = UIDatePicker()
    let toolBar = UIToolbar()
    var selectedDate : Date?
    var dateClosure : ((String)->Void)?
    var userData = Utility.getUserData()
   

    override func awakeFromNib() {
        super.awakeFromNib()
        self.setUpDatePicker()
//        self.birthDateTextField.text = Utility.getUserData()?.profile?.babyBornDate
    }
    
    var childBirthDetails: BirthdayData?{
        didSet{
            if let date = childBirthDetails?.dateTimeStamp{
                self.birthDateTextField.text = Utility.setTimeToDate(timestamp: date)
            }else{
                self.birthDateTextField.text = ""
            }
           
            self.titleLabel.text = childBirthDetails?.title
        }
    }

}
extension ChildBirthDateCell {
    func setUpDatePicker() {
            
            self.datePicker = UIDatePicker(frame:CGRect(x: 0, y: 0, width: self.frame.size.width, height: 216))
            self.datePicker.backgroundColor = UIColor.white
            self.datePicker.maximumDate = Date()
            self.datePicker.minimumDate = Calendar.current.date(byAdding: .year, value: -100, to: Date())
            self.datePicker.datePickerMode = UIDatePicker.Mode.date
            if #available(iOS 14.0, *) {
                self.datePicker.preferredDatePickerStyle = .wheels
            } else {
                //datePicker.preferredDatePickerStyle = .automatic
            }
            self.birthDateTextField.inputView = datePicker
            self.toolBar.sizeToFit()
            let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(doneButtonTapped))
            let spaceButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelButton = UIBarButtonItem(title: "Cancel", style: .plain, target: self, action: #selector(cancelButtonTapped))
            self.toolBar.setItems([cancelButton,spaceButton, doneButton], animated: true)
            
            self.birthDateTextField.inputAccessoryView = self.toolBar
        }

    @objc func doneButtonTapped() {
        
        self.selectedDate = self.datePicker.date
        let formattedDate =  dateToString(Formatter: MM_DD_YYYY, date: self.datePicker.date)
        self.birthDateTextField.text = formattedDate
        let convertTimeStamp = Utility.getTimeStampInMillisecond(date: selectedDate ?? Date()) // convert date to time stame
        self.dateClosure?(convertTimeStamp)
        self.endEditing(true)
     }
    
//    MARK: - CANCEL BUTTON
    @objc func cancelButtonTapped() {
        self.endEditing(true) // Dismiss the DatePicker
    }
}
