//
//  SearchMessageScreen.swift
//  Kinship
//
//  Created by iMac on 28/05/24.
//

import UIKit

class SearchMessageScreen: UIViewController {
    
//    MARK: - VARIABELS
    var searchData: [ChatImageLinkMessageResponse] = []
    var searchMessageMetaDetails: ChatImageLinkMessageMetaResponse?
    
//    MARK: - IB OUTLETS
    @IBOutlet weak var searchTextField: UITextField!
    @IBOutlet weak var searchLabel: UILabel!
    @IBOutlet weak var searchMessageTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "SearchMessageCell", bundle: nil)
            searchMessageTableView.register(nib, forCellReuseIdentifier: "SearchMessageCell")
        }
    }
    var timer: Timer?

    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
    }
    
    func initilize() {
        searchTextField.addTarget(self, action: #selector(SearchMessageScreen.textFieldDidChange(_:)), for: .editingChanged)
    }

    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @objc func textFieldDidChange(_ textField: UITextField) {
        self.timer?.invalidate()  // Cancel any previous timer
        self.timer = Timer.scheduledTimer(timeInterval: 0.5, target: self, selector: #selector(performSearch), userInfo: nil, repeats: false)
    }
    
    @objc func performSearch() {
        if self.searchTextField.text?.count ?? 0 != 0 {
            self.searchAPICall(pageCount: "1", searchText: self.searchTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines))
        }else{
            self.searchData.removeAll()
            self.searchMessageTableView.reloadData()
        }
    }
}
//  MARK: - TABLEVIEW DELEGATE
extension SearchMessageScreen: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.searchData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SearchMessageCell", for: indexPath) as! SearchMessageCell
        cell.labelText = self.searchTextField.text
        cell.searchData = self.searchData[indexPath.row]
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = STORYBOARD.home.instantiateViewController(withIdentifier: "SearchMessageDisplayScreen") as! SearchMessageDisplayScreen
        vc.messageId = self.searchData[indexPath.row].Id
        vc.groupId = self.searchData[indexPath.row].groupId
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
//  MARK: - API CALL
extension SearchMessageScreen {
    func searchAPICall(pageCount: String?, searchText: String?) {
        if Utility.isInternetAvailable() {
            let param = LikeMessageLinkResponse(id: CHAT_GROUP_ID, type: 2, imageLinkType: 0,search: searchText, page: pageCount, perPage: "20")
//            print(param.toJSON())
            GroupChatServices.shared.likeMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                
                if let meta = response?.chatImageLinkMessageMetaResponse {       // Meta response
                    self?.searchMessageMetaDetails = meta
                }
                if let data = response?.chatImageLinkMessageResponse {      // Data response
                    self?.searchData = data
                    if self?.searchData.count == 0{
                        self?.searchLabel.text = "No search results found"
                        self?.searchLabel.isHidden = false
                    }else{
                        self?.searchLabel.isHidden = true
                    }
                    self?.searchMessageTableView.reloadData()
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
