//
//  TableViewExtension.swift
//  Kinship
//
//  Created by iMac on 13/06/24.
//

import Foundation
import UIKit

extension UITableView {

    func hasRowAtIndexPath(indexPath: IndexPath) -> Bool {
        return indexPath.section < self.numberOfSections && indexPath.row < self.numberOfRows(inSection: indexPath.section)
    }
    
    func scrollToBottom(with Animation: Bool){
            DispatchQueue.main.async {
                let indexPath = IndexPath(
                    row: self.numberOfRows(inSection:  self.numberOfSections-1) - 1,
                    section: self.numberOfSections - 1)
                if self.hasRowAtIndexPath(indexPath: indexPath) {
                    self.scrollToRow(at: indexPath, at: .bottom, animated: false)
                }
            }
    }
    func scrollToIndexPath(with Animation: Bool, indexPath: IndexPath){
            DispatchQueue.main.async {
                if self.hasRowAtIndexPath(indexPath: indexPath) {
                    self.scrollToRow(at: indexPath, at: .bottom, animated: false)
                }
            }
    }
}
