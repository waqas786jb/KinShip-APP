//
//  EditProfileServices.swift
//  Kinship
//
//  Created by iMac on 11/04/24.
//

import Foundation

class EditProfileService {
    
    static let shared = {EditProfileService() }()
    
    func editProfile(parameters:[String:Any] = [:],imageData:Data? ,success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestWithImage(urlString: editProfileURL, method: .put,imageParameterName: "profileImage", images: imageData, videoParameterName: "", videoData: nil, audioParameterName: "", audioData: nil, bgThumbnailParameter: "", bgThumbImage: nil, videoPreviewParameter: "", videoPreview: nil, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
            
        } failure: { (error) in
            failure(error)
        }
    }
    
    func changePassword(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .put, urlString: editProfileURL, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func help(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: helpUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
