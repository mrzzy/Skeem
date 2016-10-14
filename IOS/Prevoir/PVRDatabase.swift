//
//  PVRDatabase.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public class PVRDatabase:NSObject
{
    //Data
    var task:[String:PVRTask] //Tasks
    var mcache:[String:Any]  //In-Memory Cache
    var cache:[String:NSCoding] //Cache
    
    //Storage
    var pst_file_path:String
    var tmp_file_path:String
    
    override init()
    {
        //Persistent Storage
        let doc_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.pst_file_path = "\(doc_path)/pvr_db.plist"
        
        if FileManager.default.fileExists(atPath: self.pst_file_path)
        {
            if let ua = NSKeyedUnarchiver.unarchiveObject(withFile: self.pst_file_path) as? NSKeyedUnarchiver
            {
                self.task = (ua.decodeObject(forKey: "task") as! [String:PVRTask])
            }
            else
            {
                print("ERROR: Failed to load Persistent Data from: \(self.pst_file_path)")
                abort()
            }
        }
        else
        {
            self.task = [:]
        }
    
        //Cache (Tmp Storage)
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.tmp_file_path = "\(tmp_path)/pvr_cache.plist"
        
        if FileManager.default.fileExists(atPath: self.tmp_file_path)
        {
            if let ua = NSKeyedUnarchiver.unarchiveObject(withFile: self.tmp_file_path) as? NSKeyedArchiver
            {
                self.cache = (ua.decodeObject(forKey: "task") as! [String:PVRTask])
            }
            else
            {
                print("Info: Failed to load Tmp Data from: \(self.tmp_file_path)")
                
                self.cache = [:]
            }
        }
        else
        {
            self.cache = [:]
        }
        
        //In-Memory Cache
        self.mcache = [:]
    }
}
