//
//  BringYouModel.swift
//  Kinship
//
//  Created by Vikram's Macbook on 01/03/24.
//

import Foundation

class BringYouModel{
    var id: Int
    var title: String

    init(id: Int, title: String) {
        self.id = id
        self.title = title
    }
}

class SegmentItemModel{
    var id: Int
    var title: String
    var image: String

    init(id: Int, title: String, image: String) {
        self.id = id
        self.title = title
        self.image = image
    }
}

class EventDetailsModel{
    var id: Int
    var title: String
    
    init(title: String, id: Int) {
        self.title = title
        self.id = id
    }
}

class EventsSegmentModel{
    var id: Int
    var title: String
    
    init(id: Int, title: String) {
        self.id = id
        self.title = title
    }
}
