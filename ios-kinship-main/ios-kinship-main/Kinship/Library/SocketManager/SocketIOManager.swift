//
//  SocketIOManager.swift
//  Kinship
//
//  Created by iMac on 03/05/24.
//

import Foundation
import SocketIO

class SocketHelper {
    static let shared = SocketHelper()
//
    let manager:SocketManager?
    var socket: SocketIOClient!
    
    private init() {
//        manager = SocketManager(socketURL: URL(string:socketServerUrl)!, config: [.log(true), .compress,.connectParams(["token" : Utility.getAccessToken() ?? "" ]), .reconnects(true)])
//        manager = SocketManager(socketURL: URL(string: socketServerUrl)!, config: [.log(true), .compress])
        manager = SocketManager(socketURL: URL(string: socketServerUrl)!, config: [.log(true), .compress, .reconnects(true), .extraHeaders(["Authorization": "\(Utility.getAccessToken() ?? "")"])])      // Jaydeep sir
        socket = manager?.defaultSocket
    }
    
    func connectSocket(completion: @escaping(Bool) -> () ) {
        self.disconnectSocket()
        socket.on(clientEvent: .connect) {[weak self] (data, ack) in
            print(" ==== socket connected ==== ")
            self?.socket.removeAllHandlers()
            completion(true)
        }
        socket.connect()
    }
    func disconnectSocket() {
        socket.removeAllHandlers()
        socket.disconnect()
        print("==== socket Disconnected ====")
    }
    
    func checkConnection() -> Bool {
        if socket.status == .connected {
            return true
        }
        return false
    }
    
    enum Events {
        case createRoom
        case roomConnected
        case disconnected
        case UpdateStatusToOnline
        case sendMessage
        case newMessage
        case roomDisconnect
        
        var emitterName: String {
            switch self {
            case .createRoom:
                return "createRoom"
            case .roomConnected:
                return "roomConnected"
            case .disconnected:
                return "disconnected"
            case .UpdateStatusToOnline:
                return "UpdateStatusToOnline"
            case .sendMessage:
                return "sendMessage"
            case .newMessage:
                return "newMessage"
            case .roomDisconnect:
                return "roomDisconnect"
            }
        }
        
        func emit(params: [String : Any]) {
            SocketHelper.shared.socket?.emit(emitterName, params)
        }
        
        func listen(completion: @escaping (Any) -> Void) {
            SocketHelper.shared.socket?.on(emitterName) { (response, emitter) in
                completion(response[0])
            }
        }
        
        func off() {
            SocketHelper.shared.socket?.off(emitterName)
        }
    }
}
