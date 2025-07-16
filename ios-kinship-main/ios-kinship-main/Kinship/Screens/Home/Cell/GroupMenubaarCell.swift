//
//  GroupMenubaarCell.swift
//  Kinship
//
//  Created by iMac on 14/06/24.
//

import UIKit

class GroupMenubaarCell: UITableViewCell {
    
//    MARK: - IB-OUTLET
    @IBOutlet weak var logoImageView: UIImageView!
    @IBOutlet weak var funcnalityName: UILabel!
    @IBOutlet weak var arrowImageView: UIImageView!
    

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    var menuData: funcanalityName? {
        didSet{
            self.logoImageView.image = UIImage(named: self.menuData?.logoImage ?? "")
            self.funcnalityName.text = menuData?.name
        }
    }
}
