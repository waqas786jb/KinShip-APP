//
//  SearchMessageDisplayScreen.swift
//  Kinship
//
//  Created by iMac on 12/06/24.
//

import UIKit
import SafariServices
import SwiftUI

class SearchMessageDisplayScreen: UIViewController {
    
//    MARK: - VARIABLES
    var messageId: String?
    var groupId: String?
    var searchData: [ChatImageLinkMessageResponse]?
    
   
//    MARK: - IB-OUTLET
    @IBOutlet weak var searchTableView: UITableView!{
        didSet{
            let nib = UINib(nibName: "GroupChatCell", bundle: nil)
            searchTableView.register(nib, forCellReuseIdentifier: "GroupChatCell")
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.searchDisaplay()
        // Do any additional setup after loading the view.
    }
    
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func onMenu(_ sender: Any) {
        ChatViewController = self
        self.sideMenuController?.sideMenuType = 1
        self.sideMenuController?.showRightView()
    }
}
//MARK: - TABLEVIEW DELEGATE
extension SearchMessageDisplayScreen: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.searchData?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "GroupChatCell") as! GroupChatCell
        cell.chatData = self.searchData?[indexPath.row]
        cell.image = { value in
            let vc = STORYBOARD.home.instantiateViewController(identifier: "ImageOpenScreen") as! ImageOpenScreen
            vc.imageData = self.searchData?[indexPath.row].image
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

// MARK: - API CALL
extension SearchMessageDisplayScreen {
    func searchDisaplay() {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = SearchDisplayResponse(id: self.messageId, groupId: self.groupId)
            GroupChatServices.shared.searchResultList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.chatImageLinkMessageResponse {
                    self?.searchData = data
                    self?.searchTableView.reloadData()
                    let index = data.firstIndex(where: { $0.Id ?? "" == self?.messageId })
                    let indexPath = IndexPath(row: index ?? 0, section: 0)
                    self?.searchTableView.scrollToRow(at: indexPath, at: .middle, animated: true)
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
