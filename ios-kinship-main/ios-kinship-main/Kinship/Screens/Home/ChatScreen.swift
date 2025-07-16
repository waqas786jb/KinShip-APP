//
//  ChatScreen.swift
//  Kinship
//
//  Created by iMac on 04/04/24.
//

import UIKit
import LGSideMenuController
import IQKeyboardManagerSwift
import AVFoundation
import ObjectMapper
import SafariServices

class ChatScreen: UIViewController, UITextViewDelegate {

//    MARK: - OUTLETS
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var kinshipNameLabel: UILabel!
    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var userProfileImageView: UIImageView!
    @IBOutlet weak var menuButton: UIButton!
    @IBOutlet weak var messageTextView: IQTextView!
    @IBOutlet weak var photosButton: UIButton!
    @IBOutlet weak var noMessageLabel: UILabel!
    @IBOutlet weak var textViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var bottomContainerToSafeAreaConstraint: NSLayoutConstraint!
    @IBOutlet weak var groupChatTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "GroupChatCell", bundle: nil)
            groupChatTableView.register(nib, forCellReuseIdentifier: "GroupChatCell")
            
            let dateNib = UINib(nibName: "DateCell", bundle: nil)
            self.groupChatTableView.register(dateNib, forCellReuseIdentifier: "DateCell")
            
            let nameChangeNib = UINib(nibName: "GroupNameChange", bundle: nil)
            self.groupChatTableView.register(nameChangeNib, forCellReuseIdentifier: "GroupNameChange")
        }
    }
    
//    MARK: - VARIABLES
    var kinshipName: String = ""
    var userData = Utility.getUserData()
    var userID = Utility.getUserData()?.profile?.userId
    var personChatData: [ChatImageLinkMessageResponse] = []
    var chatMetaDetails: ChatImageLinkMessageMetaResponse?
    var roomId: String?
    var isFristFrom = true
    let senderId = Utility.getUserData()?.Id
    var chatDetails: ChatImageLinkMessageResponse?
    var finalDictionary: [SectionMessageData] = []
    let imagePickerVC = UIImagePickerController()
    var imageData: Data?
    var prePageCount = "20"     // Data get per page
    var imageType: Int?
    var searchMessageId: String?
    var chatGroupId: String?        // For Both chat
    var groupName: String?
    var scrollIndex: Int = 0
//    For Direct message
    var isFromDirectChat: Bool?
    var chatData: UserGroupListResponse?
    var receiverId: String?
//    From member screen
    var fromMemberScreen = false
    var profileImage: String?
    var userName: String?
    var IS_FROM_CAMERA = false
    var textViewMaxHeight: CGFloat = 80
    var textViewMinHeight: CGFloat = 38
    var totalMessage: Int = 0
    
    // MARK: - METHOD
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
        if !SocketHelper.shared.checkConnection() {
            connectSocketIOAndSendMessage(shouldLogin: true)
        } else {
            self.createRoomId()
            self.socketListenMethods()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        IQKeyboardManager.shared.enable = false
        IQKeyboardManager.shared.enableAutoToolbar = false
        self.view.endEditing(true)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.enableAutoToolbar = true
        NotificationCenter.default.removeObserver(self, name:  NSNotification.Name("GROUP_NAME_CHANGE"), object: nil)
        NotificationCenter.default.removeObserver(self, name:  NSNotification.Name("DISLIKE_MESSAGE"), object: nil)
        NotificationCenter.default.removeObserver(self, name:  NSNotification.Name("SOCKET_AGAINCONNECT"), object: nil)
        if IS_FROM_CAMERA == false {
            if isFromDirectChat == true {
                if chatData?.userId?.count ?? 0 > 2 {
                    self.roomDisconnected(receiverId: "", groupId: self.chatData?.Id ?? "")
                }else{
                    self.roomDisconnected(receiverId: self.receiverId, groupId: "")
                }
            }else{
                self.roomDisconnected(receiverId: "", groupId: self.chatGroupId)
            }
        }
    }

//    MARK: - FUNCTIONS
    func initilize(){
        self.messageTextView.isScrollEnabled = false // Disable scrolling initially
        self.textViewHeightConstraint.constant = 38
        self.messageTextView.delegate = self
//        self.createRoomId()
//        self.socketListenMethods()
        self.setUpKeyboard()
        self.notificationObserver()
        if isFromDirectChat == true{
            [self.menuButton,self.kinshipNameLabel].forEach {$0?.isHidden = true}
            [self.userNameLabel,self.userProfileImageView].forEach {$0?.isHidden = false}
            if self.chatData?.name == ""{
                self.userNameLabel.text = "You"
            }else{
                if fromMemberScreen == true{
                    self.userNameLabel.text = self.userName
                }else{
                    if chatData?.userId?.count ?? 0 > 2 {
                        self.kinshipNameLabel.isHidden = false
                        self.userNameLabel.isHidden = true
                        self.userProfileImageView.isHidden = true
                        self.kinshipNameLabel.text = self.chatData?.name
                    }else{
                        self.kinshipNameLabel.isHidden = true
                        self.userNameLabel.isHidden = false
                        self.userProfileImageView.isHidden = false
                        self.userNameLabel.text = self.chatData?.name
                    }
                }
            }
            if fromMemberScreen == true{
                Utility.setImage(self.profileImage, imageView: self.userProfileImageView)
            }else{
                Utility.setImage(self.chatData?.profileImage, imageView: self.userProfileImageView)
            }
            
            if chatData?.userId?.count ?? 0 > 2 {
                self.menuButton.isHidden = false
                self.menuButton.setImage(UIImage(named: "ic_profile"), for: .normal)
            }
        }else{
            [self.userNameLabel,self.userProfileImageView].forEach({$0?.isHidden = true})
            [self.menuButton, self.kinshipNameLabel].forEach {$0?.isHidden = false}
            
            self.kinshipNameLabel.text = UserDefaults.standard.value(forKey: "GROUP_NAME") as? String  ?? ""
            self.menuButton.setImage(UIImage(named: "ic_menu"), for: .normal)
        }
    }
    func notificationObserver(){
        NotificationCenter.default.addObserver(self, selector: #selector(self.nameChange(_:)), name: .nameChangeNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.dislike(_:)), name: .disLikeNotification, object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.appAgainActive(_:)), name: .socketAgainConnect, object: nil)
        
//        NotificationCenter.default.addObserver(self, selector: #selector(self.appAgainActive(_:)), name: .socketDisconnect, object: nil)
    }
    
    @objc func nameChange(_ notification: NSNotification){
        if let name = notification.userInfo?["name"] as? String {
            self.kinshipNameLabel.text = name
            self.sendMessage(message: name, senderId: self.senderId ?? "", receiverId: "", groupId: self.chatGroupId ?? "", type: 5, roomId: self.roomId)
        }
    }
    @objc func dislike(_ notification: NSNotification){
        if let disLikeId = notification.userInfo?["disLike"] as? Array<Any> {
//            print(disLikeId)
            if self.isFromDirectChat == true{
                self.finalDictionary.removeAll()
                if self.fromMemberScreen == true{
                    self.groupChatAPI(pageCount: "1", id: self.chatGroupId, type: 2, imageLinkType: 0)
                }else{
                    self.groupChatAPI(pageCount: "1", id: self.chatData?.Id, type: 2, imageLinkType: 0)     // DIRECT CHAT API
                }
            }else if self.isFromDirectChat == false{
                self.finalDictionary.removeAll()
                self.groupChatAPI(pageCount: "1", id: self.chatGroupId, type: 2, imageLinkType: 0)      // GROUP API
            }
        }
    }
    
    @objc func appAgainActive(_ notification: NSNotification){
        print("=== Application again active ===")
        Utility.showIndicator(view: view)
        self.finalDictionary.removeAll()
        self.groupChatTableView.reloadData()
        if isFromDirectChat == true {
            if chatData?.userId?.count ?? 0 > 2 {
                self.roomDisconnected(receiverId: "", groupId: self.chatData?.Id ?? "")
            }else{
                self.roomDisconnected(receiverId: self.receiverId, groupId: "")
            }
        }else{
            self.roomDisconnected(receiverId: "", groupId: self.chatGroupId)
        }
        print("=== Room disconnected ===")
        self.isFristFrom = true
        self.reconnectSocketIfNeeded()
    }
    
    func reconnectSocketIfNeeded() {
        if !SocketHelper.shared.checkConnection() {
            self.connectSocketIOAndSendMessage(shouldLogin: true)
        } else {
            self.createRoomId()
            self.socketListenMethods()
        }
    }
    
    @objc func socketDisconnect(_ notification: NSNotification){
        SocketHelper.shared.disconnectSocket()
    }
    
    func connectSocket(){
        if let userData = Utility.getUserData(){
//            print("userData : ", userData)
            SocketHelper.shared.connectSocket(completion: { val in
                if(val){
                    print("==== socket connected ====")
                    var parameter = [String: Any]()
                    parameter = ["senderId": Utility.getUserData()?.Id ?? ""]
                    SocketHelper.Events.UpdateStatusToOnline.emit(params: parameter)
                }else{
                    print("socket did't connected")
                }
            })
        }else{
            print("data getting nil")
        }
    }
    
    fileprivate func setUpKeyboard(){
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver( self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if ((notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue.height) != nil {
            if let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
                if messageTextView.isFirstResponder == true{
                    self.bottomContainerToSafeAreaConstraint.constant = topSafeArea == 20 ? (keyboardSize.height) : (keyboardSize.height) - 15
                }else{
                    self.messageTextView.resignFirstResponder()
                }
            }
        }
    }
    @objc func keyboardWillHide(notification: NSNotification) {
        self.bottomContainerToSafeAreaConstraint.constant = 15
    }
    
    func textViewDidChange(_ textView: UITextView) {
        var sizeToFit = messageTextView.sizeThatFits(CGSize(width: messageTextView.frame.width, height: CGFloat.greatestFiniteMagnitude))
        if sizeToFit.height < self.textViewMinHeight{
            sizeToFit.height = self.textViewMinHeight
        }
        self.textViewHeightConstraint.constant = min(sizeToFit.height, self.textViewMaxHeight)
        self.messageTextView.isScrollEnabled = sizeToFit.height > self.textViewMaxHeight
    }
        
        
        
//    MARK: - IBACTIONS
    @IBAction func onMenuBar(_ sender: Any) {
        ChatViewController = self
        if chatData?.userId?.count ?? 0 > 2 {
            self.sideMenuController?.sideMenuType = 2
            self.sideMenuController?.groupId = self.chatData?.Id
        }else{
            self.sideMenuController?.sideMenuType = 1
        }
        self.sideMenuController?.showRightView()
    }
    
    @IBAction func onBack(_ sender: Any) {
        IS_FROM_CAMERA = false
        view.endEditing(true)
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onSend(_ sender: Any) {
        if (self.messageTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).count != 0) {
            if Utility.isInternetAvailable() {
                if SocketHelper.shared.checkConnection(){
                    if isFromDirectChat == true {
                        if chatData?.userId?.count ?? 0 > 2 {
                            self.messageType(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: "", groupId: self.chatData?.Id ?? "", roomId: self.roomId)
                        }else{
                            self.messageType(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: self.receiverId ?? "", groupId: "", roomId: self.roomId)
                        }
                    }else if isFromDirectChat == false{
                        self.messageType(message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines), senderId: self.senderId ?? "", receiverId: "", groupId: self.chatGroupId ?? "", roomId: self.roomId)
                    }
                }
            }
        }
    }
    
    @IBAction func onPhotosSend(_ sender: Any) {
        self.selectProfilePictureOrCapture()
    }
}

//MARK: - EXTENSION

//MARK: - FUNCTION'S
extension ChatScreen {
    
//    MARK: - SEND MESSAGE
    func sendMessage(message: String?, senderId: String, receiverId: String, groupId: String, type: Int?, roomId: String?){
        var parameter = [String:Any]()
        //        Request parameter: { message, type: 1, senderId, receiverId, roomId, chatType }
        parameter = ["message": message ?? "",
                     "senderId": senderId,
                     "receiverId": receiverId,
                     "groupId": groupId,
                     "type": type ?? "",
                     "roomId": roomId ?? ""]
        
        print("parameter : ", parameter)
        self.messageTextView.text = ""
        SocketHelper.Events.sendMessage.emit(params: parameter)
    }
    func sendImageMessage(message: String?, senderId: String, receiverId: String, groupId: String, type: Int?, roomId: String?, chatId: String?, image: String?){
        var parameter = [String:Any]()
        
        parameter = ["message": message ?? "",
                     "senderId": senderId,
                     "receiverId": receiverId,
                     "groupId": groupId,
                     "type": type ?? "",
                     "roomId": roomId ?? "",
                     "chatId": chatId ?? "",
                     "image": image ?? ""]
        
        print("parameter : ", parameter)
        SocketHelper.Events.sendMessage.emit(params: parameter)
    }
    
    func roomDisconnected(receiverId: String?, groupId: String?) {
        var parameter = [String:Any]()
        
        parameter = ["senderId": self.senderId ?? "",
                     "receiverId": receiverId ?? "",
                     "groupId": groupId ?? "",
                     "roomId": self.roomId ?? ""]
        
        print("parameter : ", parameter)

        SocketHelper.Events.roomDisconnect.emit(params: parameter)
    }
//    MARK: - LOCALLY DATA ADD
    func addNewElementIntoDictionary(model: [ChatImageLinkMessageResponse]) {
        self.manageChatData(data: model,isFromSendMessage: true)
        self.groupChatTableView.reloadData()
        if self.finalDictionary.count >= 1 {
            self.groupChatTableView.scrollToBottom(with: true)
        }
    }
    
//   MARK: - GET ROOM ID
    func getRoomId(senderId:String, receiverId: String, groupId: String) {
        
        SocketHelper.Events.roomConnected.listen { [weak self] (result) in
            guard let self = self else { return }
            if let result = result as? String {
                self.roomId = result
                print("ROOM ID:", self.roomId ?? "")
                
                DispatchQueue.main.async {
                    if self.isFristFrom {
                        self.isFristFrom = false
                        self.finalDictionary.removeAll()
                        print("=== Room Connected ===")
                        if self.isFromDirectChat == true {
                            if self.fromMemberScreen == true {
                                self.groupChatAPI(pageCount: "1", id: self.chatGroupId, type: 2, imageLinkType: 0)
                            } else {
                                self.groupChatAPI(pageCount: "1", id: self.chatData?.Id, type: 2, imageLinkType: 0)
                            }
                        } else {
                            self.groupChatAPI(pageCount: "1", id: self.chatGroupId, type: 2, imageLinkType: 0)
                        }
                    }
                }
            } else {
                print("No match data")
            }
        }
        
        var parameter = [String:Any]()
        parameter = ["senderId":senderId,
                     "receiverId": receiverId,
                     "groupId": groupId] as [String:Any]
        print("parameter : ", parameter)
        
        SocketHelper.Events.createRoom.emit(params: parameter)
    }
    
    //    MARK: - CREATE ROOM ID
    fileprivate func createRoomId(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            if SocketHelper.shared.checkConnection() {
                if isFromDirectChat == true{    // For Direct Chat
                    if fromMemberScreen == true{
                        self.getRoomId(senderId: self.senderId ?? "", receiverId: self.receiverId ?? "", groupId: chatGroupId ?? "")
                    }else{
                        self.getRoomId(senderId: self.senderId ?? "", receiverId: self.receiverId ?? "", groupId: self.chatData?.Id ?? "")
                    }
                }else if isFromDirectChat == false{
                    self.getRoomId(senderId: self.senderId ?? "", receiverId: "", groupId: self.chatGroupId ?? "")
                }
            }else{
                self.connectSocketIOAndSendMessage(shouldLogin: true)
            }
        }else {
            Utility.showNoInternetConnectionAlertDialog(vc: self)
        }
    }
    
//    MARK: - SOCKET CONNECT AND UPDATE STATUS
    func connectSocketIOAndSendMessage(shouldLogin: (Bool)){
        if(Utility.getUserData() != nil){
            SocketHelper.shared.connectSocket(completion: { val in
                if(val){
                    print("==== socket connected ====")
                    if shouldLogin {
                        self.createRoomId()
                        self.socketListenMethods()
                    }
                    var parameter: [String: Any]
                    parameter = ["senderId": "\(self.senderId ?? "")", "receiverId" : "", "groupId": "\(self.chatGroupId ?? "")"]
                    SocketHelper.Events.UpdateStatusToOnline.emit(params: parameter)
                }else{
                    print("==== socket did't connected ====")
                    Utility.showAlert(vc: self, message: "Could not connect to the server.")
                }
                
            })
        }
    }
//    MARK: - LISTEN MESSAGE
    func socketListenMethods(){
        SocketHelper.Events.newMessage.listen { [weak self] (result) in
            do {
                let jsonData = try JSONSerialization.data(withJSONObject: result, options: .prettyPrinted)
                let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
                if let dictFromJSON = decoded as? [String:Any] {
                    
                        let newMessage = ChatImageLinkMessageResponse(Id: dictFromJSON["chatId"] as? String,
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
                                                                      createdAt:dictFromJSON["createdAt"] as? Int == nil ? Int(Utility.getTimeStampInMillisecond(date: Date())) : dictFromJSON["createdAt"] as? Int ,
                                                                      updatedAt: dictFromJSON["updatedAt"] as? Int,
                                                                      profileImage: dictFromJSON["profileImage"] as? String,
                                                                      name: dictFromJSON["name"] as? String,
                                                                      image: dictFromJSON["image"] as? String)
                    
                    if self?.isFromDirectChat == true{
                        if self?.chatData?.userId?.count ?? 0 > 2 {
                            if self?.chatGroupId == dictFromJSON["groupId"] as? String {
                                self?.addNewElementIntoDictionary(model: [newMessage])
                                self?.noMessageLabel.isHidden = true
                                self?.messageTextView.isScrollEnabled = false // Disable scrolling initially
                                self?.textViewHeightConstraint.constant = 38
                            }
                        }else{
                            if self?.senderId ?? "0" == dictFromJSON["senderId"] as? String{
                                self?.addNewElementIntoDictionary(model: [newMessage])
                                self?.noMessageLabel.isHidden = true
                                self?.messageTextView.isScrollEnabled = false // Disable scrolling initially
                                self?.textViewHeightConstraint.constant = 38
                            }
                            else if self?.senderId ?? "0" == dictFromJSON["receiverId"] as? String{
                                self?.addNewElementIntoDictionary(model: [newMessage])
                                self?.noMessageLabel.isHidden = true
                                self?.messageTextView.isScrollEnabled = false // Disable scrolling initially
                                self?.textViewHeightConstraint.constant = 38
                            }
                        }
                    }else if self?.isFromDirectChat == false{
                        if self?.chatGroupId == dictFromJSON["groupId"] as? String {
                            self?.addNewElementIntoDictionary(model: [newMessage])
                            self?.noMessageLabel.isHidden = true
                            self?.messageTextView.isScrollEnabled = false // Disable scrolling initially
                            self?.textViewHeightConstraint.constant = 38
                        }
                    }
                }
            } catch {
                print(error.localizedDescription)
            }
        }
   }
    func selectProfilePictureOrCapture() {
        
        let alert = UIAlertController(title: APPLICATION_NAME , message: "Please Select an Option", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Library", style: .default , handler:{ (UIAlertAction)in
            ImagePicker.photoLibrary(target: self, edit: true)
        }))
        
        alert.addAction(UIAlertAction(title: "Camera", style: .default , handler:{ (UIAlertAction)in
            self.IS_FROM_CAMERA = true
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
//    MARK: - MANAGE CHAT DATA
    func manageChatData(data: Array<ChatImageLinkMessageResponse>,isFromSendMessage:Bool = false, isGroupChat: Bool = false){
        
        var dateArray = Array<Any>()
        if self.chatMetaDetails?.currentPage == 1 && isFromSendMessage == false{
            dateArray = Array(Set(data.compactMap({ Utility.setTimeToDateConvert(timestamp: "\($0.createdAt ?? 0)", dateFormate: MM_DD_YYYY)}))).sorted().compactMap({ Utility.dateToDateString(dateString: $0)})
        }else{
            dateArray = Array(Set(data.compactMap({ Utility.setTimeToDateConvert(timestamp: "\($0.createdAt ?? 0)", dateFormate: MM_DD_YYYY)}))).sorted().reversed().compactMap({ Utility.dateToDateString(dateString: $0)})
        }
        let dateValue = self.finalDictionary.compactMap({$0.headerDate}).reversed()
        
        for i in dateArray{     // Set a data accoding to date
//            print(i)
            let filterArray = data.filter({Utility.setTimeToTimeZone(timestamp: "\($0.createdAt ?? 0)") == i as! String})
            
            if self.chatMetaDetails?.currentPage == 1 && isFromSendMessage == false{
                self.finalDictionary.append(SectionMessageData(headerDate: i as? String, messageData: filterArray.reversed()))
            } else if isFromSendMessage == true{
                if i as? String? == dateValue.first {
                    guard let obj = finalDictionary.last else {return}
                    if isFromSendMessage == true{
                        obj.messageData?.append(contentsOf: filterArray)
                    }else{
                        obj.messageData?.insert(contentsOf: filterArray.reversed(), at: 0)
                    }
                    self.finalDictionary[finalDictionary.count - 1] = obj
                }else {
                     self.finalDictionary.append(SectionMessageData(headerDate: i as? String, messageData: filterArray.reversed()))
                }
            }else{
                let dicDate = self.finalDictionary.compactMap({$0.headerDate})
                
                if i as? String? == dicDate.first {
                    for i in 0...finalDictionary.count-1{
                        let obj = finalDictionary[i]
                        if isFromSendMessage == true{
                            obj.messageData?.append(contentsOf: filterArray)
                        } else {
                            if isGroupChat == false {
                                obj.messageData?.insert(contentsOf: filterArray.reversed(), at: 0)
                            } else {
                                obj.messageData?.insert(contentsOf: filterArray.reversed(), at: 0)
                            }
                        }
                        self.finalDictionary[i] = obj
                    }
                } else {
                    self.finalDictionary.insert(SectionMessageData(headerDate: i as? String, messageData: filterArray.reversed()), at: 0)
                }
            }
        }
    }
    
    func messageType(message: String?, senderId: String, receiverId: String, groupId: String, roomId: String?){
        if Utility.verifyUrl(urlString: message) || self.messageTextView.text.contains("www."){
            self.sendMessage(message: message, senderId: senderId, receiverId: receiverId, groupId: groupId, type: 3, roomId: roomId)
        }else{
            if self.messageTextView.text.contains(" "){
                if let linkOrSimpleMessage = self.messageTextView.text{
                    let myArr1 = linkOrSimpleMessage.components(separatedBy: " ")
                    let value = myArr1.map { Utility.verifyUrl(urlString: $0) }
                    if value.contains(true) {
                        self.sendMessage(message: message, senderId: senderId, receiverId: receiverId, groupId: groupId, type: 3, roomId: roomId)
                    }else{
                        self.sendMessage(message: message, senderId: senderId, receiverId: receiverId, groupId: groupId, type: 1, roomId: roomId)
                    }
                }
            }else{
                self.sendMessage(message: message, senderId: senderId, receiverId: receiverId, groupId: groupId, type: 1, roomId: roomId)
            }
        }
    }
    
    func imageTyepe(geoupId: String?, receiverId: String?) -> photosSendRequest{
        var parameter = photosSendRequest()
        if self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines).count == 0{
            parameter = photosSendRequest(groupId: geoupId, receiverId: receiverId, type: "2", message: "")
            self.imageType = 2
        }else{
            parameter = photosSendRequest(groupId: geoupId, receiverId: receiverId, type: "4", message: self.messageTextView.text.trimmingCharacters(in: .whitespacesAndNewlines))
            self.imageType = 4
        }
        return parameter
    }
}


//MARK: - TABLEVIEW DELEGATE
extension ChatScreen: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return finalDictionary.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.scrollIndex = finalDictionary[section].messageData?.count ?? 0
        return finalDictionary[section].messageData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let mainArray = finalDictionary[indexPath.section].messageData?[indexPath.row]
        if mainArray?.type == 5{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GroupNameChange") as! GroupNameChange
            cell.nameChamgeMessageLabel.text = mainArray?.message
            return cell
        }else{
            let cell = tableView.dequeueReusableCell(withIdentifier: "GroupChatCell") as! GroupChatCell
            if self.isFromDirectChat == true{
                cell.isFromDirectMessageScreen = true
            }else if self.isFromDirectChat == false {
                cell.isFromDirectMessageScreen = false
            }
            cell.chatData = mainArray
            cell.likeOrNot = { value in
            }
            cell.image = { value in
                let vc = STORYBOARD.home.instantiateViewController(identifier: "ImageOpenScreen") as! ImageOpenScreen
                vc.imageData = mainArray?.image
                self.navigationController?.pushViewController(vc, animated: true)
            }
            cell.link = { link in
                var velidUrl: String
                if link.starts(with: "www.") {
                    velidUrl = "https://" + link
                }else{
                    velidUrl = link
                }
                guard let urlLink = URL(string: velidUrl) else{ return }
                let sfSafari = SFSafariViewController(url: urlLink)
                self.present(sfSafari, animated: true)
            }
            return cell
        }
    }
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DateCell") as! DateCell
        cell.item = finalDictionary[section].headerDate
        return cell
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if (50 >= scrollView.contentOffset.y) {
            if self.chatMetaDetails?.lastPage != self.chatMetaDetails?.currentPage && self.chatMetaDetails?.lastPage != 0 {
                if self.isFromDirectChat == true{
                    self.totalMessage = self.finalDictionary[0].messageData?.count ?? 0
                    self.groupChatAPI(pageCount: "\((self.chatMetaDetails?.currentPage as? Int ?? 0) + 1)", id: self.chatData?.Id, type: 2, imageLinkType: 0, isPaggination: true)
                } else {
                    self.totalMessage = self.finalDictionary[0].messageData?.count ?? 0
                    self.groupChatAPI(pageCount: "\((self.chatMetaDetails?.currentPage as? Int ?? 0) + 1)", id: self.chatGroupId, type: 2, imageLinkType: 0, isPaggination: true)
                }
            }
        }
    }
}
//MARK: - IMAGE PICKER DELEGATE
extension ChatScreen: UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        IS_FROM_CAMERA = true
            if let image = info[.editedImage] as? UIImage {
                if let imgData = Utility.getCompressedImageData(image) {
                    self.imageData = imgData
                } else {
                    let imageData = image.jpegData(compressionQuality:0.8)!
                    self.imageData = imageData
                }
            }
        self.photosSendAPI()
        self.view.endEditing(true)
        self.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        IS_FROM_CAMERA = true
        self.dismiss(animated: true, completion: nil)
    }
    
}


//MARK: - API CALL
extension ChatScreen {
//   MESSAGE API
    func groupChatAPI( pageCount: String?, id: String?, type: Int?, imageLinkType: Int?, isPaggination: Bool = false){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = ChatImageLinkMessageList(id: id, type: type, imageLinkType: imageLinkType,page: pageCount, perPage: self.prePageCount)
            DirectMessageServices.shared.getImageLinkMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                guard let self = self else {return}
//                Utility.hideIndicator()
                if let flag = response?.flag {
                    if flag == true {
                        Utility.hideIndicator()
                        if isFromDirectChat == false {
                            let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "groupInformationScreen") as! groupInformationScreen
                            vc.modalPresentationStyle = .custom
                            vc.modalTransitionStyle = .crossDissolve
                            self.present(vc, animated: true, completion: nil)
                        }
                    }
                }
                if let meta = response?.chatImageLinkMessageMetaResponse {      // Manage meta
                    self.chatMetaDetails = meta
                }
                if let data = response?.chatImageLinkMessageResponse {
                    var isGroupChatorNot: Bool?
//                    self.chatGroupId = data.first?.groupId ?? ""
                    if isFromDirectChat == true{
                        if chatData?.userId?.count ?? 0 > 2{
                            isGroupChatorNot = true
                        }else{
                            isGroupChatorNot = false
                        }
                    }else{
                        isGroupChatorNot = true
                    }
                    print("=== Get all the data from API ===")
                    self.manageChatData(data: data, isGroupChat: isGroupChatorNot ?? true)    // Manage all chat data
                    if data.count == 0{
                        if isFromDirectChat == true{
                            self.noMessageLabel.isHidden = false
                            if chatData?.userId?.count ?? 0 > 2{
                                self.noMessageLabel.text = "No message in this group yet"
                            }else{
                                self.noMessageLabel.text = "No message in this chat yet"
                            }
                        }else{
                            self.noMessageLabel.isHidden = false
                            self.noMessageLabel.text = "No message in this group yet"
                        }
                    }else{
                        self.noMessageLabel.isHidden = true
                    }
                    
                    Utility.hideIndicator()
                    let oldContentHeight: CGFloat = self.groupChatTableView.contentSize.height
                    let oldOffsetY: CGFloat = self.groupChatTableView.contentOffset.y
                    self.groupChatTableView.reloadData()
                    if isPaggination == false {
                        if chatMetaDetails?.currentPage == 1{
                            if self.finalDictionary.count >= 1 {
                                self.groupChatTableView.scrollToBottom(with: true)
                            }
                        }
                    } else {
                        
                        let newContentHeight: CGFloat = self.groupChatTableView.contentSize.height
                        self.groupChatTableView.contentOffset.y = oldOffsetY + (newContentHeight - oldContentHeight)
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
//    MARK: - IMAGE API
    func photosSendAPI(){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            var parameter = photosSendRequest()
            if isFromDirectChat == true {
                if chatData?.userId?.count ?? 0 > 2 {
                    parameter = self.imageTyepe(geoupId: self.chatData?.Id, receiverId: self.receiverId)
                }else{
                    if fromMemberScreen == true{
                        parameter = self.imageTyepe(geoupId: self.chatGroupId, receiverId: self.receiverId)
                    } else {
                        parameter = self.imageTyepe(geoupId: self.chatData?.Id, receiverId: self.receiverId)
                    }
                }
            }else if isFromDirectChat == false{
                parameter = self.imageTyepe(geoupId: self.chatGroupId, receiverId: "")
            }
//            print(parameter.toJSON())
            DirectMessageServices.shared.photosSend(parameters : parameter.toJSON() ,imageData: self.imageData ) { [weak self] (statusCode, response) in
                print(response?.toJSON() as Any)
                Utility.hideIndicator()
                if let data = response?.photosSendResponse{
                    if SocketHelper.shared.checkConnection() {
                        if self?.isFromDirectChat == true {
                            if self?.chatData?.userId?.count ?? 0 > 2 {
                                self?.sendImageMessage(message: data.message, senderId: data.senderId ?? "", receiverId: "", groupId: self?.chatData?.Id ?? "", type: self?.imageType, roomId: self?.roomId, chatId: data.Id, image: data.image)
                            }else{
                                self?.sendImageMessage(message: data.message, senderId: data.senderId ?? "", receiverId: self?.receiverId ?? "", groupId: "", type: self?.imageType, roomId: self?.roomId, chatId: data.Id, image: data.image)
                            }
                        }else if self?.isFromDirectChat == false{
                            self?.sendImageMessage(message: data.message, senderId: data.senderId ?? "", receiverId: "", groupId: self?.chatGroupId ?? "", type: self?.imageType, roomId: self?.roomId, chatId: data.Id, image: data.image)
                        }
//                        self?.messageTextView.text = ""
                        self?.noMessageLabel.isHidden = true
//                        self?.addNewElementIntoDictionary(model: [newImageMessage])
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

//@objc private func handleSocketReconnection() {
//    // Reconnect room when socket reconnects
//    if let senderId = self.senderId {
//        self.getRoomId(
//            senderId: senderId,
//            receiverId: self.receiverId ?? "",
//            groupId: self.chatGroupId ?? ""
//        )
//    }
//}
