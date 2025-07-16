//
//  BringYouServices.swift
//  Kinship
//
//  Created by iMac on 27/03/24.
//

import Foundation

class BringYouServices {
    
    static let shared = { BringYouServices() }()
    
//    MARK: - PROFILE (Add Profile)
    func tryingToConceive(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: addProfileUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
//    MARK: - HOBBIES
    func hobbiesApi(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: hobbiesUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
