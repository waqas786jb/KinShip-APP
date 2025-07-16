//
//  Utility.swift
//  Kinship
//
//  Created by Vikram's Macbook on 29/02/24.
//

import UIKit
import NotificationBannerSwift
import SDWebImage
import LGSideMenuController

class Utility: NSObject {
    
    //MARK: reachability
    class func isInternetAvailable() -> Bool
    {
        var  isAvailable : Bool
        isAvailable = true
        let reachability = Reachability()!
        if(reachability.connection == .none)
        {
            isAvailable = false
        }
        else
        {
            isAvailable = true
        }
        
        return isAvailable
        
    }
    
    class func showAlert(vc: UIViewController? = nil, message: String) {
//        let alertController = UIAlertController(title: APPLICATION_NAME, message: message, preferredStyle: .alert)
//        let OKAction = UIAlertAction(title:  "OK", style: .default, handler: nil)
//        alertController.addAction(OKAction)
//        vc.present(alertController, animated: true, completion: nil)
//        self.showToast(message: message)
        let banner = GrowingNotificationBanner(title: APPLICATION_NAME, subtitle: message, style: .warning)
        banner.autoDismiss = true
        banner.duration = 4.0
        
        banner.show()
    }
    
    class func successAlert(message: String){
        let banner = GrowingNotificationBanner(title: APPLICATION_NAME, subtitle: message, style: .success)
        banner.autoDismiss = true
        banner.duration = 4.0
        banner.show()
    }
    
    //MARK: Internet Alert
    class func showNoInternetConnectionAlertDialog(vc: UIViewController) {
//        let alertController = UIAlertController(title: APPLICATION_NAME, message: "It seems you are not connected to the internet. Kindly connect and try again.", preferredStyle: .alert)
//        let OKAction = UIAlertAction(title: "OK", style: .default, handler: nil)
//        alertController.addAction(OKAction)
//        vc.present(alertController, animated: true, completion: nil)
//        self.showToast(message: )
        let banner = GrowingNotificationBanner(title: APPLICATION_NAME, subtitle: "It seems you are not connected to the internet. Kindly connect and try again.", style: .danger)
        banner.autoDismiss = true
        banner.duration = 4.0
        banner.show()

    }
//    MARK: - USER IS LOGIN OR NOT
    class func userIsLoginOrNot() -> Bool{
        var  isLogin : Bool
        isLogin = false
        if (UserDefaults.standard.object(forKey: IS_LOGIN) != nil) {
            let isUserLogin = (UserDefaults.standard.object(forKey: IS_LOGIN) as? String)
            if (isUserLogin=="1") {
                isLogin = true
            }
        }
        return isLogin
    }
    
//    class func showToast(message: String){
//        appDelegate.window?.makeToast(message, duration: 3.0, position: .bottom)
//    }
    
    class func clearNotifications(){
        UIApplication.shared.applicationIconBadgeNumber = 0 // For Clear Badge Counts
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }
    
    // MARK: - user defaults
      class func setUDVal( val : Any , forKey : String) {
          UserDefaults.standard.set(val, forKey: forKey)
          UserDefaults.standard.synchronize()
      }
    
//    MARK: - SET IMAGE
    class func setImage(_ imageUrl: String!, imageView: UIImageView!, placeHolderImage: String? = nil) {
            if imageUrl != nil && !(imageUrl == "") {
                imageView.sd_imageIndicator = SDWebImageActivityIndicator.gray
                imageView!.sd_setImage(with: URL(string: imageUrl! ), placeholderImage: UIImage(named: placeHolderImage ?? "place_holder_icon"))
            }
            else
            {
                imageView?.image = UIImage(named: placeHolderImage ?? "place_holder_icon")
            }
        }
    
    class func setGalleryImage(_ imageUrl: String!, imageView: UIImageView!, placeHolderImage: String? = nil) {
            if imageUrl != nil && !(imageUrl == "") {
                imageView.sd_imageIndicator = SDWebImageActivityIndicator.gray
                imageView!.sd_setImage(with: URL(string: imageUrl! ), placeholderImage: UIImage(named: placeHolderImage ?? "ic_gallery_load"))
            }
            else
            {
                imageView?.image = UIImage(named: placeHolderImage ?? "ic_gallery_load")
            }
        }
//    MARK: - SAVE DATA IN USER DEFAULTS
    class func saveUserData(data: [String: Any]){
        UserDefaults.standard.setValue(data, forKey: USER_DATA)
    }

//    MARK: - GET DETA FROM USER DEFAULTS
    class func getUserData() -> LogInResponse? {
        if let data = UserDefaults.standard.value(forKey: USER_DATA) as? [String: Any]{
            let dic = LogInResponse(JSON: data)
            return dic
        }
        return nil
    }

//    MARK: - GET ACCESSTOKEN FORM USER DEFAULTS
    class func getAccessToken() -> String? {
//        if let token = getUserData()?.auth?.accessToken?.accessToken {
        if let token = getUserData()?.auth?.accessToken {
            return token
        }
//        if let token = getUserData()?.accessToken?.token
//        getUserData()?.data?.auth?.accessToken
        return nil
    }
    
//    MARK: - REMOVE USER DATA FROM USER DEFAULTS
    class func removeUserData(){
        UserDefaults.standard.removeObject(forKey: USER_DATA)
    }
    
    
    class func setLoginRoot() {
        let vc = STORYBOARD.authentication.instantiateViewController(withIdentifier: "LoginScreen") as! LoginScreen
        let navVC = UINavigationController(rootViewController: vc)
        navVC.interactivePopGestureRecognizer?.isEnabled = false
        navVC.navigationBar.isHidden = true
        appDelegate.window?.rootViewController = navVC
        appDelegate.window?.makeKeyAndVisible()
        UIView.transition(with: appDelegate.window!, duration: 0.1, options: [.curveEaseInOut], animations: nil, completion: nil)
    }
    
    class func setTabBarRoot() {
        let rootViewController = STORYBOARD.tabbar.instantiateViewController(withIdentifier: "TabbarScreen") as! TabbarScreen
        let rightViewController = STORYBOARD.home.instantiateViewController(withIdentifier: "MenubarScreen") as! MenubarScreen

        let rootNavigationController = UINavigationController(rootViewController: rootViewController)
        rootNavigationController.isNavigationBarHidden = true
        let sideMenuController = LGSideMenuController(rootViewController: rootNavigationController,
                                                      rightViewController: rightViewController)
        
        sideMenuController.rightViewWidth = 226.0
        sideMenuController.rightViewPresentationStyle = .slideAbove
        sideMenuController.isRightViewSwipeGestureDisabled = true
        sideMenuController.delegate = rightViewController
        appDelegate.window?.rootViewController = sideMenuController
        appDelegate.window?.makeKeyAndVisible()
    }
    
    //    MARK: - SIDE MENU
       class func sideBarMenu() {
            let rootViewController = STORYBOARD.main.instantiateViewController(withIdentifier: "SplashScreen") as! SplashScreen
            let rightViewController = STORYBOARD.home.instantiateViewController(withIdentifier: "MenubarScreen") as! MenubarScreen

            let rootNavigationController = UINavigationController(rootViewController: rootViewController)
            rootNavigationController.isNavigationBarHidden = true

            let sideMenuController = LGSideMenuController(rootViewController: rootNavigationController,
                                                          rightViewController: rightViewController)

            sideMenuController.rightViewWidth = 226.0
            sideMenuController.rightViewPresentationStyle = .slideAbove

            appDelegate.window?.rootViewController = sideMenuController
            appDelegate.window?.makeKeyAndVisible()
        }
    
    class func getDateInLOCALFormat(_ strDate: String) -> String
    {
        var gmt = NSTimeZone(abbreviation: "IST")
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        dateFormatter.timeZone = gmt! as TimeZone
        
        let date: Date? = dateFormatter.date(from: strDate)
//        guard let date: Date = dateFormatter.date(from: strDate) else { return "NO Date" }
        gmt = NSTimeZone.system as NSTimeZone
        dateFormatter.timeZone   = gmt! as TimeZone
        dateFormatter.dateFormat = "dd MM, yyyy"
        var timeStamp: String = dateFormatter.string(from: date!)
        
        if (timeStamp.count) <= 0 {
            timeStamp = ""
        }
        
        return timeStamp
    }
    
//    MARK: - FOR TIME STEMP
    func getTodayDateAndTime() -> String {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMddHHmmss"
        return dateFormatter.string(from: Date())
    }
    class func gmtDateToTimestone(date: Date) -> String{
        let timezoneOffset =  TimeZone.current.secondsFromGMT()
        let epochDate = date.timeIntervalSince1970
        let timezoneEpochOffset = (epochDate + Double(timezoneOffset))
        let timeZoneOffsetDate = Date(timeIntervalSince1970: timezoneEpochOffset)
        return Utility.getTimeStampFromDate(date: timeZoneOffsetDate)
    }
    class func getTimeStampFromDate(date: Date) -> String {                 //For date to time stamp
            return String(format: "%.0f", (date.timeIntervalSince1970))
        }
    class func getTimeStampInMillisecond(date: Date) -> String {                           // For time stamp in millisecond
        return String(format: "%.0f", (date.timeIntervalSince1970 * 1000))
    }
    
    class func getTimeStampInMillisecond(date: String) -> String {                           // For time stamp in millisecond
        //        let milliseconds = Int(date.timeIntervalSince1970 * 1000)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = DD_MM_YYYY
        let date = dateFormatter.date(from: date)
        return String(format: "%.0f", ((date?.timeIntervalSince1970 ?? 0.00) * 1000))
    }
    class func getTimeForTimestamp(timestamp: String) -> String {           // For time stamp to time
        var strDate = ""
        if let unixTime = Double(timestamp) {
            let date = Date(timeIntervalSince1970: unixTime)
            let dateFormatter = DateFormatter()
            let timezone = TimeZone.current.abbreviation() ?? "CET"
            dateFormatter.timeZone = TimeZone(abbreviation: timezone)
            dateFormatter.locale = NSLocale.current
//            let calendar = Calendar.current
            //            dateFormatter.dateFormat = "dd.MM.yyyy HH:mm"
            dateFormatter.dateFormat = "hh:mm a"
            strDate = dateFormatter.string(from: date)
//                    print(strDate)
        }
        return strDate
    }
    class func setTimeToTimeZone(timestamp: String, dateFormate:String = "MMM d, YYYY") -> String {           // Timestamp in milliseconds to Date in 3 fomet
        var finalDate: String?
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
        let calendar = Calendar.current
        if  calendar.isDateInToday(date) == true{               // Today'd Day
            finalDate = "Today"
        }else if calendar.isDateInYesterday(date) == true {     // Yester day's Date
            finalDate = "Yesterday"
        }else{                                                  // Date
            dateFormatter.dateFormat = dateFormate
            finalDate = dateFormatter.string(from: date)
        }
//        print(finalDate ?? "")
        return finalDate ?? ""
    }
    class func setMultiTimeToTimeZone(timestamp: String, dateFormate:String = "MMM d, YYYY") -> String {           // Timestamp in milliseconds to Date in 3 fomet
        var finalDate: String?
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = NSTimeZone.local
        let calendar = Calendar.current
        if  calendar.isDateInToday(date) == true{               // Today'd Day
            finalDate = getTimeForTimestamp(timestamp: timestamp)
        }else if calendar.isDateInYesterday(date) == true {     // Yesterday's Date
            finalDate = "Yesterday"
        }else{                                                  // Date
            dateFormatter.dateFormat = dateFormate
            finalDate = dateFormatter.string(from: date)
        }
        return finalDate ?? ""
    }
    
    class func getDateForTimestamp(timestamp: String) -> String {           // For time stamp to time
        var strDate = ""
        if let unixTime = Double(timestamp) {
            let date = Date(timeIntervalSince1970: unixTime)
            let dateFormatter = DateFormatter()
            let timezone = TimeZone.current.abbreviation() ?? "CET"
            dateFormatter.timeZone = TimeZone(abbreviation: timezone)
            dateFormatter.locale = NSLocale.current
            dateFormatter.dateFormat = "EEEE, MMM d"
            strDate = dateFormatter.string(from: date)
        }
        return strDate
    }
    
    class func timeZoneToDate(timeZone: Int) -> String {
        var Date = ""
        let date = NSDate(timeIntervalSince1970: Double(timeZone) / 1000)
        let formatter = DateFormatter()
        formatter.timeZone = NSTimeZone(name: "CET") as? TimeZone
        formatter.dateFormat = "EEEE, MMM d"
        Date = formatter.string(from: date as Date)
//        print(Date)
        return Date
    }
    
    class func setTimeToTime(date: String) -> String {           // Timestamp in milliseconds to Time
        let outDate: Double = Double(date) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
      // dateFormatter.timeZone = /**/(abbreviation: "UTC")
        dateFormatter.timeZone = NSTimeZone.local
        dateFormatter.dateFormat = "hh:mm a"
//        print(dateFormatter.string(from: date))
        return dateFormatter.string(from: date)
    }
    
    class func setTimeToDate(timestamp: String) -> String {           // Timestamp in milliseconds to Date
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let formatter = DateFormatter()
        formatter.dateFormat = MM_DD_YYYY
        formatter.locale = NSLocale(localeIdentifier: "en_US") as Locale?
//        print(formatter.string(from: date))
//        print(formatter.date(from: "\(date)") ?? "")
        return formatter.string(from: date as Date)
    }
    
    class func setTimeToDateFormate(timestamp: String,dateFormate:String = "EEEE, MMM d") -> String {           // Timestamp in milliseconds to Date(Change format)
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = dateFormate
//        print(dateFormatter.string(from: date))
//        print(dateFormatter.date(from: dateFormatter.string(from: date)))
        return dateFormatter.string(from: date)
    }
    
    class func setTimeToDateConvert(timestamp: String,dateFormate:String = "EEEE, MMM d") -> Date {           // Timestamp in milliseconds to Date(Change format)
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = dateFormate
        let dateInString = dateFormatter.string(from: date)
//        print(dateInString)
        return dateStringToDate(dateString: dateInString) ?? Date()
    }
    
    class func setTimeToDateConvert2(timestamp: String,dateFormate:String = "dd MM yyyy") -> Date {           // Timestamp in milliseconds to Date(Change format)
        let outDate: Double = Double(timestamp) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = dateFormate
        dateFormatter.timeZone = .current
        dateFormatter.locale = Locale.current
        let dateInString = dateFormatter.string(from: date)
        return dateStringToDate(dateString: dateInString) ?? Date()
    }
    
    class func utcToLocalDate(timeInterval: String) -> Date{
        let outDate: Double = Double(timeInterval) ?? 0.0
        let date = Date(timeIntervalSince1970: (outDate / 1000.0))
           let dateFormatter = DateFormatter()
           dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
           dateFormatter.dateFormat = "yyyy-MM-dd hh:mm a"

           let strDate = dateFormatter.string(from: date)

           let outputFormatter = DateFormatter()
           outputFormatter.dateFormat = "yyyy-MM-dd hh:mm a"
           outputFormatter.timeZone = TimeZone(abbreviation: "UTC")

           let dt = outputFormatter.date(from: strDate)
           outputFormatter.timeZone = NSTimeZone.local
           outputFormatter.dateFormat = "yyyy-MM-dd HH:mm"
           
           return dt ?? Date()
       }
    
    class func dateStringToDate(dateString: String) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = MM_DD_YYYY
        dateFormatter.timeZone = .current
        dateFormatter.locale = Locale.current
        return dateFormatter.date(from: dateString)
    }
    
    class func dateToDateString(dateString: Date) -> String? {
        
        let timeStemp = String(format: "%.0f", (dateString.timeIntervalSince1970 * 1000))
        return self.setTimeToTimeZone(timestamp: timeStemp)
        
//        let dateFormatter = DateFormatter()
//        dateFormatter.dateFormat = MM_DD_YYYY
//        return dateFormatter.string(from: dateString)
    }
    
//    MARK: - FOR INDICATORS
    class func showIndicator(view:UIView) {
        ScreenIndicatorLoader.shared.showLoader(view: view)
    }
    
    class func hideIndicator() {
        ScreenIndicatorLoader.shared.hideLoader()
    }
    
//    MARK: - EMAIL VALIDATION
    class func validateEmail(_ emailStr: String) -> Bool
    {
        let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
        let emailTest  = NSPredicate(format: "SELF MATCHES %@", emailRegex)
        return emailTest.evaluate(with: emailStr)
    }
    
    class func isValidPassword(stringPassword: String) -> Bool {
            let passwordRegex =  "^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^!&+=]).{8,}$"
            let result = NSPredicate(format:"SELF MATCHES %@", passwordRegex)
            return result.evaluate(with: stringPassword)
        }
    
    class func isValidPhoneNumber(phone: String) -> Bool {
            let phoneRegex = "^[0-9+]{0,1}+[0-9]{9,14}$"
            let phoneTest = NSPredicate(format: "SELF MATCHES %@", phoneRegex)
            return phoneTest.evaluate(with: phone)
    }
//    MARK: - URL VALIDATION
    class func validateUrl(urlString: String) -> Bool {
        let urlRegEx = "((?:http|https)://)?(?:www\\.)?[\\w\\d\\-_]+\\.\\w{2,3}(\\.\\w{2})?(/(?<=/)(?:[\\w\\d\\-./_]+)?)?"
        return NSPredicate(format: "SELF MATCHES %@", urlRegEx).evaluate(with: urlString)
    }
    class func verifyUrl (urlString: String?) -> Bool {
       if let urlString = urlString {
           if let url  = NSURL(string: urlString) {
               return UIApplication.shared.canOpenURL(url as URL)
           }
       }
       return false
    }
//    MARK: - IMAGE RESIGE
    func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
        let size = image.size
        
        let widthRatio  = targetSize.width  / size.width
        let heightRatio = targetSize.height / size.height
        
        // Figure out what our orientation is, and use that to form the rectangle
        var newSize: CGSize
        if(widthRatio > heightRatio) {
            newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
        } else {
            newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
        }
        
        // This is the rect that we've calculated out and this is what is actually used below
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        
        // Actually do the resizing to the rect using the ImageContext stuff
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage!
    }
    
    class func getCompressedImageData(_ originalImage: UIImage?) -> Data? {
        // UIImage *largeImage = [info objectForKey:@"UIImagePickerControllerOriginalImage"];
        let largeImage = originalImage
        
        var compressionRatio: Double = 1
        var resizeAttempts = 3
        var imgData = largeImage?.jpegData(compressionQuality: CGFloat(compressionRatio))
        print(String(format: "Starting Size: %lu", UInt(imgData?.count ?? 0)))
        
        if imgData!.count > 1000000 {
            resizeAttempts = 4
        } else if imgData!.count > 400000 && imgData!.count <= 1000000 {
            resizeAttempts = 2
        } else if imgData!.count > 100000 && imgData!.count <= 400000 {
            resizeAttempts = 2
        } else if imgData!.count > 40000 && imgData!.count <= 100000 {
            resizeAttempts = 1
        } else if imgData!.count > 10000 && imgData!.count <= 40000 {
            resizeAttempts = 1
        }
        print("resizeAttempts \(resizeAttempts)")
        //Trying to push it below around about 0.4 meg
        //while ([imgData length] > 400000 && resizeAttempts > 0) {
        while resizeAttempts > 0 {
            
            resizeAttempts -= 1
            
            print("Image was bigger than 400000 Bytes. Resizing.")
            print(String(format: "%i Attempts Remaining", resizeAttempts))
            
            //Increase the compression amount
            compressionRatio = compressionRatio * 0.8
            print("compressionRatio \(compressionRatio)")
            
            //Test size before compression
            //            print(String(format: "Current Size: %lu", UInt(imgData.c)))
            imgData = largeImage!.jpegData(compressionQuality: CGFloat(compressionRatio))
            
            //Test size after compression
            //            print(String(format: "New Size: %lu", UInt(imgData.length())))
            
        }
        
        //Set image by comprssed version
        let savedImage = UIImage(data: imgData!)
        //Check how big the image is now its been compressed and put into the UIImageView
        // *** I made Change here, you were again storing it with Highest Resolution ***
        let endData = savedImage!.jpegData(compressionQuality: CGFloat(compressionRatio))
        //NSData *endData = UIImagePNGRepresentation(savedImage);
        
        print(String(format: "Ending Size: %lu", UInt(endData?.count ?? 0)))
        
        return endData
    }
    
    // MARK: - To convert pinCode into lat long
    class func getLocation(pinCode:String) -> CLLocationCoordinate2D{
        let searchRequest = MKLocalSearch.Request()
        searchRequest.naturalLanguageQuery = pinCode
        let search = MKLocalSearch(request: searchRequest)
        var myCoordinate : CLLocationCoordinate2D?
        search.start { response, error in
            guard let response = response else {
                print(error?.localizedDescription ?? "This should be impossible")
                return
            }
            
            if let coordinate = response.mapItems.first?.placemark.coordinate{
                
                myCoordinate = coordinate
            }
        }
        return myCoordinate ?? CLLocationCoordinate2D()
    }
    class func getLabelHeight(label:UILabel) -> (Int,Int){
        var lineCount = 0
        let textSize = CGSize(width: label.frame.size.width, height: CGFloat(MAXFLOAT))
        let rHeight = lroundf(Float(label.sizeThatFits(textSize).height))
        let charSize = lroundf(Float(label.font.lineHeight))
        lineCount = rHeight / charSize
        return (lineCount,rHeight)
    }
    class func labelWidth(height: CGFloat,font: UIFont,text: String) -> CGFloat {
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: .greatestFiniteMagnitude, height: height))
        label.numberOfLines = 0
        label.text = text
        label.font = font
        label.sizeToFit()
        return label.frame.width
    }
    class func openWebView(urlString: String) {
        guard let url = URL(string: urlString) else { return }
        UIApplication.shared.open(url)
    }
}
