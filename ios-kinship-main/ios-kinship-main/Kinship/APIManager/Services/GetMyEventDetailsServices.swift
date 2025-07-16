//
//  GetMyEventDetailsServices.swift
//  Kinship
//
//  Created by iMac on 17/04/24.
//

import Foundation

class GetMyEventDetailsServices{
    
    static let shared = { GetMyEventDetailsServices() }()
    
    func myEvent(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: getMyEventsUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func muUpcomingEvent(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: getMyUpcomingEventUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func createEvent(parameters:[String:Any] = [:],imageData:Data? ,success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestWithImage(urlString: createEventUrl, method: .post, imageParameterName: "photo", images: imageData, videoParameterName: "", videoData: nil, audioParameterName: "", audioData: nil, bgThumbnailParameter: "", bgThumbImage: nil, videoPreviewParameter: "", videoPreview: nil, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
            
        } failure: { (error) in
            failure(error)
        }
    }
    
    func getAllEvents(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: allEventsUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func setEventStatus(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: eventStatusUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func updateEventStatus(parameters:[String:Any] = [:],imageData:Data? ,success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestWithImage(urlString: updateEventUrl, method: .put, imageParameterName: "photo", images: imageData, videoParameterName: "", videoData: nil, audioParameterName: "", audioData: nil, bgThumbnailParameter: "", bgThumbImage: nil, videoPreviewParameter: "", videoPreview: nil, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
            
        } failure: { (error) in
            failure(error)
        }
    }
    
    func deleteEvent(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .delete, parameters: parameters, urlString: deleteEventUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func getRSVPNames(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: getRSVPMemberUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
