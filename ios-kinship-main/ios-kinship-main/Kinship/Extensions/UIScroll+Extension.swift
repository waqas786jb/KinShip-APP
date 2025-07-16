//
//  UIScroll+Extension.swift
//  So-Fetch
//
//  Created by iMac on 20/11/24.
//

import Foundation
import UIKit

extension UIScrollView {
    func scrollToBottom() {
        let bottomOffset = CGPoint(x: 0, y: contentSize.height - bounds.size.height + contentInset.bottom)
        setContentOffset(bottomOffset, animated: true)
    }
    func scrollToTop() {
            let desiredOffset = CGPoint(x: 0, y: -contentInset.top)
            setContentOffset(desiredOffset, animated: true)
       }
}
