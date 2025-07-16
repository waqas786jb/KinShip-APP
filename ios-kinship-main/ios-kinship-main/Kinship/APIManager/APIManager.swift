//
//  APIManager.swift
//  Kinship
//
//  Created by iMac on 21/03/24.
//

import Foundation
import Alamofire
import AlamofireObjectMapper

class APIManager {
    
    static let shared = { APIManager(baseURL: serverUrl) }()
    
    var baseURL: URL?
    
    required init(baseURL: String) {
        self.baseURL = URL(string: baseURL)
    }
    
    func getHeader() -> HTTPHeaders {
        
        var headerDic: HTTPHeaders = [:]
        
        if Utility.getUserData() == nil {
            headerDic = [ "Accept": "application/json" , "Content-Type": "application/json"]
        } else {
            if let accessToken = Utility.getAccessToken() {
                headerDic = [
                    "Authorization":"Bearer "+accessToken,
                    "Accept": "application/json",
                    "Content-Type": "application/json"
                ]
            }
            else {
                headerDic = [ "Accept": "application/json", "Content-Type": "application/json"]
            }
        }
        return headerDic
    }
    
    func isConnectedToNetwork()->Bool{
        return NetworkReachabilityManager()!.isReachable
    }
    
    func requestAPIWithParameters(method: HTTPMethod,urlString: String,parameters: [String:Any],success: @escaping(Int,Response) -> (),failure : @escaping(String) -> ()){
        
        if isAppInTestMode {
            print("Headers : ", getHeader())
            print("Method : ", method)
            print("UrlString : ", urlString)
            print("Parameters : ", parameters)
        }
        
        Alamofire.request(urlString, method: method, parameters: parameters, encoding: JSONEncoding.default, headers: getHeader()).responseObject { (response: DataResponse<Response>) in
            if isAppInTestMode {
                print("Response value : ", response.value?.toJSON() as Any)
                print("Response result : ", response.result.value?.toJSON() as Any)
                print("Response failure : ", failure)
            }
            
            switch response.result {
            case .success(let value):
                guard let statusCode = response.response?.statusCode else {
                    failure(value.message ?? "")
                    return
                }
                if (200..<300).contains(statusCode) {
                    success(statusCode,value)
//                    print("Status code is : \(statusCode)")
                }
                else if statusCode == 401 {
                    Utility.clearNotifications()
//                    Utility.hideIndicator()
                    Utility.removeUserData()
                    Utility.setLoginRoot()
                    userDefaults.clearUserDefaultsValues()
                    failure(value.message ?? "")
                }else if statusCode == 402{
                    failure(value.message ?? "")
                }else if statusCode == 403{
                    success(statusCode,value)
                }else{
                    failure(value.message ?? "")
                }
                break
            case .failure(let error):
                failure(error.localizedDescription)
                break
            }
        }
    }
    
    func requestAPIWithGetMethod(method: HTTPMethod,urlString: String,success: @escaping(Int,Response) -> (),failure : @escaping(String) -> ()){
        
        if isAppInTestMode {
            print("Headers : ", getHeader())
            print("Method : ", method)
            print("UrlString : ", urlString)
        }
        
        Alamofire.request(urlString, method: method, parameters: nil, encoding: JSONEncoding.default, headers: getHeader()).responseObject { (response: DataResponse<Response>) in
            
            if isAppInTestMode {
                print("Response value : ", response.value?.toJSON() ?? "")
                print("Response result : ", response.result.value?.toJSON() ?? "")
                print("Response failure : ", failure)
            }
            
            switch response.result{
            case .success(let value):
                guard let statusCode = response.response?.statusCode else {
                    failure(value.message ?? "")
                    return
                }
                if (200..<300).contains(statusCode){
                    success(statusCode,value)
                    print("Status code is : \(statusCode)")
                }else if statusCode == 401{
                    Utility.clearNotifications()
                    Utility.hideIndicator()
                    Utility.removeUserData()
                    Utility.setLoginRoot()
                    userDefaults.clearUserDefaultsValues()
                    failure(value.message ?? "")
                }else if statusCode == 402{
                    failure(value.message ?? "")
                }else if statusCode == 403{
                    success(statusCode,value)
                }else{
                    failure(value.message ?? "")
                }
                break
            case .failure(let error):
                failure(error.localizedDescription)
                break
            }
        }
    }
    
    func requestAPIWithParametersGetMethod(method: HTTPMethod, parameters: [String:Any], urlString: String,success: @escaping(Int,Response) -> (),failure : @escaping(String) -> ()){
        
        if isAppInTestMode {
            print("Headers : ", getHeader())
            print("Method : ", method)
            print("UrlString : ", urlString)
        }
        
        Alamofire.request(urlString, method: method, parameters: parameters, encoding: URLEncoding.default, headers: getHeader()).responseObject { (response: DataResponse<Response>) in
            
            if isAppInTestMode {
                print("Response value : ", response.value?.toJSON() ?? "")
                print("Response result : ", response.result.value?.toJSON() ?? "")
                print("Response failure : ", failure)
            }
            
            switch response.result{
            case .success(let value):
                guard let statusCode = response.response?.statusCode else {
                    failure(value.message ?? "")
                    return
                }
                if (200..<300).contains(statusCode){
                    success(statusCode,value)
                    print("Status code is : \(statusCode)")
                }else if statusCode == 401{
                    Utility.clearNotifications()
//                    Utility.hideIndicator()
                    Utility.removeUserData()
                    Utility.setLoginRoot()
                    userDefaults.clearUserDefaultsValues()
                    failure(value.message ?? "")
                }else if statusCode == 402{
                    failure(value.message ?? "")
                }else if statusCode == 403{
                    success(statusCode,value)
                }else{
                    failure(value.message ?? "")
                }
                break
            case .failure(let error):
                failure(error.localizedDescription)
                break
            }
        }
    }
    
    func requestWithImage(urlString : String,method: HTTPMethod = .post, imageParameterName : String,images : Data?, videoParameterName: String, videoData : Data?, audioParameterName : String, audioData : Data?, bgThumbnailParameter : String, bgThumbImage : Data?, videoPreviewParameter : String, videoPreview : Data?, parameters : [String:Any],success : @escaping(Int,Response) -> (),failure : @escaping(String) -> ()){

        if isConnectedToNetwork() == false {
            failure("No internet available.")
            return
        }
        print("url ----> ", urlString)
        print("parameters ----> ", parameters)
        print("headers ----> ", getHeader())

        
        Alamofire.upload(multipartFormData:{(multipartFormData) in

            if let image = images {
                multipartFormData.append(image, withName: imageParameterName,fileName: getFileName()+".jpg", mimeType: "image/jpg")
            }

            if let thumbImage = bgThumbImage {
                multipartFormData.append(thumbImage, withName: bgThumbnailParameter,fileName: getFileName()+".jpg", mimeType: "image/jpg")
            }

            if let video = videoData{
                //                do {
                //                    let data = try Data(contentsOf: video, options: .mappedIfSafe)
                //                    print(data)
                multipartFormData.append(video, withName: videoParameterName, fileName: getFileName()+".mp4", mimeType: "video/mp4")
                //                } catch  {
                //                }
                /*
                if let thumbImage = bgThumbImage{
                    multipartFormData.append(thumbImage, withName: bgThumbnailParameter,fileName: getFileName()+".jpg", mimeType: "image/jpg")
                }
                */

                if let videoPreviewGIF = videoPreview {
                    multipartFormData.append(videoPreviewGIF, withName: videoPreviewParameter,fileName: getFileName()+".gif", mimeType: "image/gif")
                }
            }
            if let audio = audioData{
                //                do {
                //                    let data = try Data(contentsOf: audio)
                //                    print(data)
                multipartFormData.append(audio, withName: audioParameterName, fileName: getFileName()+".mp3", mimeType: "audio/m4a")
                //                } catch  {
                //                }
            }
            for (key, value) in parameters {
                multipartFormData.append((value as AnyObject).data(using: String.Encoding.utf8.rawValue)!, withName: key)
            }

        }, to: urlString, method: method, headers: getHeader()){ (result) in
            switch result {
            case .success(let upload, _, _):

                upload.uploadProgress(closure: { (progress) in
                    print("Upload Progress: \(progress.fractionCompleted)")
                    //                    SVProgressHUD.showProgress(Float(progress.fractionCompleted), status: "Uploading...")
                })

                upload.responseObject { (response: DataResponse<Response>) in

                    if isAppInTestMode {
                        print("Response value : ", response.value?.toJSON() ?? "")
                        print("Response result : ", response.result.value?.toJSON() ?? "")
                        print("Response failure : ", failure)
                    }

                    switch response.result{
                    case .success(let value):
                        guard let statusCode = response.response?.statusCode else {
                            failure(value.message ?? "")
                            return
                        }

                        print("response ----> ", response.result.value?.toJSON() as Any)
                        if (200..<300).contains(statusCode){
                            success(statusCode,value)
                        }else if statusCode == 401{
                            // failure(value.message ?? "")
                            Utility.clearNotifications()
                            Utility.hideIndicator()
                            Utility.removeUserData()
                            Utility.setLoginRoot()
                            userDefaults.clearUserDefaultsValues()
                        }else if statusCode == 402{
                            failure(value.message ?? "")
                        }else if statusCode == 403{
                            success(statusCode,value)
                        }else{
                            failure(value.message ?? "")
                        }
                        break
                    case .failure(let error):
                        failure(error.localizedDescription)
                        break
                    }

                }
            case .failure(let error):
                failure(error.localizedDescription)
            }
        }
    }
        
}
