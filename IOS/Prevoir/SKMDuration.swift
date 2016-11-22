//
//  SKMDuration.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 18/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/* 
 * public class SKMDuration: NSObject,NSCoding
 * - Defines a Duration of Time
*/
public class SKMDuration: NSObject,NSCoding,NSCopying
{
    //Properties
    public var begin:NSDate /*Defines the start date/time of "Duration of time"*/
    public var duration:Int /*Defines the time in seconds of the "Duration of time"*/

    //Methods
    /*
     * init(begin:NSDate, duration:Int)
     * [Argument]
     * begin - Start Date/Time
     * duration - Time in seconds of the "Duration of time"
    */
    init(begin:NSDate,duration:Int)
    {
        self.begin = begin
        self.duration = duration
    }


    //NSCoding
    public required convenience init?(coder aDecoder: NSCoder) {
        let begin = (aDecoder.decodeObject(forKey: "begin") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")

        self.init(begin:begin, duration:duration)
    }

    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration, forKey: "duration")

    }

    //NSCopying
    public func copy(with zone: NSZone? = nil) -> Any {
        return SKMDuration(begin: self.begin, duration: self.duration)
    }
}

/*
 * public class SKMVoidDuration: SKMDuration
 * - Defines a "void" Duration of time where tasks should not be scheduled.
*/
public class SKMVoidDuration: SKMDuration
{
    //Properties
    public var name:String /*Defines the name of the void duration*/
    public var asserted:Bool /*Defines whether the void duration asserts to not have task scheduled during the "Duration of Time"*/
    public var date:NSDate /* Defines the virtual current date*/

    //Methods
    /*
     * init(begin: NSDate, duration: Int, name: String,repeat_loop:[TimeInterval],deadline:NSDate, asserted: Bool = false)
     * [Argument]
     * begin - Start Date/Time
     * duration - Time in seconds of the "Duration of time" NOTE: For accurate scheduling, duration is decremented
     * name - name of the void duration
     * asserted = false - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
    */
    init(begin: NSDate, duration: Int, name:String, asserted:Bool = false)
    {
        self.name = name
        self.asserted = asserted
        self.date = NSDate() //Init To Current Time

        super.init(begin: begin, duration: duration - 1) //NOTE: For accurate scheduling, duration is decremented
    }

    //NSCoding
    public override func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration + 1, forKey: "duration") //NOTE: For accurate scheduling, duration is incremented
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.asserted,forKey:"asserted")
    }

    public required convenience init?(coder aDecoder: NSCoder) {
        let begin = (aDecoder.decodeObject(forKey: "begin") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let asserted = aDecoder.decodeBool(forKey: "asserted")

        self.init(begin:begin, duration:duration, name:name, asserted:asserted)
    }

    //NSCopying
    public override func copy(with zone: NSZone?) -> Any {
        return SKMVoidDuration(begin: self.begin, duration: self.duration, name: self.name, asserted: self.asserted)
    }

    //Data
    /*
     * public func vaild() -> Bool
     * - Check if void duration is vaild
     *  
     * [Return]
     * Bool - Retuns true if task is vaild, false otherwise
    */
    public func vaild() -> Bool
    {
        if NSDate(timeInterval: Double(self.duration), since: self.begin as Date).compare(self.date as Date) == ComparisonResult.orderedDescending
        {
            return true
        }
        else
        {
            return false
        }
    }

    /*
     * public func update(date:NSDate)
     * - Reset Void Duration with date as the  virtual current date
     * [Argument]
     * date - Virtual current date
    */
    public func update(date:NSDate)
    {
        self.date = date
    }
}

/*
 * public class SKMRepeatVoidDuration: SKMVoidDuration
 * - Defines a "void" Duration of time which task should not be scheduled.
 * - Able to reschdule "void" Duration of time based on repeat_data
*/
public class SKMRepeatVoidDuration: SKMVoidDuration
{
    //Properties
    //Repeat Data
    public var repeat_enabled:Bool /*Defines whether repeat of void duration" is enabled*/
    public var repeat_loop:[TimeInterval] /*Defines a loop of intervals of time in seconds to increment for each repeat*/
    public var repeat_index:Int /*Defines the current position in thne repeat_loop*/
    public var repeat_deadline:NSDate? /*Defines a date/time that repeat stops*/
    public var repeat_duration:Int /*Defines the duration of each repeat*/

    //Methods
    /*
     * init(begin: NSDate, duration: Int, name: String,repeat_loop:[TimeInterval],deadline:NSDate, asserted: Bool = false)
     * [Argument]
     * begin - Start Date/Time
     * duration - Time in seconds of the "Duration of time"
     * name - name of the void duration
     * repeat_loop - a loop of intervals of time to increment for each repeat.
     * deadline - date/time that repeat stops
     * asserted = false - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
     */
    init(begin: NSDate, duration: Int, name: String,repeat_loop:[TimeInterval],deadline:NSDate? = nil, asserted: Bool = false) {
        self.repeat_enabled = true
        self.repeat_loop = repeat_loop
        self.repeat_index = 0
        self.repeat_deadline = deadline
        self.repeat_duration = duration

        super.init(begin: begin, duration: duration, name: name, asserted: asserted)
        
    }

    //NSCoding
    public required convenience init?(coder aDecoder: NSCoder) {
        let begin = (aDecoder.decodeObject(forKey: "begin") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let asserted = aDecoder.decodeBool(forKey: "asserted")
        let repeat_enabled = aDecoder.decodeBool(forKey: "repeat_enabled")
        let repeat_loop = (aDecoder.decodeObject(forKey: "repeat_loop") as! [TimeInterval])
        let repeat_index = aDecoder.decodeInteger(forKey: "repeat_index")
        let repeat_deadline = (aDecoder.decodeObject(forKey: "repeat_deadline") as! NSDate)
        let repeat_duration =  aDecoder.decodeInteger(forKey: "repeat_duration")

        self.init(begin:begin, duration:duration, name:name, repeat_loop:repeat_loop, deadline:repeat_deadline,asserted:asserted)

        self.repeat_duration = repeat_duration
        self.repeat_enabled = repeat_enabled
        self.repeat_index = repeat_index
    }

    public override func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration + 1, forKey: "duration") //NOTE: For accurate scheduling, duration is incremented
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.asserted,forKey:"asserted")
        aCoder.encode(self.repeat_enabled, forKey: "repeat_enabled")
        aCoder.encode(self.repeat_index, forKey: "repeat_index")
        aCoder.encode(self.repeat_loop, forKey: "repeat_loop")
        aCoder.encode(self.repeat_deadline, forKey: "repeat_deadline")
        aCoder.encode(self.repeat_duration, forKey: "repeat_duration")
    }

    //NSCopying
    public override func copy(with zone: NSZone?) -> Any {
        return SKMRepeatVoidDuration(begin: self.begin, duration: self.repeat_duration, name: self.name, repeat_loop: self.repeat_loop, deadline: self.repeat_deadline, asserted: self.asserted)
    }

    //Data
    /*
     * public func vaild() -> Bool
     * - Check if void duration is vaild
     * 
     * [Return]
     * Bool - Retuns true if task is vaild, false otherwise
     */
    public override func vaild() -> Bool
    {
        if self.repeat_enabled == false || self.repeat_deadline?.compare(Date()) == ComparisonResult.orderedDescending
        {
            return false
        }
        else
        {
            return true
        }
    }


    /*
     * public func update(date:NSDate)
     * - Update Void Duration data with date as virtual current date
     *
     * [Argument]
     * date - virtual current date
     * SKMRepeatVoidDuration::nextVoid() - Updates based on date as current date
     */
    public override func update(date: NSDate)
    {
        //Prepare Data
        var rpt_idx_fwd = self.repeat_index &+ 1 //Overflow Addition
        var tint_fwd = abs(self.repeat_loop[rpt_idx_fwd % self.repeat_loop.count])

        var rpt_idx_bwd = abs(self.repeat_index &- 1) //Overflow Subtraction
        var tint_bwd = -(self.repeat_loop[rpt_idx_bwd % self.repeat_loop.count])

        if self.begin.addingTimeInterval(TimeInterval(self.duration)).compare(date as Date) == ComparisonResult.orderedAscending
        {
            while self.begin.addingTimeInterval(TimeInterval(self.duration)).compare(date as Date) == ComparisonResult.orderedAscending
            {

                //current date/time is later then begin + duration
                //Update Void Duration Data
                rpt_idx_fwd = rpt_idx_fwd &+ 1 //Overflow Addition
                tint_fwd = self.repeat_loop[rpt_idx_fwd % self.repeat_loop.count]
                self.begin = NSDate(timeInterval: tint_fwd, since: (self.begin as Date))
                self.duration = self.repeat_duration - 1 //NOTE: For accurate scheduling, duration is decremented
            }
            self.repeat_index = rpt_idx_fwd
        }
        else if self.begin.addingTimeInterval(tint_bwd).compare(date as Date) == ComparisonResult.orderedDescending
        {
            //current date/time is earlier then previous begin
            //Update Backwards in Time
            while self.begin.addingTimeInterval(tint_bwd).compare(date as Date) == ComparisonResult.orderedDescending
            {
                //Update Void Duration Data
                rpt_idx_bwd = rpt_idx_bwd &- 1 //Overflow Subtraction
                tint_bwd = -(self.repeat_loop[self.repeat_index % self.repeat_loop.count]) //Negative Time Interval
                self.begin = NSDate(timeInterval: tint_bwd, since: (self.begin as Date))
                self.duration = self.repeat_duration - 1 //NOTE: For accurate scheduling, duration is decremented
            }
        }
    }
}

/*
 * public enum SKMVoidDurationSort
 * - Defines constants that specify sort atttriable.
*/
public enum SKMVoidDurationSort
{
    case name //Sort by name
    case begin //Sort by begin date/time
}

/*
 * public struct SKMVoidDurationSortFunc
 * - Defines functions for use in sorting SKMVoidDuration
*/
public struct SKMVoidDurationSortFunc
{
    /*
     * public func name(voidd1:SKMVoidDuration,voidd2:SKMVoidDuartion) -> Bool
     * - Defines sort by name. Sort Stable.
     * - Smaller Alphanumeric Order first
    */
    public static func name(voidd1:SKMVoidDuration,voidd2:SKMVoidDuration) -> Bool
    {
        //if voidd1.name <= void2.name, voidd1 before voidd2
        return voidd1.name <= voidd2.name
    }

    /*
     * public func begin(voidd1:SKMVoidDuration,voidd2:SKMVoidDuartion) -> Bool
     * - Define sort by begin date. Sort Stable.
     * - Earlier begin date first
    */
    public static func begin(voidd1:SKMVoidDuration,voidd2:SKMVoidDuration) -> Bool
    {
        //if voidd1.begin <= voidd2.begin, voidd1 before voidd2
        return voidd1.begin.compare((voidd2.begin as Date)) != ComparisonResult.orderedDescending
    }
}
