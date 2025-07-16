//
//  RoundedButton.swift
//  Kinship
//
//  Created by Vikram's Macbook on 29/02/24.
//

import UIKit

//@IBDesignable
//class RoundedButton: UIButton {
//
//    // MARK: - Inspectable properties
//
//    @IBInspectable var cornerRadius: CGFloat = 24 {
//        didSet {
//            layer.cornerRadius = cornerRadius
//            layer.masksToBounds = true
//        }
//    }
//
//    @IBInspectable var buttonColor: UIColor = #colorLiteral(red: 0.8, green: 0.4, blue: 0.4666666667, alpha: 1) {
//        didSet {
//            backgroundColor = buttonColor
//        }
//    }
//
//    @IBInspectable var buttonHeight: CGFloat = 0 {
//            didSet {
//                updateButtonHeight()
//            }
//        }
//
//    // MARK: - Initializers
//
//    override init(frame: CGRect) {
//        super.init(frame: frame)
//        setupButton()
//    }
//
//    required init?(coder aDecoder: NSCoder) {
//        super.init(coder: aDecoder)
//        setupButton()
//    }
//
//    // MARK: - Private methods
//
//    private func setupButton() {
//        layer.cornerRadius = cornerRadius
//        backgroundColor = buttonColor
//        setTitleColor(.white, for: .normal)
//    }
//
//    // MARK: - Designable support
//
//    override func prepareForInterfaceBuilder() {
//        super.prepareForInterfaceBuilder()
//        setupButton()
//    }
//
//    private func updateButtonHeight() {
//        NSLayoutConstraint.deactivate(constraints.filter { $0.firstAttribute == .height })
//        NSLayoutConstraint.activate([heightAnchor.constraint(equalToConstant: buttonHeight)])
//    }
//}

@IBDesignable
class RoundedButton: UIButton {
    
    // MARK: - Inspectable properties
    
    @IBInspectable var cornerRadius: CGFloat = 24 {
        didSet {
            layer.cornerRadius = cornerRadius
            layer.masksToBounds = true
        }
    }
    
    @IBInspectable var buttonColor: UIColor = #colorLiteral(red: 0.2823529412, green: 0.1176470588, blue: 0.4392156863, alpha: 1) {
        didSet {
            backgroundColor = buttonColor
        }
    }
    
    @IBInspectable var buttonHeight: CGFloat = 48 {
        didSet {
            updateButtonHeight()
        }
    }
    
    // MARK: - Initializers
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupButton()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupButton()
    }
    
    // MARK: - Private methods
    
    private func setupButton() {
        layer.cornerRadius = cornerRadius
        backgroundColor = buttonColor
        setTitleColor(.white, for: .normal)
        updateButtonHeight()
    }
    
    private func updateButtonHeight() {
        NSLayoutConstraint.deactivate(constraints.filter { $0.firstAttribute == .height })
        NSLayoutConstraint.activate([heightAnchor.constraint(equalToConstant: buttonHeight)])
    }
    
    // MARK: - Designable support
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        setupButton()
    }
}
