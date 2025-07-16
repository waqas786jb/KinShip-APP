//
//  BringYouCell.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import UIKit

class BringYouCell: UITableViewCell {
    
    //MARK: - Outlets
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var tickView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    var bringYouModel: BringYouModel?{
        didSet{
            self.titleLabel.text = bringYouModel?.title ?? ""
        }
    }
}
