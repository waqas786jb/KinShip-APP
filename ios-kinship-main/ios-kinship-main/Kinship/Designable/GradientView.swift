//
//  GradientView.swift
//  Kinship
//
//  Created by Vikram's Macbook on 29/02/24.
//

import UIKit

@IBDesignable
class GradientView: UIView {
    // Define the colors for the gradient
    @IBInspectable var startColor: UIColor = .white {
        didSet {
            updateGradient()
        }
    }
    
    @IBInspectable var midColor: UIColor = .white {
        didSet {
            updateGradient()
        }
    }
    
    @IBInspectable var endColor: UIColor = .black {
        didSet {
            updateGradient()
        }
    }
    
    private func updateGradient() {
        // Set up the gradient layer
        let gradientLayer = self.layer as! CAGradientLayer
        gradientLayer.colors = [startColor.cgColor, endColor.cgColor]
        gradientLayer.startPoint = CGPoint(x: 0, y: 0)
        gradientLayer.endPoint = CGPoint(x: 1, y: 1)
    }
    
    override class var layerClass: AnyClass {
        return CAGradientLayer.self
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        updateGradient()
    }
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        updateGradient()
    }
}

