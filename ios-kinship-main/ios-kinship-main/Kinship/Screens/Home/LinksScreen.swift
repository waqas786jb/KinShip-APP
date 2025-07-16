//
//  LinksScreen.swift
//  Kinship
//
//  Created by iMac on 24/04/24.
//

import UIKit
import SafariServices


class LinksScreen: UIViewController {
    
//    MARK: - VARIABLES
    var linkData: [ChatImageLinkMessageResponse] = []
    let isFetchMetadataDynamic = true
    
    
//    MARK: - IB OUTLETS
    @IBOutlet weak var noLinkLabel: UILabel!
    @IBOutlet weak var linksCollectionView: UICollectionView!{
        didSet{
            self.linksCollectionView.register(UINib(nibName: "LinksCell", bundle: nil), forCellWithReuseIdentifier: "LinksCell")
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.linksAPI( pageCount: "1")
    }
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}
//MARK: - COLLECTION VIEW DELEGATE
extension LinksScreen: UICollectionViewDelegate, UICollectionViewDataSource{
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        self.linkData.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "LinksCell", for: indexPath) as! LinksCell
        cell.item = self.linkData[indexPath.row]
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        guard let link = URL(string: self.linkData[indexPath.row].message ?? "") else{ return }
        let sfSafari = SFSafariViewController(url: link)
        present(sfSafari, animated: true)
    }
}
extension LinksScreen: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: linksCollectionView.frame.width/2-8, height: 122)
    }
}

//MARK: - API CALL
extension LinksScreen {
    func linksAPI( pageCount: String?){
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            let param = ChatImageLinkMessageList(id: CHAT_GROUP_ID, type: 1, imageLinkType: 2,page: pageCount, perPage: "20")
            DirectMessageServices.shared.getImageLinkMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let data = response?.chatImageLinkMessageResponse {
//                    print(data.toJSON())
                    self?.linkData = data
                    if self?.linkData.count == 0{
                        self?.noLinkLabel.isHidden = false
                    }else{
                        self?.noLinkLabel.isHidden = true
                    }
                    self?.linksCollectionView.reloadData()
                }
            } failure: { (error) in
                Utility.hideIndicator()
                Utility.showAlert(vc: self, message: error)
            }
        }
    }
}
