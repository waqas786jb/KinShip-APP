//
//  DateCell.swift
//  Kinship
//
//  Created by iMac on 06/05/24.
//

import UIKit

class DateCell: UITableViewCell {

    @IBOutlet weak var dateTimeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    var item:String?{
        didSet{
            self.dateTimeLabel.text = item
            if item == nil{
                self.dateTimeLabel.isHidden = true
            }else{
                self.dateTimeLabel.isHidden = false
            }
        }
    }
}
