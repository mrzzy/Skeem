//
//  PVRDataView.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 23/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public PVRDataView: NSObject
 * - Represents a view of Data.
 * - Able to Simulate Data under certain conditions
 * - Permits non-Persistent Editing of Data
*/
public enum PVRDataViewError:Error
{
    case methodDisabled
}
public class PVRDataView: PVRDatabase
{
    //Methods

    /*
     * init(db:PVRDatabase)
     * [Argument]
     * db - PJVDatabase to copy data from
    */
    init(db:PVRDatabase)
    {
        self.loadFromDB(db: db)
    }

    //Disabled Methods
    public override func load() throws
    {
        print("ERR:PVRDataView: Disabled method load() called")
    }
    
    public override func commit()
    {
        print("ERR:PVRDataView: Disabled method commit() called")
    }

    //Data
    /*
     * public func loadFromDB(db:PVRDatabase)
     * - Copies data from Database db
     * [Argument]
     * db - The Database to copy from
    */
    public func loadFromDB(db:PVRDatabase)
    {
        //Copy Database data
        self.task = db.task
        self.voidDuration = db.voidDuration
        self.cache = db.cache
        self.mcache = db.mcache
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
