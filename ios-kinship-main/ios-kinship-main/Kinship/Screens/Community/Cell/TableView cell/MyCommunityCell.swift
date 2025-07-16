//
//  MyCommunityCell.swift
//  Kinship
//
//  Created by iMac on 28/10/24.
//

import UIKit

class MyCommunityCell: UITableViewCell {

    // MARK: - IB Outlet
    @IBOutlet weak var communityName: UILabel!
    @IBOutlet weak var memberLabel: UILabel!
    @IBOutlet weak var messageCountLabel: dateSportLabel!
    
    // MARK: - Variable
    var labelText: String?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    var data: MyCommunityResponse? {
        didSet{
            self.communityName.text = data?.name
            self.memberLabel.text = "\(data?.members ?? 0) Members"
            if data?.unseenCount == 0 {
                self.messageCountLabel.isHidden = true
            } else {
                self.messageCountLabel.isHidden = false
                self.messageCountLabel.text = "\(data?.unseenCount ?? 0)"
            }
        }
    }
    
    var exploreCommunityData: ExploreCommunity? {
        didSet{
            self.messageCountLabel.isHidden = true
            self.communityName.text = exploreCommunityData?.name
            self.memberLabel.text = "\(exploreCommunityData?.members ?? 0) Members"
            
            let convertAttributedText = NSMutableAttributedString(string: exploreCommunityData?.name?.lowercased() ?? "")
            let defaultTextColour = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)]
            
            convertAttributedText.addAttributes(defaultTextColour, range: NSRange(location: 0, length: convertAttributedText.length))
            
            var range = ((exploreCommunityData?.name?.lowercased() as? NSString ?? "") as NSString).range(of: self.labelText ?? "", options: .caseInsensitive)
                
            let attributedText = [NSAttributedString.Key.foregroundColor: #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)]
            
            while range.location != NSNotFound {
            convertAttributedText.addAttributes(attributedText, range: range)
            let nextRangeStart = range.location + range.length
                range = (self.communityName.text! as NSString).range(of: self.labelText ?? "", options: .caseInsensitive, range: NSRange(location: nextRangeStart, length: (self.communityName.text?.utf16.count ?? 0) - nextRangeStart))
            }
            
            convertAttributedText.addAttributes(attributedText, range: range)
            self.communityName.attributedText = convertAttributedText
        }
    }
}
