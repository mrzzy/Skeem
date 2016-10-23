//
//  PVRDatabase.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public enum PVRDBKey:String
 * - Defines key of virtual storage Locations
*/
public enum PVRDBKey:String
{
    //Persistent File
    case task = "pvrdb_task" //Task Location
    case void_duration = "pvrdb_voidduration" //Void Duration Location

    //Temp File
    case cache = "pvrdb_cache" //Cache Location

    //Not Saved
    case mcache = "pvrdb_mcache" //Memory Only Cache
}

/* 
 * public enum PVRDBError:Error
 * - Defines errors that may occur when using PVRDatabase
*/
public enum PVRDBError:Error
{
    case entry_exist //Entry already exist
    case entry_not_exist //Entry does not exist
    case entry_modified //An Entry has been modified but not safed
}

/*
 * public enum PVRDBFileError:Error
 * - Defines errors that may occur when using PVRDBFile
*/
public enum PVRDBFileError:Error
{
    case file_not_exist //File does not exist
    case file_exist //File Already exists
    case file_unreadable //File is not readable. An unkown I/O error occured.
    case data_staged //Data has already been staged
}

/*
 * public class PVRDBFile:NSObject
 * - Defines an object that represents a database file
 * - Performs I/O Operations
*/
public class PVRDBFile:NSObject
{
    //Properties
    //Storage
    public var file_path:String //Path to file of the object
    private var data:NSMutableData = NSMutableData() //Data of the archive
    private var unach:NSKeyedUnarchiver! //Unarchiver
    private var ach:NSKeyedArchiver! //Archiver

    //Status
    var staged:Bool = false //Whether data has been staged

    /*
     * init(file_path:String)
     * [Argument]
     * file_path - Path to file of the object.
    */
    init(file_path:String = "")
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

    /*
     * public func stage(key:PVRDBKey,data:Any)
     * - Stage data for writing
     * [Arguments]
     * key - Virtual Storage Location to store data
     * data - Data to store for key
     */
    public func stage(key:PVRDBKey,data:Any)
    {
        if self.staged == false
        {
            //New Stage
            self.data = NSMutableData()
            self.ach = NSKeyedArchiver(forWritingWith: self.data)
            self.staged = true
        }

        self.ach.encode(data, forKey: key.rawValue)
    }

    /*
     * public func retrieve(key:PVRDBKey) throws -> Any
     * - Retrieve data from virtual storage location
     * [Argument]
     * key - Virtual storage location to retrieve from
     * [Return]
     * Any - Data retrieved
     * [Exception]
     * PVRDBFileError.data_staged - Changes have been staged
     */
    public func retrieve(key:PVRDBKey) throws -> Any
    {
        if self.staged == true
        {
            throw PVRDBFileError.data_staged
        }

        return self.unach.decodeObject(forKey: key.rawValue)
    }

    /*
     * public func commit()
     * - (Over)Write changes staged to disk
    */
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

    /*
     * public func load() throws
     * - Load data from disk
     * [Error]
     * PVRDBFileError.data_staged - Changes have been staged
     * PVRDBFileError.file_unreadable - File is unreadable, an unknown I/O error occured
     * PVRDBFileError.file_not_exist - File does not exist
    */
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
    //Properties
    //Data
    internal var task:[String:PVRTask] //Tasks
    internal var voidDuration:[String:PVRVoidDuration] //Void Duration
    internal var mcache:[String:Any]  //In-Memory Cache
    internal var cache:[String:NSCoding] //Cache

    //Storage
    internal var pst_file:PVRDBFile
    internal var tmp_file:PVRDBFile

    //Status
    internal var modified:Bool

    //Methods
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


        self.modified = false

        super.init()
    }

    //I/O
    /*
     * public func load() throws
     * - Loads all data from PVRDBFile, or inits data
     * [Exception]
     * PVRDBError.entry_modified - An Entry has been modified
    */
    public func load() throws
    {
        if self.modified == false
        {
            //Persistent Storage
            try! self.pst_file.load()
            self.task = (try! self.pst_file.retrieve(key: PVRDBKey.task) as! [String : PVRTask])
            self.voidDuration = (try! self.pst_file.retrieve(key: PVRDBKey.void_duration) as! [String:PVRDuration] as! [String : PVRVoidDuration])

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
        else
        {
            throw PVRDBError.entry_modified
        }
    }

    /*
     * public func commit()
     * - Commit changes to disk.
     */
    public func commit()
    {
        if self.modified == true
        {
            //Persistent File
            self.pst_file.stage(key: PVRDBKey.task, data: self.task)
            self.pst_file.stage(key: PVRDBKey.void_duration, data: self.voidDuration)
            self.pst_file.commit()

            //Temporary File
            self.tmp_file.stage(key: PVRDBKey.cache, data: self.cache)
            self.tmp_file.commit()
        }
    }
    /*
     * public func createEntry(locKey:PVRDBKey,key:String,val:Any) throws
     * - Creates an entry in the virtual storage location specifed by loc key
     * [Argument]
     * lockey - Virtual storage location to store entry
     * key - Unique Identifier for the entry
     * val - Value of the entry
     * [Exception]
     * PVRDBError.entry_exist - An entry already exists under the key
     */
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

        self.modified = true
    }

    /*
     public func updateEntry(lockey:PVRDBKey,key:String,val:Any) throws
     - Update value of entry specified by key in the virtual storage location specifed by lockey
     * [Argument]
     * lockey - Virtual storage location of the entry
     * key - Identifier for the entry
     * val - Value to update the entry
     * [Exception]
     *  PVRDBError.entry_not_exist - Entry specifed by key does not exist
    */
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

        self.modified = true
    }

    /*
     * public func deleteEntry(lockey:PVRDBKey,key:String)
     * - Deletes entry specifed by key in the virtual storage location specifed by lockey
     * [Argument]
     * lockey - Virtual storage location of the entry
     * key - Identifier of the entry
    */
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

        self.modified = true
    }

    /*
     * public func retrieveEntry(lockey:PVRDBKey,key:String) throws -> Any
     * - Retrieves the data of the entry specified key located in the virtual storage location specfied by lockey
     * NOTE: Might Terminate executable
     * [Argument]
     * lockey - Virtual storage location of the entry
     * key - Identifier of the entry
     * [Return]
     * Any - Data of the entry
     * [Exception]
     * PVRDBError.entry_not_exist - Entry does not exist
    */
    public func retrieveEntry(lockey:PVRDBKey,key:String) throws -> Any
    {
        if lockey == PVRDBKey.task
        {
            if let rst = self.task[key]
            {
                return rst
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.void_duration
        {
            if let rst = self.voidDuration[key]
            {
                return rst
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.cache
        {
            if let rst = self.cache[key]
            {
                return rst
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else if lockey == PVRDBKey.mcache
        {
            if let rst = self.mcache[key]
            {
                return rst
            }
            else
            {
                throw PVRDBError.entry_not_exist
            }
        }
        else
        {
            //Should not happen
            abort() //Terminates Executable
        }
    }

    /*
     * public func retrieveAllEntry(lockey:PVRDBKey) -> Any
     * - Retrieves all entries located in the virtual storage location specified by lockey
     * NOTE: Might Terminate Executable
     * [Argument]
     * lockey - Specfies the Virtual Storage location to retrieve all entrues
     * [Return]
     * Dictionary[String,Any] - Dictionary of entryies in virtual storage location
    */
    public func retrieveAllEntry(lockey:PVRDBKey) -> [String:Any]
    {
        if lockey == PVRDBKey.task
        {
            return self.task //Returns Copy
        }
        else if lockey == PVRDBKey.void_duration
        {
            return self.voidDuration //Returns Copy
        }
        else if lockey == PVRDBKey.cache
        {
            return self.cache //Returns Copy
        }
        else if lockey == PVRDBKey.mcache
        {
            return self.mcache //Returns Copy
        }
        else
        {
            //Should not happen
            abort() //Terminates Executable
        }
    }
}
