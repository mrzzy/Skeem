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
    var CFG:PVRConfig!

    //App Logic
    var SCH:PVRScheduler!

    //Storage
    var ud:UserDefaults!
    
    //MARK:App Delegate
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool {
        //Init Data
        self.use_cnt = 0
        self.DB = PVRDatabase()
        self.DBC = PVRDataController(db: DB)
        self.CFG = PVRConfig(cfg: Dictionary<String,NSCoding>())
        self.SCH = PVRScheduler(dataCtrl: self.DBC,cfg: self.CFG)
        self.loadUD()
        
        return true
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        try? DB.load()
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
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
            let cfg_data = (ud.object(forKey: PVRUserDefaultKey.setting.rawValue) as! [String : NSCoding])
            self.CFG = PVRConfig(cfg: cfg_data)
        }

        //Update use count
        self.use_cnt = self.use_cnt + 1
    }
    
    public func commitUD()
    {
        //Save App Status
        ud.set(self.use_cnt, forKey: PVRUserDefaultKey.use.rawValue)
        ud.set(self.CFG.cfg, forKey: PVRUserDefaultKey.setting.rawValue)
        self.ud.synchronize()
    }
}
