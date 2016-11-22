//
//  PVRDatabaseTest.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 22/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import XCTest
@testable import Prevoir

class PVRDatabaseTest: XCTestCase {
    var DB:PVRDatabase!

    override func setUp() {
        super.setUp()
        self.DB = PVRDatabase()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func load_test_data()
    {
        //Task Virtual Storage Location
        try! self.DB.createEntry(locKey: PVRDBKey.task, key: "task_test", val: PVRTask(name: "task_test", deadline: NSDate() as NSDate, duration: 0, duration_affinity: 0, subject: "test", description: "This is a test task"))

        //Void Duration Storage Location
        try! self.DB.createEntry(locKey: PVRDBKey.void_duration, key: "voidd_test", val: PVRVoidDuration(begin: NSDate() as NSDate, duration: 0, name: "voidd_test", asserted: false))

        //Cache VIrtual Storage Location
        try!self.DB.createEntry(locKey: PVRDBKey.cache, key: "cache_test", val: "test_value")

        //In Memory Cache Virtual Storage Location
        try! self.DB.createEntry(locKey: PVRDBKey.mcache, key: "mcache_test", val: "test_value")
    }

    func unload_test_data()
    {
        //Task Virtual Storage Location
        self.DB.deleteEntry(lockey: PVRDBKey.task, key: "task_test")
        //Void Duration Virtual Storage Location
        self.DB.deleteEntry(lockey: PVRDBKey.void_duration, key: "voidd_test")
        //Cache Virtual Sotrage Location
        self.DB.deleteEntry(lockey: PVRDBKey.cache, key: "cache_test")
        //In Memory Cache Virutal storage location
        self.DB.deleteEntry(lockey: PVRDBKey.mcache, key: "mcache_test")
    }
    
    func test_init()
    {
        self.DB = PVRDatabase()

        XCTAssert(self.DB != nil)
    }

    func test_createEntry()
    {
        do
        {
            //Task Virtual Storage Location
            try self.DB.createEntry(locKey: PVRDBKey.task, key: "task_test", val: PVRTask(name: "task_test", deadline: NSDate() as NSDate, duration: 0, duration_affinity: 0, subject: "test", description: "This is a test task"))

            //Void Duration Storage Location
            try self.DB.createEntry(locKey: PVRDBKey.void_duration, key: "voidd_test", val: PVRVoidDuration(begin: NSDate() as NSDate, duration: 0, name: "voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.createEntry(locKey: PVRDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.createEntry(locKey: PVRDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch PVRDBError.entry_exist
        {
            XCTFail("Database failed to create test entry")
        }
        catch
        {
            XCTFail("Unkown Error")
        }

        self.unload_test_data()
    }

    func test_updateEntry()
    {
        self.load_test_data()

        //Update Test
        do
        {
            //Task Virtual Storage Location
            try self.DB.updateEntry(locKey: PVRDBKey.task, key: "task_test", val: PVRTask(name: "update_task_test", deadline: (NSDate() as NSDate), duration: 0, duration_affinity: 0, subject: "update test", description: "This is an updated test task"))

            //Void Duration Storage Location
            try self.DB.updateEntry(locKey: PVRDBKey.void_duration, key: "voidd_test", val: PVRVoidDuration(begin: (NSDate() as NSDate), duration: 0, name: "update_voidd_test", asserted: false))

            //Cache VIrtual Storage Location
            try self.DB.updateEntry(locKey: PVRDBKey.cache, key: "cache_test", val: "test_value")

            //In Memory Cache Virtual Storage Location
            try self.DB.updateEntry(locKey: PVRDBKey.mcache, key: "mcache_test", val: "test_value")
        }
        catch PVRDBError.entry_not_exist
        {
            XCTFail("Entry to update does not exist in database")
        }
        catch
        {
            XCTFail("Unknown Error")
        }

        self.unload_test_data()
    }

    func test_deleteEntry()
    {
        self.load_test_data()

        //Delete Test
        //Task Virtual Storage Location
        self.DB.deleteEntry(lockey: PVRDBKey.task, key: "task_test")
        //Void Duration Virtual Storage Location
        self.DB.deleteEntry(lockey: PVRDBKey.task, key: "voidd_test")
        //Cache Virtual Sotrage Location
        self.DB.deleteEntry(lockey: PVRDBKey.task, key: "cache_test")
        //In Memory Cache Virutal storage location
        self.DB.deleteEntry(lockey: PVRDBKey.task, key: "mcache_test")

        self.unload_test_data()
    }

    func test_retrieveEntry()
    {
        self.load_test_data()

        //Test Retrieve Data
        //Task Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.task, key: "task_test") as! PVRTask) != nil)

        //Void Duration Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.void_duration, key: "voidd_test") as! PVRVoidDuration) != nil)

        //Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.cache, key: "cache_test")) != nil)

        //In Memory Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.mcache, key: "mcache_test")) != nil)

        //Test Delete Retrieve
        self.unload_test_data()

        //Task Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.task, key: "task_test") as! PVRTask) == nil)

        //Void Duration Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.void_duration, key: "voidd_test") as! PVRVoidDuration) == nil)

        //Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.cache, key: "cache_test")) == nil)

        //In Memory Cache Virtual Storage Location
        XCTAssert((try? self.DB.retrieveEntry(lockey: PVRDBKey.mcache, key: "mcache_test")) == nil)

        self.unload_test_data()
    }
}
