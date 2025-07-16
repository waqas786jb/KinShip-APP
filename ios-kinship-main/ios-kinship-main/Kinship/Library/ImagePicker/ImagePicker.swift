//
//  ImagePicker.swift
//  Kinship
//
//  Created by iMac on 15/04/24.
//

import UIKit
import MobileCoreServices

class ImagePicker: NSObject {
    
    class func cameraPhoto(target: Any, edit: Bool) {
        
        let type = kUTTypeImage as String
        if (UIImagePickerController.isSourceTypeAvailable(.camera)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .camera) {
                if (availableMediaTypes.contains(type)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.mediaTypes = [type]
                    imagePicker.sourceType = .camera
                    
                    
                    if (UIImagePickerController.isCameraDeviceAvailable(.rear)) {
                        imagePicker.cameraDevice = .rear
                    } else if (UIImagePickerController.isCameraDeviceAvailable(.front)) {
                        imagePicker.cameraDevice = .front
                    }
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.showsCameraControls = true
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
    }
    
    class func photoLibrary(target: Any, edit: Bool) {
        
        let type = kUTTypeImage as String
        
        if (UIImagePickerController.isSourceTypeAvailable(.photoLibrary)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary) {
                if (availableMediaTypes.contains(type)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .photoLibrary
                    imagePicker.mediaTypes = [type]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
        else if (UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .savedPhotosAlbum) {
                if (availableMediaTypes.contains(type)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .savedPhotosAlbum
                    imagePicker.mediaTypes = [type]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
    }

    class func photoLibraryMulti(target: Any, edit: Bool) {
    
        let type1 = kUTTypeImage as String
        let type2 = kUTTypeMovie as String
        
        if (UIImagePickerController.isSourceTypeAvailable(.photoLibrary)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary) {
                if (availableMediaTypes.contains(type1) && availableMediaTypes.contains(type2)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .photoLibrary
                    imagePicker.mediaTypes = [type1,type2]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
        
        else if (UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .savedPhotosAlbum) {
                if (availableMediaTypes.contains(type1) && availableMediaTypes.contains(type2)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .savedPhotosAlbum
                    imagePicker.mediaTypes = [type1,type2]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
    }
}
