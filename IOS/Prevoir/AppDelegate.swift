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
}

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    //UI
    var window: UIWindow?
    
    //App Status
    var use_cnt:Int = 0

    //Storage
    var ud:UserDefaults!
    
    //MARK:App Delegate
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey : Any]? = nil) -> Bool {
        self.loadAppStatus()
        
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
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    //MARK:Additional Methods
    public func loadAppStatus()
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
        
        //Update
        self.use_cnt += 1
    }
    
    public func commitAppStatus()
    {
        //Save App Status
        ud.set(self.use_cnt, forKey: PVRUserDefaultKey.use.rawValue)
        
        self.ud.synchronize()
    }
}

