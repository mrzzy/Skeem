//
//  PVRDatabase.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum PVRDBKey:String
{
    case task = "pvrdb_task"
    case cache = "pvrdb_cache"
}

public enum PVRDBError:Error
{
    case entry_exist
    case entry_not_exist
}

public class PVRDatabase:NSObject
{
    //Data
    var task:[String:PVRTask] = [:] //Tasks
    var mcache:[String:Any] = [:]  //In-Memory Cache
    var cache:[String:NSCoding] = [:]//Cache
    
    //Storage
    var stage:Bool = false
    
    var pst_file_path:String
    var pst_file_data:NSMutableData!
    var pst_ach:NSKeyedArchiver!

    var tmp_file_path:String
    var tmp_file_data:NSMutableData!
    var tmp_ach:NSKeyedArchiver!
    
    //Init
    
    override init()
    {
        //Persistent Storage
        let doc_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.pst_file_path = "\(doc_path)/pvr_db.plist"

        //Cache (Tmp Storage)
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.tmp_file_path = "\(tmp_path)/pvr_cache.plist"

        super.init()
        
        self.load()
    }
    
    //Staging
    
    public func stage_data(key:PVRDBKey,val:Any,file_path:String)
    {
        if self.stage == false
        {
            //New Stage
            if file_path == self.pst_file_path
            {
                self.pst_file_data = NSMutableData()
                self.pst_ach = NSKeyedArchiver(forWritingWith: self.pst_file_data)
            }
            else if file_path == self.tmp_file_path
            {
                self.tmp_file_data = NSMutableData()
                self.tmp_ach = NSKeyedArchiver(forWritingWith: self.tmp_file_data)
            }
            
            self.stage = true
        }
        
        if file_path == self.pst_file_path
        {
            self.pst_ach.encode(val, forKey: key.rawValue)
            
        }
        else if file_path == self.tmp_file_path        {
            self.tmp_ach.encode(val, forKey: key.rawValue)
        }
        
    }
    
    //I/O

    public func load_data(key:PVRDBKey,file_path:String) -> Any?
    {
        if FileManager.default.fileExists(atPath: file_path)
        {
            if let ua = NSKeyedUnarchiver.unarchiveObject(withFile: file_path) as? NSKeyedUnarchiver
            {
                return ua.decodeObject(forKey: key.rawValue)
            }
        }
        
        return nil
    }

    
    public func commit_data(file_path:String)
    {
        //Remove Old File
        if FileManager.default.fileExists(atPath: file_path)
        {
            try! FileManager.default.removeItem(atPath: file_path)
        }
        
        if file_path == self.pst_file_path
        {
            self.pst_ach.finishEncoding()
            self.pst_file_data.write(toFile: file_path, atomically: true)
            
            self.stage = false
        }
        else
        {
            self.tmp_ach.finishEncoding()
            self.tmp_file_data.write(toFile: file_path, atomically: true)
            
            self.stage = false
        }
    }

    public func commit()
    {
        self.stage_data(key: PVRDBKey.task, val: self.task, file_path: self.pst_file_path)
        self.stage_data(key: PVRDBKey.cache, val: self.cache, file_path: self.tmp_file_path)
        
        self.commit_data(file_path: self.pst_file_path)
        self.commit_data(file_path: self.tmp_file_path)
    }
    
    public func load()
    {
        //Persistent Storage
        if (UIApplication.shared.delegate as! AppDelegate).use_cnt > 1
        {
            if let tsk = self.load_data(key: PVRDBKey.task, file_path: self.pst_file_path)
            {
                self.task = (tsk as! [String : PVRTask])
            }
            else
            {
                abort() //Task List Failed to Load
            }
        }
        
        //Cache (Tmp Storage)
        if (UIApplication.shared.delegate as! AppDelegate).use_cnt > 1, let cch = self.load_data(key: PVRDBKey.cache, file_path: self.tmp_file_path)
        {
            self.cache = (cch as! [String:NSCoding])
        }
    }
}
