//
//  AddPostScreen.swift
//  Kinship
//
//  Created by iMac on 06/11/24.
//

import UIKit
import AVFoundation
import IQKeyboardManagerSwift

class AddPostScreen: UIViewController {

    // MARK: - IB Outlets
    @IBOutlet weak var imageHideView: UIView!
    @IBOutlet weak var imageView: dateSportImageView!
    @IBOutlet weak var messageTextView: IQTextView!
    @IBOutlet weak var viewHeightConstrain: NSLayoutConstraint!
    
    
    // MARK: - Variables
    var imageData: Data?
    var communityId: String?
    var newPost: ((AddPostResponse)->Void)?
    
    // MARK: - Method’s
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialise()
        self.messageTextView.delegate = self
    }
    
    // MARK: - Function’s
    
    func initialise() {
        self.manageUI()
    }
    
    func manageUI() {
    }
    
    // MARK: - IB Action
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func onPhoto(_ sender: Any) {
        self.selectProfilePictureOrCapture()
    }
    @IBAction func onSendPost(_ sender: Any) {
        if self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 && self.imageData == nil {
            Utility.showAlert(message: "You must post image or message")
        } else {
            self.addNewPost()
        }
    }
}

// MARK: - Extension
extension AddPostScreen {
    func selectProfilePictureOrCapture() {
        
        let alert = UIAlertController(title: APPLICATION_NAME , message: "Please Select an Option", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Library", style: .default , handler:{ (UIAlertAction)in
            ImagePicker.photoLibrary(target: self, edit: true)
        }))
        
        alert.addAction(UIAlertAction(title: "Camera", style: .default , handler:{ (UIAlertAction)in
            self.proceedWithCameraAccess()
        }))
        
        alert.addAction(UIAlertAction(title: "Cancle", style: .cancel))
        
        //uncomment for iPad Support
        //alert.popoverPresentationController?.sourceView = self.view
        
        self.present(alert, animated: true, completion: {
//            print("completion block")
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

extension AddPostScreen: UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let image = info[.editedImage] as? UIImage {
                self.imageView.image = image
                self.imageHideView.isHidden = false
                if let imgData = Utility.getCompressedImageData(image) {
                    self.imageData = imgData
                } else {
                    let imageData = image.jpegData(compressionQuality:0.8)!
                    self.imageData = imageData
                }
            }
        self.view.endEditing(true)
        self.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.dismiss(animated: true, completion: nil)
    }
}

// MARK: - Delegate Method

// MARK: - API Call
extension AddPostScreen {
    func addNewPost(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = addNewPostRequest(communityId: self.communityId, message: self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 ? nil : self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines))
            Community.shared.newPost(parameters: param.toJSON(), imageData: self.imageData) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.addPostResponse {
                    Utility.successAlert(message: response?.message ?? "")
                    self?.newPost?(data)
                    self?.navigationController?.popViewController(animated: true)
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

extension AddPostScreen: UITextViewDelegate {
    func textViewDidChange(_ textView: UITextView){
        let maxHeight: CGFloat = self.messageTextView.frame.height * 2
        let minHeight: CGFloat = 50.0
        print(min(maxHeight, max(minHeight, textView.contentSize.height)), self.view.layoutIfNeeded())
        self.viewHeightConstrain.constant = min(maxHeight, max(minHeight, textView.contentSize.height))
        self.view.layoutIfNeeded()
    }
}
