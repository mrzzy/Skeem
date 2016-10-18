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
    //Persistent File
    case task = "pvrdb_task"
    case void_duration = "pvrdb_voidduration"

    //Temp File
    case cache = "pvrdb_cache"

    //Not Saved
    case mcache = "pvrdb_mcache"
}

public enum PVRDBError:Error
{
    case entry_exist
    case entry_not_exist
}

public enum PVRDBFileError:Error
{
    case file_not_exist
    case file_exist
    case file_unreadable
    case data_staged
}

public class PVRDBFile:NSObject
{
    //Storage
    public var file_path:String
    private var data:NSMutableData = NSMutableData()
    private var unach:NSKeyedUnarchiver!
    private var ach:NSKeyedArchiver!

    //Status
    var staged:Bool = false

    init(file_path:String)
    {
        self.file_path = file_path

        super.init()

        do
        {
            try self.load()
        }
        catch PVRDBFileError.file_not_exist
        {
            print("Info:PVRDBFile: File does not exist, assumming new file")
        }
        catch PVRDBFileError.file_unreadable
        {
            print("FATAL:PVRDBFile: File not readable")
            abort()
        }
        catch
        {
            print("FATAL:PVRDBFile: Unknown Error")
            abort()
        }

    }

    public func stage(key:PVRDBKey,val:Any)
    {
        if self.staged == false
        {
            //New Stage
            self.data = NSMutableData()
            self.ach = NSKeyedArchiver(forWritingWith: self.data)
            self.staged = true
        }

        self.ach.encode(val, forKey: key.rawValue)
    }

    public func retrieve(key:PVRDBKey) throws -> Any
    {
        if self.staged == true
        {
            throw PVRDBFileError.data_staged
        }

        return self.unach.decodeObject(forKey: key.rawValue)
    }

    public func commit()
    {
        if self.staged == true
        {
            if FileManager.default.fileExists(atPath: self.file_path)
            {
                try! FileManager.default.removeItem(atPath: self.file_path)
            }

            //Write Data
            self.ach.finishEncoding()
            self.data.write(toFile: self.file_path, atomically: true)
            self.staged = false
        }
    }

    public func load() throws
    {
        if self.staged == true
        {
            throw PVRDBFileError.data_staged
        }

        if FileManager.default.fileExists(atPath: self.file_path) == true
        {
            if let dat = NSMutableData(contentsOfFile: self.file_path)
            {
                //Data Read Successful
                self.data = dat
                self.unach = NSKeyedUnarchiver(forReadingWith: (self.data as Data))
            }
            else
            {
                throw PVRDBFileError.file_unreadable
            }
        }
        else
        {
            throw PVRDBFileError.file_not_exist
        }
    }
}


public class PVRDatabase:NSObject
{
    //Data
    var task:[String:PVRTask] //Tasks
    var voidDuration:[String:PVRVoidDuration] //Void Duration
    var mcache:[String:Any]  //In-Memory Cache
    var cache:[String:NSCoding]//Cache

    //Storage
    var pst_file:PVRDBFile
    var tmp_file:PVRDBFile

    //Init
    override init()
    {
        //Persistent Storage
        let doc_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.pst_file = PVRDBFile(file_path: "\(doc_path)/pvr_pst.plist")

        //Cache (Tmp Storage)
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.tmp_file = PVRDBFile(file_path: "\(tmp_path)/pvr_cache.plist")

        self.task = [:]
        self.voidDuration = [:]
        self.mcache = [:]
        self.cache = [:]

        super.init()
    }

    //I/O 
    public func load()
    {
        //Persistent Storage
        try! self.pst_file.load()
        self.task = (try! self.pst_file.retrieve(key: PVRDBKey.task) as! [String : PVRTask])
        //Temporary Storage
        do
        {
            try self.tmp_file.load()
            if let cch = (try? self.tmp_file.retrieve(key: PVRDBKey.cache) as! [String:NSCoding])
            {
                self.cache = cch
            }
            else
            {
                self.cache = [:]
            }
        }
        catch PVRDBFileError.file_not_exist
        {
            self.cache = [:]
        }
        catch
        {
            abort()
        }

    }

    public func commit()
    {
        //Persistent File
        self.pst_file.stage(key: PVRDBKey.task, val: self.task)
        self.pst_file.stage(key: PVRDBKey.void_duration, val: self.voidDuration)
        self.pst_file.commit()

        //Temporary File
        self.tmp_file.stage(key: PVRDBKey.cache, val: self.cache)
        self.tmp_file.commit()
    }

    public func createEntry(locKey:PVRDBKey,key:String,val:Any) throws
    {
        if locKey == PVRDBKey.task
        {
            if self.task[key] == nil
            {
                self.task[key] = (val as! PVRTask)
            }
            else
            {
                throw PVRDBError.entry_exist
            }
        }
        else if locKey == PVRDBKey.void_duration
        {
            if self.voidDuration[key] == nil
            {
                self.voidDuration[key] = (val as! PVRVoidDuration)
            }
            else
            {
                throw PVRDBError.entry_exist
            }
        }
        else if locKey == PVRDBKey.cache
        {
            if self.cache[key] == nil
            {
                self.cache[key] = (val as! NSCoding)
            }
            else
            {
                throw PVRDBError.entry_exist
            }
        }
        else if locKey == PVRDBKey.mcache
        {
            if self.mcache[key] == nil
            {
                self.mcache[key] = val
            }
            else
            {
                throw PVRDBError.entry_exist
            }
        }
    }

    public func updateEntry(lockey:PVRDBKey,key:String,val:Any) throws
    {
        if lockey == PVRDBKey.task
        {
            if self.task[key] != nil
            {
                self.task[key] = (val as! PVRTask)
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.void_duration
        {
            if self.voidDuration[key] != nil
            {
                self.voidDuration[key] = (val as! PVRVoidDuration)
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.cache
        {
            if self.cache[key] != nil
            {
                self.cache[key] = (val as! NSCoding)
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.mcache
        {
            if self.mcache[key] != nil
            {
                self.mcache[key] = val
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
    }

    public func deleteEntry(lockey:PVRDBKey,key:String)
    {
        if lockey == PVRDBKey.task
        {
            self.task[key] = nil
        }
        else if lockey == PVRDBKey.void_duration
        {
            self.voidDuration[key] = nil
        }
        else if lockey == PVRDBKey.cache
        {
            self.cache[key] = nil
        }
        else if lockey == PVRDBKey.mcache
        {
            self.mcache[key] = nil
        }
    }
}   
