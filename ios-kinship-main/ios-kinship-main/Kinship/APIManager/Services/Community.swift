//
//  Community.swift
//  Kinship
//
//  Created by iMac on 30/10/24.
//

import Foundation

class Community {
    
    static let shared = { Community() }()
    
    func myCommunity(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithGetMethod(method: .get, urlString: myCommunityUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func sendSuggestion(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: sendSuggestionUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func communityGetPost(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: communityPostUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func newPost(parameters:[String:Any] = [:],imageData:Data? ,success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()){
        APIManager.shared.requestWithImage(urlString: newPostUrl, method: .post, imageParameterName: "file", images: imageData, videoParameterName: "", videoData: nil, audioParameterName: "", audioData: nil, bgThumbnailParameter: "", bgThumbImage: nil, videoPreviewParameter: "", videoPreview: nil, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
            
        } failure: { (error) in
            failure(error)
        }
    }
    func commentGetPost(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: commentPostUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func addNewPost(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParameters(method: .post, urlString: addCommentUrl, parameters: parameters) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func likeOnPost(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: likePostUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func likeOnComment(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: likeCommentUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func exploreCommunity(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: exploreCommunityUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func leaveCommunity(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: leaveCommunityUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func joinCommunity(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: joinCommunityUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func myCommunitySearch(parameters: [String: Any] = [:], success: @escaping (Int, Response?) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: myCommunityUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
    
    func searchCommunityGetPost(parameters: [String: Any] = [:],success: @escaping (Int, Response) -> (), failure: @escaping (String) -> ()) {
        APIManager.shared.requestAPIWithParametersGetMethod(method: .get, parameters: parameters, urlString: searchCommunityPostUrl) { (statusCode, response) in
            success(statusCode,response)
        } failure: { (error) in
            failure(error)
        }
    }
}
