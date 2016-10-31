//
//  AppDelegate.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 17/9/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum PVRUserDefaultKey:String {
    case suite = "pvr_ud_suite"
    case use = "pvr_ud_use"
    case setting = "pvr_ud_setting"
}

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    //UI
    var window: UIWindow?
    
    //App Status
    var use_cnt:Int!

    //App Data
    var DB:PVRDatabase!
    var DBC:PVRDataController!
    var setting:[String:NSCoding]!

    //App Logic
    var SCH:PVRScheduler!

    //Storage
    var ud:UserDefaults!
    
    //MARK:App Delegate
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool {
        //Init Data
        self.use_cnt = 0
        self.setting = Dictionary<String,NSCoding>()
        self.DB = PVRDatabase()
        self.DBC = PVRDataController(db: DB)
        self.SCH = PVRScheduler(dataCtrl: self.DBC)

        self.loadUD()

        let test = PVRTest()
        let rst = test.unit_schedule()

        return true
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        DB.commit()
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        try? DB.load()
    }

    func applicationDidBecomeActive(_ application: UIApplication) {

    }

    func applicationWillTerminate(_ application: UIApplication) {
        DB.commit()
    }
    
    //MARK:Additional Methods
    public func loadUD()
    {
        //Load user Defaults
        if let ud = UserDefaults(suiteName: PVRUserDefaultKey.suite.rawValue)
        {
            self.ud = ud
            self.use_cnt = ud.integer(forKey: PVRUserDefaultKey.use.rawValue)
        }
        else
        {
            //No Suite for User Defaults
            UserDefaults().addSuite(named: PVRUserDefaultKey.suite.rawValue)
            let ud = UserDefaults(suiteName: PVRUserDefaultKey.suite.rawValue)!
            self.ud = ud
        }

        //Load App Settins If Present
        if self.use_cnt > 1
        {
            self.setting = (ud.object(forKey: PVRUserDefaultKey.setting.rawValue) as! [String : NSCoding])
        }

        //Update use count
        self.use_cnt = self.use_cnt + 1
    }
    
    public func commitUD()
    {
        //Save App Status
        ud.set(self.use_cnt, forKey: PVRUserDefaultKey.use.rawValue)
        ud.set(self.setting, forKey: PVRUserDefaultKey.setting.rawValue)
        self.ud.synchronize()
    }
}
