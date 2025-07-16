//
//  LikeScreen.swift
//  Kinship
//
//  Created by iMac on 27/05/24.
//

import UIKit
import SafariServices

class LikeScreen: UIViewController {
    
//    MARK: - VARIABELS
    var personChatData: [ChatImageLinkMessageResponse] = []
    var likeMessageMetaDetails: ChatImageLinkMessageMetaResponse?
    var dislikeId: [String] = []
    
//MARK: - IB OUTLETS
    @IBOutlet weak var noLikeMessageLabel: UILabel!
    @IBOutlet weak var likeMessageTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "GroupChatCell", bundle: nil)
            likeMessageTableView.register(nib, forCellReuseIdentifier: "GroupChatCell")
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.likeAPICall(pageCount: "1")
    }
    @IBAction func onBack(_ sender: Any) {
        let disLikeId:[String: Array<Any>] = ["disLike": self.dislikeId]
        view.endEditing(true)
        NotificationCenter.default.post(name: .disLikeNotification, object: nil, userInfo: disLikeId)
        self.navigationController?.popViewController(animated: true)
    }
}
//MARK: - TABLEVIE DELEGATE
extension LikeScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.personChatData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "GroupChatCell", for: indexPath) as! GroupChatCell
        cell.isFromLikeScreen = true
        cell.chatData = self.personChatData[indexPath.row]
        cell.image = { value in
            let vc = STORYBOARD.home.instantiateViewController(identifier: "ImageOpenScreen") as! ImageOpenScreen
            vc.imageData = value
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
        
        cell.disLikeMessage = { [weak self] messageId in
            self?.dislikeId.append(messageId)
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
            if Utility.verifyUrl(urlString: personChatData[indexPath.row].message ?? "") {
                guard let iroid = URL(string: personChatData[indexPath.row].message ?? "") else{ return }
                let sfSafari = SFSafariViewController(url: iroid)
                present(sfSafari, animated: true)
            }
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        if ((scrollView.contentOffset.y + scrollView.frame.size.height) >= scrollView.contentSize.height - 5) {
            if self.likeMessageMetaDetails?.lastPage != self.likeMessageMetaDetails?.currentPage {
                self.likeAPICall(pageCount: "\((self.likeMessageMetaDetails?.currentPage as? Int ?? 0) + 1)")
            }
        }
    }
}

//  MARK: - API CALL
extension LikeScreen {
    func likeAPICall(pageCount: String?) {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = LikeMessageLinkResponse(id: CHAT_GROUP_ID, type: 3, imageLinkType: 0, page: pageCount, perPage: "20")
//            print(param.toJSON())
            GroupChatServices.shared.likeMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                
                if let meta = response?.chatImageLinkMessageMetaResponse {       // Meta response
                    self?.likeMessageMetaDetails = meta
                }
                
                if let data = response?.chatImageLinkMessageResponse {      // Data response
                    self?.personChatData.append(contentsOf: data)
                    if self?.personChatData.count == 0 {
                        self?.noLikeMessageLabel.isHidden = false
                    }else{
                        self?.noLikeMessageLabel.isHidden = true
                    }
                    self?.likeMessageTableView.reloadData()
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
}
