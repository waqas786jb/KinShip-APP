//
//  ForgotPasswordServices.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation

class ForgotPasswordServices {
    
    static let shared = { ForgotPasswordServices() }()
    
    //MARK: - FORGOT PASSWORD
    func forgotPassword(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: forgotPasswordUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    //MARK: - OTP VARIFICATION
    func otpVerification(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: otpVerificationUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    //MARK: - RESEND OTP VARIFICATION
    func resendOtpVerification(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: resendOtpVarificationUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
//  MARK: -  FORGOT PASSWORD IN CHANGE PASSWORD
    func forgotPasswordAuth(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: forgotPasswordAuthUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
