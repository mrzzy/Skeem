//
//  SKMDataView.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 23/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public SKMDataView: NSObject
 * - Represents a view of Data.
 * - Able to Simulate Data under certain conditions
 * - Permits non-Persistent Editing of Data
*/
public enum SKMDataViewError:Error
{
    case methodDisabled
}
public class SKMDataView: SKMDatabase
{
    //Properties
    weak var DB:SKMDatabase!


    //Methods

    /*
     * init(db:SKMDatabase)
     * [Argument]
     * db - PJVDatabase to copy data from
    */
    init(db:SKMDatabase)
    {
        self.DB = db

        super.init()
        
        self.loadFromDB()
    }

    //Disabled Methods
    public override func load() throws
    {
        print("ERR:SKMDataView: Disabled method load() called")
    }
    
    public override func commit()
    {
        print("ERR:SKMDataView: Disabled method commit() called")
    }

    //Data
    /*
     * public func loadFromDB()
     * - Copies data from Database
    */
    public func loadFromDB()
    {
        //Copy Database data
        for (name,task) in self.DB.task
        {
            self.task[name] = (task.copy() as! SKMTask)
        }
        for (name,voidd) in self.DB.voidDuration
        {
            self.voidDuration[name] = (voidd.copy() as! SKMVoidDuration)
        }

        self.cache = self.DB.cache
        self.mcache = self.DB.mcache
    }

    //Simulation
    /*
     * public func simulateDate(date:NSDate)
     * - Simulates current date as date
     * [Argument]
     * date - date/time to simulate as current date.
    */
    public func simulateDate(date:NSDate)
    {
        //Update Task
        for (_,task) in self.task
        {
            task.update(date: date)
        }

        //Update Void Duration
        for (_,voidd) in self.voidDuration
        {
            voidd.update(date: date)
        }
    }

}
