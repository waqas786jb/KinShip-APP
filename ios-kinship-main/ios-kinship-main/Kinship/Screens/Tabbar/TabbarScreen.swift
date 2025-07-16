//
//  TabbarScreen.swift
//  Kinship
//
//  Created by iMac on 04/03/24.
//

import UIKit

class TabbarScreen: UITabBarController {
    // MARK: - Outlets

    // MARK: - Method
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialDetail()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
    }
    // MARK: - IBActions
    
    // MARK: - Functions
    func initialDetail() {
        self.setupTabbar()
    }
    
    func setupTabbar() {
        
        let homeVC     = STORYBOARD.home.instantiateViewController(withIdentifier: "HomeScreen") as! HomeScreen
        let eventsVC   = STORYBOARD.events.instantiateViewController(withIdentifier: "EventDetailsScreen") as! EventDetailsScreen
        let messagesVC = STORYBOARD.messages.instantiateViewController(withIdentifier: "MessagesScreen") as! MessagesScreen
        let settingsVC = STORYBOARD.settings.instantiateViewController(withIdentifier: "SettingsScreen") as! SettingsScreen
        
        let homeViewController        = UINavigationController(rootViewController: homeVC)
        let eventViewController       = UINavigationController(rootViewController: eventsVC)
        let messagesViewController    = UINavigationController(rootViewController: messagesVC)
        let settingsViewController    = UINavigationController(rootViewController: settingsVC)
        
        homeViewController.isNavigationBarHidden        = true
        eventViewController.isNavigationBarHidden       = true
        messagesViewController.isNavigationBarHidden    = true
        settingsViewController.isNavigationBarHidden    = true
        
        homeViewController.tabBarItem = self.getTabBarItem(
                                                            tag: 0,
                                                            title: "Home",
                                                            imageName: "ic_home",
                                                            selectedImageName: "ic_home_fill"
                                                        )
        
        eventViewController.tabBarItem = self.getTabBarItem(
                                                            tag: 1,
                                                            title: "Events",
                                                            imageName: "ic_calendar",
                                                            selectedImageName: "ic_calendar_fill"
                                                        )
        
        messagesViewController.tabBarItem = self.getTabBarItem(
                                                            tag: 2,
                                                            title: "Messages",
                                                            imageName: "ic_chat_lite",
                                                            selectedImageName: "ic_chat_fill"
                                                        )
        
        settingsViewController.tabBarItem = self.getTabBarItem(
                                                            tag: 3,
                                                            title: "Settings",
                                                            imageName: "ic_setting",
                                                            selectedImageName: "ic_setting_fill"
                                                        )
        
        viewControllers = [
            homeViewController,
            eventViewController,
            messagesViewController,
            settingsViewController
        ]
        
        selectedViewController = viewControllers![0]
        
        self.tabBar.tintColor = #colorLiteral(red: 0.2820000052, green: 0.1180000007, blue: 0.4390000105, alpha: 1)
        self.tabBar.unselectedItemTintColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1).withAlphaComponent(0.5)
        
    }
    
    func getTabBarItem(tag: Int, title: String, imageName: String, selectedImageName: String) -> UITabBarItem{
        let tabBarItem =  UITabBarItem(
            title: title,
            image: UIImage(named: imageName)?.withRenderingMode(.alwaysOriginal),
            selectedImage: UIImage(named: selectedImageName)?.withRenderingMode(.alwaysOriginal))
        tabBarItem.tag = tag
        
        return tabBarItem
    }
    
}


