//
//  DirectMessageScreen.swift
//  Kinship
//
//  Created by iMac on 30/04/24.
//

import UIKit
import SocketIO
import IQKeyboardManagerSwift
import MobileCoreServices
import AVFoundation

// MARK: - ImagePickerDelegate Protocol
protocol ImagePickerDelegate: AnyObject {
    func imagePicker(_ imagePicker: ImagePicker, didSelect image: UIImage)
    func cancelButtonDidClick(on imagePicker: ImagePicker)
}

class DirectMessageScreen: UIViewController {
    
    //    MARK: - VARIABLES
    @IBOutlet weak var personNameLabel: UILabel!
    @IBOutlet weak var messageTextView: IQTextView!
    @IBOutlet weak var profileImageView: dateSportImageView!
    @IBOutlet weak var bottomContainerToSafeAreaConstraint: NSLayoutConstraint!
    @IBOutlet weak var chatTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "GroupChatCell", bundle: nil)
            self.chatTableView.register(nib, forCellReuseIdentifier: "GroupChatCell")
            
            let dateNib = UINib(nibName: "DateCell", bundle: nil)
            self.chatTableView.register(dateNib, forCellReuseIdentifier: "DateCell")
        }
    }
    
    //   MARK: - VARIABLES
    var chatData: UserGroupListResponse?
    var personChatData: [ChatImageLinkMessageResponse] = []
    var chatDetails: ChatImageLinkMessageResponse?
    var chatMetaData: ChatImageLinkMessageMetaResponse?
    var finalDictionary = [SectionMessageData]()
    var roomId: String?
    let senderId = Utility.getUserData()?.Id
    var receiverId: String?
    let imageController = UIImagePickerController()
    var chatGroupId: String?
    var imageData: Data?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.initialize()
    }
    func initialize(){
        self.setUpKeyboard()
        IQKeyboardManager.shared.enableAutoToolbar = false
        self.personNameLabel.text = self.chatData?.name
        Utility.setImage(self.chatData?.profileImage, imageView: self.profileImageView)
        self.createRoomId()
        self.socketListenMethods()
        print(Utility.getUserData()?.Id ?? "")
        self.chatTableView.showsHorizontalScrollIndicator = false
        self.chatTableView.showsVerticalScrollIndicator = false
    }
    fileprivate func setUpKeyboard(){
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if ((notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.height) != nil {
            if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                self.bottomContainerToSafeAreaConstraint.constant = topSafeArea == 20 ? (keyboardSize.height) + 5 : (keyboardSize.height) - 20
//                self.layoutIfNeeded()
            }
        }
    }
    @objc func keyboardWillHide(notification: NSNotification) {
        self.bottomContainerToSafeAreaConstraint.constant = 6
//        self.layoutIfNeeded()
    }
    //    MARK: - IB ACTION METHOD
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onPhotos(_ sender: Any) {
        self.selectProfilePictureOrCapture()
    }
    @IBAction func onSend(_ sender: Any) {
//        if chatDetails?.userId?.first
        if (self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count != 0) {
            if Utility.isInternetAvailable() {
                if SocketHelper.shared.checkConnection(){
                    if Utility.verifyUrl(urlString: self.messageTextView.text){
                        self.sendMessage(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: self.receiverId ?? "", groupId: "", type: 3, roomId: self.roomId)
                    }else{
                        self.sendMessage(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: self.receiverId ?? "", groupId: "", type: 1, roomId: self.roomId)
                    }
//                    self.sendMessage(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: , groupId: self.chatData?.Id ?? "", type: 1, roomId: self.roomId)
                }
            }
        }
    }
}

//  MARK: - TABLEVIEW DELEGATE
extension DirectMessageScreen: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return finalDictionary.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return finalDictionary[section].messageData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let mainArray = finalDictionary[indexPath.section].messageData?[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "GroupChatCell") as! GroupChatCell
        cell.isFromDirectMessageScreen = true
        cell.chatData = mainArray
        return cell
    }
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DateCell") as! DateCell
        cell.item = finalDictionary[section].headerDate
        return cell
    }
}
//MARK: - IMAGE PIKET VIEW EXTENSION
extension DirectMessageScreen: UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {

            if let image = info[.editedImage] as? UIImage {
                if let imgData = Utility.getCompressedImageData(image) {
                    self.imageData = imgData
                } else {
                    let imageData = image.jpegData(compressionQuality:0.8)!
                    self.imageData = imageData
                }
                self.dismiss(animated: true, completion: nil)
            }
        self.photosendAPI()
        self.dismiss(animated: true, completion: nil)
//        }
    }
}
//      MARK: - FUNCTION'S
extension DirectMessageScreen {
    func sendMessage(message: String?, senderId: String, receiverId: String, groupId: String, type: Int?, roomId: String?){
        var parameter = [String:Any]()
        //        Request parameter: { message, type: 1, senderId, receiverId, roomId, chatType }
        parameter = ["message": message ?? "",
                     "senderId": senderId,
                     "receiverId": receiverId,
                     "groupId": groupId,
                     "type": type ?? "",
                     "roomId": roomId ?? "" as Any] as [String:Any]
        
        print("parameter : ", parameter)
        SocketHelper.Events.sendMessage.emit(params: parameter)
        
            let newMessage = ChatImageLinkMessageResponse(Id: self.chatDetails?.Id,
                                                          senderId: senderId,
                                                          receiverId: receiverId,
                                                          groupId: groupId,
                                                          userId: nil,
                                                          message: message,
                                                          type: type,
                                                          grpSeenAt: nil,
                                                          seenAt: nil,
                                                          isLiked: nil,
                                                          isgrpLiked: nil,
                                                          createdAt: Int(Utility.getTimeStampInMillisecond(date: Date())),
                                                          updatedAt: nil,
                                                          profileImage: Utility.getUserData()?.profile?.profileImage,
                                                          name: self.chatDetails?.name,
                                                          image: nil)
//        self.addNewElementIntoDictionary(model: newMessage, currentKey: "Today")
        self.manageChatData(data: [newMessage])
        self.messageTextView.text = ""
        self.chatTableView.reloadData()
        if self.finalDictionary.count >= 1{
            self.chatTableView.scrollToBottom(with: false)
        }
    }
    
    func sendImageMessage(message: String?, senderId: String, receiverId: String, groupId: String, type: Int?, roomId: String?, chatId: String?){
        var parameter = [String:Any]()
        
        parameter = ["message": message ?? "",
                     "senderId": senderId,
                     "receiverId": receiverId,
                     "groupId": groupId,
                     "type": type ?? "",
                     "roomId": roomId ?? "",
                     "chatId": chatId ?? ""] as [String:Any]
        
        print("send image parameter : ", parameter)
        SocketHelper.Events.sendMessage.emit(params: parameter)
        
    }
    
//    func addNewElementIntoDictionary(model: ChatImageLinkMessageResponse) {
////        var newArray = [ChatImageLinkMessageResponse]()
//        let chatMessageData = ChatImageLinkMessageResponse(Id: model.Id, senderId: model.senderId, receiverId: model.receiverId, groupId: model.groupId, userId: model.userId, message: model.message, type: model.type, grpSeenAt: model.grpSeenAt, seenAt: model.seenAt, isLiked: model.isLiked, isgrpLiked: model.isgrpLiked, createdAt: model.createdAt, updatedAt: model.updatedAt, profileImage: model.profileImage, name: model.name, image: model.image)
////        newArray.append(chatMessageData)
//        self.personChatData.append(chatMessageData)
//        self.chatTableView.reloadData()
//        self.messageTextView.text = ""
//        
//    }
    func photoLibraryMulti(target: Any, edit: Bool) {
    
        let type1 = kUTTypeImage as String
        let type2 = kUTTypeMovie as String
        
        if (UIImagePickerController.isSourceTypeAvailable(.photoLibrary)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary) {
                if (availableMediaTypes.contains(type1) && availableMediaTypes.contains(type2)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .photoLibrary
                    imagePicker.mediaTypes = [type1,type2]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
        else if (UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum)) {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: .savedPhotosAlbum) {
                if (availableMediaTypes.contains(type1) && availableMediaTypes.contains(type2)) {
                    
                    let imagePicker = UIImagePickerController()
                    imagePicker.sourceType = .savedPhotosAlbum
                    imagePicker.mediaTypes = [type1,type2]
                    
                    let viewController = target as! UIViewController
                    imagePicker.allowsEditing = edit
                    imagePicker.delegate = viewController as? (UIImagePickerControllerDelegate & UINavigationControllerDelegate)
                    viewController.present(imagePicker, animated: true)
                }
            }
        }
    }
    func manageChatData(data: Array<ChatImageLinkMessageResponse>,isFromSendMessage:Bool = false){
        
        var dateArray = Array<Any>()
        if self.chatMetaData?.currentPage == 1 && isFromSendMessage == false{
            dateArray = Array(Set(data.compactMap({ Utility.setTimeToDateConvert(timestamp: "\($0.createdAt ?? 0)", dateFormate: MM_DD_YYYY)}))).sorted().compactMap({ Utility.dateToDateString(dateString: $0)})
        }else{
            dateArray = Array(Set(data.compactMap({ Utility.setTimeToDateConvert(timestamp: "\($0.createdAt ?? 0)", dateFormate: MM_DD_YYYY)}))).sorted().reversed().compactMap({ Utility.dateToDateString(dateString: $0)})
        }
        
        for i in dateArray{     // Set a date accoding to data
            print(i)
            let filterArray = data.filter({Utility.setTimeToTimeZone(timestamp: "\($0.createdAt ?? 0)") == i as! String})
            if self.chatMetaData?.currentPage == 1 && isFromSendMessage == false{
                self.finalDictionary.append(SectionMessageData(headerDate: i as? String, messageData: filterArray.reversed()))
            }else {
                let dateValue = self.finalDictionary.compactMap({$0.headerDate})
                
                if i as? String? == dateValue.first {
                    for i in 0...finalDictionary.count-1{
                        let obj = finalDictionary[i]
                        if isFromSendMessage == true{
                            obj.messageData?.append(contentsOf: filterArray)
                        }else{
                            obj.messageData?.insert(contentsOf: filterArray.reversed(), at: 0)
                        }
                        finalDictionary[i] = obj
                    }
                    
                }else {
                    self.finalDictionary.insert(SectionMessageData(headerDate: i as? String, messageData: filterArray.reversed()), at: 0)
                }
            }
        }
    }
    func selectProfilePictureOrCapture() {
        
        let alert = UIAlertController(title: APPLICATION_NAME , message: "Please Select an Option", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Library", style: .default , handler:{ (UIAlertAction)in
            ImagePicker.photoLibrary(target: self, edit: true)
        }))
        
        alert.addAction(UIAlertAction(title: "Camera", style: .default , handler:{ (UIAlertAction)in
            self.proceedWithCameraAccess()
        }))
        
        alert.addAction(UIAlertAction(title: "Cancle", style: .cancel))
        
        //uncomment for iPad Support
        //alert.popoverPresentationController?.sourceView = self.view
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    func proceedWithCameraAccess() {
        
        AVCaptureDevice.requestAccess(for: .video) { success in
            
            if success {
                DispatchQueue.main.async {
                    ImagePicker.cameraPhoto(target: self, edit: true)
                }
            }
            else {
                let alert = UIAlertController(title: "Camera", message: "Camera access is necessary to take photo.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Open Setting", style: .default, handler: { action in
                    UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
                }))
                
                alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { action in
                    
                }))
                
                DispatchQueue.main.async {
                    self.present(alert, animated: true)
                }
            }
        }
    }
}
extension DirectMessageScreen{
//    MARK: - CREATE ROOM ID
    fileprivate func createRoomId(){
        if Utility.isInternetAvailable() {
            if SocketHelper.shared.checkConnection() {
                self.getRoomId(senderId: "\(self.senderId ?? "")", receiverId: "\(self.receiverId ?? "")", groupId: "\(self.chatData?.Id ?? "")")
//                self.getRoomId(senderId: "\(self.senderId ?? "661e5ac9c07146f8690f2be8")", receiverId: "661e5d05c07146f8690f2ca2", groupId: "")
            }else{
                self.connectSocketIOAndSendMessage(shouldLogin: true)
            }
        }else {
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
    //MARK: CONNECT TO SOCKET
    func connectSocketIOAndSendMessage(shouldLogin: (Bool)){
        if(Utility.getUserData() != nil){
            SocketHelper.shared.connectSocket(completion: { val in
                if(val){
                    print("==== socket connected ====")
                    
                    if shouldLogin {
                        self.getRoomId(senderId: "\(self.senderId ?? "")", receiverId: "\(self.receiverId ?? "")", groupId: "\(self.chatData?.Id ?? "")")
                    }
                    var parameter: [String: Any]
                    parameter = ["senderId": "\(self.senderId ?? "")", "receiverId" : "\(self.receiverId ?? "")", "groupId": "\(self.chatData?.Id ?? "")"]
                    SocketHelper.Events.UpdateStatusToOnline.emit(params: parameter)
                }else{
                    print("socket did't connected")
                    Utility.showAlert(vc: self, message: "Could not connect to the server.")
                }
            })
        }
    }
    
//    MARK: - GET ROOM ID
    func getRoomId(senderId:String, receiverId: String, groupId: String) {
        var parameter = [String:Any]()
        parameter = ["senderId":senderId,
                     "receiverId": receiverId,
                     "groupId": groupId] as [String:Any]
        print("parameter : ", parameter)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5 , execute: {
           SocketHelper.Events.createRoom.emit(params: parameter)
           print(SocketHelper.shared.checkConnection())
            
            SocketHelper.Events.roomConnected.listen { [weak self] (result) in
                if let result = result as? String {
                    self?.roomId = result// dictFromJSON["roomId"] as? NSNumber
                    print("ROOM ID:", self?.roomId ?? "")
                }
                self?.messageAPI()
            }
        })
    }
    
//    MARK: - SOCKET LISTEN
    func socketListenMethods(){
        SocketHelper.Events.newMessage.listen { [weak self] (result) in
            print(result)

            do {
                let jsonData = try JSONSerialization.data(withJSONObject: result, options: .prettyPrinted)
                let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
                if let dictFromJSON = decoded as? [String:Any] {
                    print(dictFromJSON)

                    let newMessage = ChatImageLinkMessageResponse(Id: dictFromJSON["_id"] as? String,
                                                                  senderId: dictFromJSON["senderId"] as? String,
                                                                  receiverId: dictFromJSON["receiverId"] as? String,
                                                                  groupId: dictFromJSON["groupId"] as? String,
                                                                  userId: dictFromJSON["userId"] as? [String],
                                                                  message: dictFromJSON["message"] as? String,
                                                                  type: dictFromJSON["type"] as? Int,
                                                                  grpSeenAt: dictFromJSON["grpSeenAt"] as? [GrpSeenAt],
                                                                  seenAt: dictFromJSON["seenAt"] as? Bool,
                                                                  isLiked: dictFromJSON["isLiked"] as? Bool,
                                                                  isgrpLiked: dictFromJSON["isgrpLiked"] as? Bool,
                                                                  createdAt: dictFromJSON["createdAt"] as? Int,
                                                                  updatedAt: dictFromJSON["updatedAt"] as? Int,
                                                                  profileImage: dictFromJSON["profileImage"] as? String,
                                                                  name: dictFromJSON["name"] as? String,
                                                                  image: dictFromJSON["image"] as? String )

                    self?.manageChatData(data: [newMessage])
                    self?.chatTableView.reloadData()
                    if self?.finalDictionary.count ?? 0 >= 1{
                        self?.chatTableView.scrollToBottom(with: false)
                    }
                }
            } catch {
                print(error.localizedDescription)
            }
        }
   }
    
}
// MARK: - API CAll
extension DirectMessageScreen{
//
    func messageAPI(){
//    func messageAPI(id: String?, type: Int?, imageLinkType: Int?){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ChatImageLinkMessageList(id: chatData?.Id, type: 2, imageLinkType: 0, perPage: "100")
            print(param.toJSON())
            DirectMessageServices.shared.getImageLinkMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                print(response?.toJSON() as Any)
                if let meta = response?.chatImageLinkMessageMetaResponse {      // Manage meta
                    self?.chatMetaData = meta
                }
                if let data = response?.chatImageLinkMessageResponse {          // Manage data
                    self?.manageChatData(data: data)
                    self?.chatTableView.reloadData()
                    if self?.finalDictionary.count ?? 0 >= 1{
                        self?.chatTableView.scrollToBottom(with: false)
                    }
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
        else {
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
//  MARK: - PHOTOS SEND API
    func photosendAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = photosSendRequest(groupId: self.chatData?.Id, receiverId: self.receiverId)
//            print(self.chatGroupId)
            
            DirectMessageServices.shared.photosSend(parameters : param.toJSON() ,imageData: self.imageData ) { [weak self] (statusCode, response) in
//                print(response?.toJSON() as Any)
                Utility.hideIndicator()
                if let data = response?.photosSendResponse{
                    if SocketHelper.shared.checkConnection() {
                        let newImageMessage = ChatImageLinkMessageResponse(Id: self?.chatDetails?.Id,
                                                                           senderId: self?.senderId,
                                                                           receiverId: self?.receiverId,
                                                                           groupId: nil,
                                                                           userId: nil,
                                                                           message: data.image,
                                                                           type: 2,
                                                                           grpSeenAt: nil,
                                                                           seenAt: nil,
                                                                           isLiked: nil,
                                                                           isgrpLiked: nil,
                                                                           createdAt: Int(Utility.getTimeStampInMillisecond(date: Date())),
                                                                           updatedAt: nil,
                                                                           profileImage: Utility.getUserData()?.profile?.profileImage,
                                                                           name: self?.chatDetails?.name,
                                                                           image: data.image)
                        self?.sendImageMessage(message: data.image, senderId: data.senderId ?? "", receiverId: self?.receiverId ?? "", groupId: "", type: 2, roomId: self?.roomId, chatId: data.Id)
                        self?.manageChatData(data: [newImageMessage])
                        self?.chatTableView.reloadData()
                        if self?.finalDictionary.count ?? 0 >= 1{
                            self?.chatTableView.scrollToBottom(with: false)
                        }
                    }else{
                        self?.connectSocketIOAndSendMessage(shouldLogin: true)
                    }
                }
            } failure: { error in
                print(error)
                Utility.hideIndicator()
                Utility.showAlert(message: error)
            }
        }else{
            Utility.hideIndicator()
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
}
