//
//  LinksCell.swift
//  Kinship
//
//  Created by iMac on 28/05/24.
//

import UIKit
import LinkPresentation
//import URLLivePreview
import SDWebImage

class LinksCell: UICollectionViewCell {

    var isFetchMetadataDynamic = true
    
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var EventNameLabel: UILabel!
    @IBOutlet weak var linkBtton: UILabel!
    
    //    private var metadataTask: URLSessionTask?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        imageView.clipsToBounds = true
        imageView.layer.cornerRadius = 5
        imageView.layer.maskedCorners = [.layerMinXMinYCorner,.layerMaxXMinYCorner]
    }
    override func prepareForReuse() {
        super.prepareForReuse()
//        metadataTask?.cancel()
//        self.imageView.image = nil
    }
    
    var item: ChatImageLinkMessageResponse?{
        didSet{
            if item?.name == nil{
                self.EventNameLabel.text = "You"
            }else{
                self.EventNameLabel.text = item?.name
            }
            if let linkOrSimpleMessage = item?.message{
                let myArr1 = linkOrSimpleMessage.components(separatedBy: " ")
                let value = myArr1.filter { Utility.verifyUrl(urlString: $0) || $0.contains("www")}
//                print(myArr1)
//                print(value)
                self.linkBtton.text = value.first
            }
//            self.linkBtton.text = item?.message
            self.setThumbnailImage(urlLink: "\(item?.message ?? "")", yourImageView: self.imageView)
        }
    }
}
extension LinksCell {
    func setThumbnailImage(urlLink: String, yourImageView: UIImageView) {
        guard let url = URL(string: urlLink) else {
            yourImageView.image = UIImage(named: "ic_link_Thumbnail")
            return
        }
        yourImageView.sd_setImage(with: url, placeholderImage: UIImage(named: "ic_link_Thumbnail"),options: .retryFailed) { image, error, cacheType, url in
            if let error = error {
                self.setImage(urlLink: urlLink, yourImageView: yourImageView)
//                print("Error fetching image: \(error.localizedDescription)")
                return
            }

            //        metadataTask?.cancel()
            if image == nil{
                let metadataProvider = LPMetadataProvider()
                metadataProvider.startFetchingMetadata(for: url!) { (linkMetadata, error) in
                    guard let linkMetadata = linkMetadata, error == nil else {
                        DispatchQueue.main.async {
                            yourImageView.image = UIImage(named: "ic_link_Thumbnail")
                        }
                        if let error = error {
                            print("Error fetching metadata: \(error.localizedDescription)")
                        }
                        return
                    }
                    
                    print(linkMetadata.title ?? "Untitled")
                    
                    guard let imageProvider = linkMetadata.iconProvider else {
                        DispatchQueue.main.async {
                            yourImageView.image = UIImage(named: "ic_link_Thumbnail")
                        }
                        print("No image provider available")
                        return
                    }
                    
                    imageProvider.loadObject(ofClass: UIImage.self) { (image, error) in
                        guard error == nil else {
                            DispatchQueue.main.async {
                                yourImageView.image = UIImage(named: "ic_link_Thumbnail")
                            }
                            print("Error loading image: \(error!.localizedDescription)")
                            return
                        }
                        
                        if let image = image as? UIImage {
                            DispatchQueue.main.async {
                                yourImageView.image = image
                                SDImageCache.shared.store(image, forKey: urlLink, completion: nil)
                            }
                        } else {
                            DispatchQueue.main.async {
                                yourImageView.image = UIImage(named: "ic_link_Thumbnail")
                            }
                            print("No image available")
                        }
                    }
                }
            }
        }
    }
    
    func setImage(urlLink: String, yourImageView: UIImageView) {
        let url = URL(string: "\(urlLink)")!
        
        LPMetadataProvider().startFetchingMetadata(for: url) { (linkMetadata, error) in
            guard let linkMetadata = linkMetadata, let imageProvider = linkMetadata.iconProvider else {
                return
            }
            
            print(linkMetadata.title ?? "Untitled")
            
            imageProvider.loadObject(ofClass: UIImage.self) { (image, error) in
                guard error == nil else {
                    // handle error
                    return
                }
                
                if let image = image as? UIImage {
                    // do something with image
                    DispatchQueue.main.async {
                        yourImageView.image = image
                        SDImageCache.shared.store(image, forKey: urlLink, completion: nil)
                        //                        Utility.setImage(image, imageView: self.imageView)
                    }
                    
                } else {
                    print("no image available")
                }
            }
        }
    }
}
