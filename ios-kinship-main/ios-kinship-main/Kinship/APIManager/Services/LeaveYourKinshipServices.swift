//
//  LeaveYourKinshipServices.swift
//  Kinship
//
//  Created by iMac on 16/04/24.
//

import Foundation

class LeaveYourKinshipServices {
    
    static let shared = { LeaveYourKinshipServices() }()
    
    func leaveKinship(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: leaveKinshipUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func deleteAccount(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .delete, urlString: deleteAccountUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
