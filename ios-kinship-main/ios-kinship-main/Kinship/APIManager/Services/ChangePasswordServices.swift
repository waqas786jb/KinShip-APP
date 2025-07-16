//
//  ChangePasswordServices.swift
//  Kinship
//
//  Created by iMac on 15/04/24.
//

import Foundation

class ChangePasswordService {
    
    static let shared = {ChangePasswordService() }()
    
    func changePassword(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: changePasswordUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
