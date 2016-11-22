//
//  AppDelegate.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 17/9/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum SKMUserDefaultKey:String {
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
    var DB:SKMDatabase!
    var DBC:SKMDataController!
    var CFG:SKMConfig!

    //App Logic
    var SCH:SKMScheduler!

    //Storage
    var ud:UserDefaults!
    
    //MARK:App Delegate
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool {
        //Init Data
        self.use_cnt = 0
        self.DB = SKMDatabase()
        self.DBC = SKMDataController(db: DB)
        self.CFG = SKMConfig(cfg: Dictionary<String,NSCoding>())
        self.SCH = SKMScheduler(dataCtrl: self.DBC,cfg: self.CFG)
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
        if let ud = UserDefaults(suiteName: SKMUserDefaultKey.suite.rawValue)
        {
            self.ud = ud
            self.use_cnt = ud.integer(forKey: SKMUserDefaultKey.use.rawValue)
        }
        else
        {
            //No Suite for User Defaults
            UserDefaults().addSuite(named: SKMUserDefaultKey.suite.rawValue)
            let ud = UserDefaults(suiteName: SKMUserDefaultKey.suite.rawValue)!
            self.ud = ud
        }

        //Load App Settins If Present
        if self.use_cnt > 1
        {
            let cfg_data = (ud.object(forKey: SKMUserDefaultKey.setting.rawValue) as! [String : NSCoding])
            self.CFG = SKMConfig(cfg: cfg_data)
        }

        //Update use count
        self.use_cnt = self.use_cnt + 1
    }
    
    public func commitUD()
    {
        //Save App Status
        ud.set(self.use_cnt, forKey: SKMUserDefaultKey.use.rawValue)
        ud.set(self.CFG.cfg, forKey: SKMUserDefaultKey.setting.rawValue)
        self.ud.synchronize()
    }
}
