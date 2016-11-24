//
//  SKMDatabaseTest.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 22/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import XCTest
@testable import Skeem

class SKMDBFileTest: XCTestCase
{
    var DBFile:SKMDBFile!
    var commit:Bool = false

    override func setUp()
    {
        super.setUp()
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.DBFile = SKMDBFile(file_path: "\(tmp_path)/skmfile_test.plist")
    }

    override func tearDown()
    {
        if self.commit
        {
            try! FileManager.default.removeItem(atPath: self.DBFile.file_path)
        }
        super.tearDown()
    }

    func load_testData()
    {
        self.DBFile.stage(key: SKMDBKey.task, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.void_duration, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.cache, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.mcache, data: ["Test Data"])
    }

    func unload_testData()
    {
        self.DBFile.stage(key: SKMDBKey.task, data: [String]())
        self.DBFile.stage(key: SKMDBKey.void_duration, data: [String]())
        self.DBFile.stage(key: SKMDBKey.cache, data: [String]())
        self.DBFile.stage(key: SKMDBKey.mcache, data: [String]())

        self.DBFile.commit()
    }

    func test_init()
    {
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.DBFile = SKMDBFile(file_path: "\(tmp_path)/skmfile_test.plist")

        //Check Properties
        XCTAssert((self.DBFile.file_path == "\(tmp_path)/skmfile_test.plist"))
    }

    func test_stage()
    {
        self.DBFile.stage(key: SKMDBKey.task, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.void_duration, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.cache, data: ["Test Data"])
        self.DBFile.stage(key: SKMDBKey.mcache, data: ["Test Data"])

        self.unload_testData()
    }

    func test_commit()
    {
        self.load_testData()

        self.DBFile.commit()

        self.unload_testData()

        self.DBFile.commit()
    }

    func test_load()
    {
        self.load_testData()

        self.DBFile.commit()

        //Reset DB File
        let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
        self.DBFile = SKMDBFile(file_path: "\(tmp_path)/skmfile_test.plist")

        do
        {
            try self.DBFile.load()
        }
        catch
        {
            XCTFail("Caught unexpected error")
        }

        //Test Error
        var pass:Bool = false

        //Test ErrorSKMDBFileError.data_staged
        self.load_testData()
        do
        {
            try self.DBFile.load()
        }
        catch SKMDBFileError.data_staged
        {
            pass = true
        }
        catch
        {
            XCTFail("Caught unexpected Error")
        }
        if pass == false
        {
            XCTFail("Expected Error Failed to Throw: SKMDBFileError.data_staged")
        }

        //Test Error SKMDBFileError.file_not_exist
        pass = false
        self.unload_testData()
        try! FileManager.default.removeItem(atPath: self.DBFile.file_path)
        do
        {
            try self.DBFile.load()
        }
        catch SKMDBFileError.file_not_exist
        {
            pass = true
        }
        catch
        {
            XCTFail("Caught Unexpected Error")
        }
        if pass == false
        {
            XCTFail("Expected Error Failed to Throw: SKMDBFileError.file_not_exist")
        }

        self.unload_testData()
    }

    func test_retrieve()
    {
        self.load_testData()
        self.DBFile.commit()

        //Test Retrieve Data
        XCTAssert((try? self.DBFile.retrieve(key: SKMDBKey.task)) != nil)
        XCTAssert((try? self.DBFile.retrieve(key: SKMDBKey.void_duration)) != nil)
        XCTAssert((try? self.DBFile.retrieve(key: SKMDBKey.cache)) != nil)
        XCTAssert((try? self.DBFile.retrieve(key: SKMDBKey.mcache)) != nil)

        self.unload_testData()
        
    }

}

class SKMDatabaseTest: XCTestCase {
    var DB:SKMDatabase!

    //Status
    var commit:Bool = false

    override func setUp() {
        super.setUp()
        self.DB = SKMDatabase()
        self.commit = false
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        if self.commit == true
        {
            let doc_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
            try! FileManager.default.removeItem(atPath: "\(doc_path)/skm_pst.plist")

            let tmp_path = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)[0]
            try! FileManager.default.removeItem(atPath: "\(tmp_path)/skm_cache.plist")
        }

        super.tearDown()
    }

    func load_test_data()
    {
        //Task Virtual Storage Location
        try! self.DB.createEntry(locKey: SKMDBKey.task, key: "task_test", val: SKMTask(name: "task_test", deadline: NSDate() as NSDate, duration: 0, duration_affinity: 0, subject: "test", description: "This is a test task"))

        //Void Duration Storage Location
        try! self.DB.createEntry(locKey: SKMDBKey.void_duration, key: "voidd_test", val: SKMVoidDuration(begin: NSDate() as NSDate, duration: 0, name: "voidd_test", asserted: false))

        //Cache VIrtual Storage Location
        try!self.DB.createEntry(locKey: SKMDBKey.cache, key: "cache_test", val: "test_value")

        //In Memory Cache Virtual Storage Location
        try! self.DB.createEntry(locKey: SKMDBKey.mcache, key: "mcache_test", val: "test_value")
    }

    func unload_test_data()
    {
        //Task Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "task_test")
        //Void Duration Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.void_duration, key: "voidd_test")
        //Cache Virtual Sotrage Location
        self.DB.deleteEntry(lockey: SKMDBKey.cache, key: "cache_test")
        //In Memory Cache Virutal storage location
        self.DB.deleteEntry(lockey: SKMDBKey.mcache, key: "mcache_test")
    }
    
    func test_init()
    {
        self.DB = SKMDatabase()

        XCTAssert(self.DB != nil)

    }

    func test_createEntry()
    {
        do
        {
            //Task Virtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.task, key: "task_test", val: SKMTask(name: "task_test", deadline: NSDate() as NSDate, duration: 0, duration_affinity: 0, subject: "test", description: "This is a test task"))

            //Void Duration Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.void_duration, key: "voidd_test", val: SKMVoidDuration(begin: NSDate() as NSDate, duration: 0, name: "voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch SKMDBError.entry_exist
        {
            XCTFail("Database failed to create test entry")
        }
        catch
        {
            XCTFail("Unkown Error")
        }

        //Test Error
        do
        {
            //Task Virtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.task, key: "task_test", val: SKMTask(name: "task_test", deadline: NSDate() as NSDate, duration: 0, duration_affinity: 0, subject: "test", description: "This is a test task"))

            //Void Duration Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.void_duration, key: "voidd_test", val: SKMVoidDuration(begin: NSDate() as NSDate, duration: 0, name: "voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.createEntry(locKey: SKMDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch SKMDBError.entry_exist
        {
            self.unload_test_data()
            return
        }
        catch
        {
            XCTFail("Unknown Error")
        }

        XCTFail("Expected Error Failed to throw: SKMDBError.entry_exist")
    }

    func test_updateEntry()
    {
        self.load_test_data()

        //Update Test
        do
        {
            //Task Virtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.task, key: "task_test", val: SKMTask(name: "update_task_test", deadline: (NSDate() as NSDate), duration: 0, duration_affinity: 0, subject: "update test", description: "This is an updated test task"))

            //Void Duration Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.void_duration, key: "voidd_test", val: SKMVoidDuration(begin: (NSDate() as NSDate), duration: 0, name: "update_voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch SKMDBError.entry_not_exist
        {
            XCTFail("Entry to update does not exist in database")
        }
        catch
        {
            XCTFail("Unknown Error")
        }

        self.unload_test_data()

        do
        {
            //Task Virtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.task, key: "task_test", val: SKMTask(name: "update_task_test", deadline: (NSDate() as NSDate), duration: 0, duration_affinity: 0, subject: "update test", description: "This is an updated test task"))

            //Void Duration Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.void_duration, key: "voidd_test", val: SKMVoidDuration(begin: (NSDate() as NSDate), duration: 0, name: "update_voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.updateEntry(locKey: SKMDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch SKMDBError.entry_not_exist
        {
            return
        }
        catch
        {
            XCTFail("Unknown Error")
        }

        XCTFail("Expected Error Failed to Throw: SKMDBError.entry_not_exist")
    }

    func test_deleteEntry()
    {
        self.load_test_data()

        //Delete Test
        //Task Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "task_test")
        //Void Duration Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "voidd_test")
        //Cache Virtual Sotrage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "cache_test")
        //In Memory Cache Virutal storage location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "mcache_test")

        self.unload_test_data()

        //Delete Test
        //Task Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "task_test")
        //Void Duration Virtual Storage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "voidd_test")
        //Cache Virtual Sotrage Location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "cache_test")
        //In Memory Cache Virutal storage location
        self.DB.deleteEntry(lockey: SKMDBKey.task, key: "mcache_test")
    }

    func test_retrieveEntry()
    {
        self.load_test_data()

        //Test Retrieve Data
        //Task Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.task, key: "task_test") as! SKMTask) != nil)


        //Void Duration Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.void_duration, key: "voidd_test") as! SKMVoidDuration) != nil)

        //Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.cache, key: "cache_test")) != nil)

        //In Memory Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.mcache, key: "mcache_test")) != nil)

        //Test Delete Retrieve
        self.unload_test_data()

        //Task Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.task, key: "task_test") as! SKMTask) == nil)

        //Void Duration Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.void_duration, key: "voidd_test") as! SKMVoidDuration) == nil)

        //Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.cache, key: "cache_test")) == nil)

        //In Memory Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.mcache, key: "mcache_test")) == nil)

        self.unload_test_data()
    }

    func test_commit()
    {
        self.load_test_data()

        self.DB.commit()

        self.unload_test_data()
        self.commit = true
    }

    func test_load()
    {
        self.load_test_data()
        self.DB.commit()
        
        self.DB = SKMDatabase() //Reset Database

        do
        {
            try self.DB.load()
        }
        catch SKMDBError.entry_modified
        {
            XCTFail("Failed to load entry due entry has been modified")
        }
        catch
        {
            XCTFail("Unknown Error")
        }

        //Test Retrieve Data
        //Task Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.task, key: "task_test") as! SKMTask) != nil)

        //Void Duration Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.void_duration, key: "voidd_test") as! SKMVoidDuration) != nil)

        //Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.cache, key: "cache_test")) != nil)

        //In Memory Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: SKMDBKey.mcache, key: "mcache_test")) == nil)

        self.unload_test_data()
        self.commit = true

        do
        {
            try self.DB.load()
        }
        catch SKMDBError.entry_modified
        {
            return
        }
        catch
        {
            XCTFail("Unexpected Error")
        }

        XCTFail("Expected Error Failed to Throw: SKMDBError.entry_modified")
    }
}
