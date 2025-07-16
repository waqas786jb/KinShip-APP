//
//  LocationSearchScreen.swift
//  Pods
//
//  Created by iMac on 25/04/24.
//

import UIKit
import MapKit

class LocationSearchScreen: UIViewController {

//   MARK: - IBOUTLETS
    @IBOutlet weak var locationSearch: UISearchBar!
    @IBOutlet weak var locationTableView: UITableView!
    
//    MARK: - VARIABLES
    var searchCompleter = MKLocalSearchCompleter()
    var searchResults = [MKLocalSearchCompletion]()
    var back : ((Double, Double, String) -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.searchCompleter.delegate = self
        self.locationSearch.delegate = self
        // Do any additional setup after loading the view.
    }

}

//MARK: - EXTENSION

//MARK: - SEARCH DELEGATE
extension LocationSearchScreen: UISearchBarDelegate{
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        searchCompleter.queryFragment = searchText
    }
}

extension LocationSearchScreen: MKLocalSearchCompleterDelegate {
    func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
        searchResults = completer.results
        locationTableView.reloadData()
    }
}

//MARK: - TABLEVIEW DELEGATE AND DATA SOURSE
extension LocationSearchScreen: UITableViewDelegate, UITableViewDataSource{
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.searchResults.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let searchResult = searchResults[indexPath.row]
        let cell = UITableViewCell(style: .subtitle, reuseIdentifier: nil)
        cell.textLabel?.text = searchResult.title
        cell.detailTextLabel?.text = searchResult.subtitle
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let searchResult = searchResults[indexPath.row]
        let searchRequest = MKLocalSearch.Request(completion: searchResult)
        
        let search = MKLocalSearch(request: searchRequest)
        search.start{ (response, error) in
            
            guard let coordinate = response?.mapItems[0].placemark.coordinate else { return }
            guard let name = response?.mapItems[0].name else { return }
            
            let latitude = coordinate.latitude
            let longitude = coordinate.longitude
            
//            print(latitude)
//            print(longitude)
//            print(name)
            self.back?(latitude, longitude, name)
        }
    }
}
