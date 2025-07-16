//
//  DirectMessageServices.swift
//  Kinship
//
//  Created by iMac on 29/04/24.
//

import Foundation

class DirectMessageServices{
    
    static let shared = { DirectMessageServices() }()
    
    func getUserGroupList(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: getUserGroupUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func getImageLinkMessageList(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: chatImageLinkMessageListUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func photosSend(parameters:[String:Any] = [:],imageData:Data? ,success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestWithImage(urlString: photosSendUrl, method: .post, imageParameterName: "file", images: imageData, videoParameterName: "", videoData: nil, audioParameterName: "", audioData: nil, bgThumbnailParameter: "", bgThumbImage: nil, videoPreviewParameter: "", videoPreview: nil, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
            
        } failure: { (error) in
            failure(error)
        }
    }
    
    func subgroupCreate(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: subgroupCreateUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func subgroupMemberList(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: subgroupMemberListUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
