//
//  SearchMessageCell.swift
//  Kinship
//
//  Created by iMac on 28/05/24.
//

import UIKit

class SearchMessageCell: UITableViewCell {

//    MARK: - IB OUTLETS
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var messageLabel: UILabel!
    @IBOutlet weak var profileImageView: dateSportImageView!
    
//    MARK: - VARIABLES
    var labelText: String?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
//        self.manageSearch()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    var searchData: ChatImageLinkMessageResponse?{
        didSet{
            self.nameLabel.text = searchData?.name
            self.messageLabel.text = searchData?.message
            Utility.setImage(searchData?.profileImage, imageView: self.profileImageView)
            
            let convertAttributedText = NSMutableAttributedString(string: self.messageLabel.text?.lowercased() ?? "")
            let defaultTextColour = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)]
            
            convertAttributedText.addAttributes(defaultTextColour, range: NSRange(location: 0, length: convertAttributedText.length))
            
            var range = ((self.messageLabel.text?.lowercased() as? NSString ?? "") as NSString).range(of: self.labelText ?? "", options: .caseInsensitive)
                
            let attributedText = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)]
            
            while range.location != NSNotFound {
            convertAttributedText.addAttributes(attributedText, range: range)
            let nextRangeStart = range.location + range.length
                range = (self.messageLabel.text! as NSString).range(of: self.labelText ?? "", options: .caseInsensitive, range: NSRange(location: nextRangeStart, length: (self.messageLabel.text?.utf16.count ?? 0) - nextRangeStart))
            }
            
            convertAttributedText.addAttributes(attributedText, range: range)
            self.messageLabel.attributedText = convertAttributedText
        }
    }
    
    
}
extension NSMutableAttributedString{
    func setColorForText(_ textToFind: String?, with color: UIColor) {
        let range:NSRange?
        if let text = textToFind{
            range = self.mutableString.range(of: text, options: .caseInsensitive)
        }else{
            range = NSMakeRange(0, self.length)
        }
        if range!.location != NSNotFound {
            addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: range!)
        }
    }
}
