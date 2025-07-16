//
//  HomePageServices.swift
//  Kinship
//
//  Created by iMac on 02/04/24.
//

import Foundation

class HomePageServices {
    
    static let shared = { HomePageServices() }()
    
//    MARK: - CREATE GROUP DETAILS
    func getMyGroupApi(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: getGroupDetailsUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
