//
//  GalleryScreen.swift
//  Kinship
//
//  Created by iMac on 24/04/24.
//

import UIKit

class GalleryScreen: UIViewController {

//    MARK: - IBOUTLETS
    @IBOutlet weak var galleryCollectionView: UICollectionView!{
        didSet{
            self.galleryCollectionView.register(UINib(nibName: "GalleryCell", bundle: nil), forCellWithReuseIdentifier: "GalleryCell")
        }
    }
    @IBOutlet weak var noImageLabel: UILabel!
    
    
    var imageData: [ChatImageLinkMessageResponse] = []
    var imageMetaData: ChatImageLinkMessageMetaResponse?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initilize()
        // Do any additional setup after loading the view.
    }
    
//    MARK: - FUNCTION'S
    func initilize() {
        self.galleryImageAPI(page: "1")
    }
//    MARK: - ACTION METHOD
    @IBAction func onBack(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
}

//  MARK: - COLLECTION VIEW DELEGATE
extension GalleryScreen: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        self.imageData.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "GalleryCell", for: indexPath) as! GalleryCell
        cell.imageData = self.imageData[indexPath.row]
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let vc = STORYBOARD.home.instantiateViewController(identifier: "ImageOpenScreen") as! ImageOpenScreen
        vc.imageData = self.imageData[indexPath.row].image
        self.navigationController?.pushViewController(vc, animated: true)
    }
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        if ((scrollView.contentOffset.y + scrollView.frame.size.height) >= scrollView.contentSize.height - 5) {
            if self.imageMetaData?.lastPage != self.imageMetaData?.currentPage {
                self.galleryImageAPI(page: "\((self.imageMetaData?.currentPage as? Int ?? 0) + 1)")
            }
        }
    }
}

extension GalleryScreen: UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: galleryCollectionView.frame.width/3-3, height: 105)
    }
}

//MARK: - API CALLING
extension GalleryScreen {
    func galleryImageAPI(page: String?) {
        if Utility.isInternetAvailable() {
            Utility.showIndicator(view: view)
            
            let param = ChatImageLinkMessageList(id: CHAT_GROUP_ID, type: 1, imageLinkType: 1, page: page, perPage: "24")
//            print(param.toJSON())
            DirectMessageServices.shared.getImageLinkMessageList(parameters: param.toJSON()) { [weak self] (statusCode, response) in
                Utility.hideIndicator()
                if let meta = response?.chatImageLinkMessageMetaResponse {
                    self?.imageMetaData = meta
                }
                
                if let data = response?.chatImageLinkMessageResponse {
                    self?.imageData.append(contentsOf: data)
                    if self?.imageData.count ?? 0 == 0{
                        self?.noImageLabel.isHidden = false
                    }else{
                        self?.noImageLabel.isHidden = true
                    }
                    self?.galleryCollectionView.reloadData()
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
