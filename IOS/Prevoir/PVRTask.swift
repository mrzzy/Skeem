//
//  PVRTask.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public class PVRTask: NSObject,NSCoding
 * - Defines an objects that represents a task
*/
public class PVRTask: NSObject,NSCoding,NSCopying
{
    //Properties
    //Data
    var name:String /*name of tha task*/
    var subject:String /*subject of the task*/
    var descript:String /*description of the task*/
    var deadline:NSDate /*date/time task must be finished*/
    var duration:Int /*duration that the task takes to complete in seconds*/
    var duration_affinity:Int /* User desired subtask length*/
    var completion:Double = 0.0 /*0.0<=x<=1.0, where x represents how much of the task is completed*/

    /*
     * init(name:String, deadline:NSDate, duration:Int, subject:String,description:String)
     * [Argument]
     * name - name of the task
     * deadline - date/time task must be finished
     * duration - duration of time in seconds that the task needs to complete
     * duration_affinity - user desired subtask length
     * subject - subject of the task
     * description - description of the task
    */
    init(name:String, deadline:NSDate, duration:Int,duration_affinity:Int, subject:String,description:String)
    {
        self.name = name
        self.subject = subject
        self.deadline = deadline
        self.duration = duration
        self.duration_affinity = duration_affinity
        self.descript = description
    }

    //NSCoding
    required public convenience init?(coder aDecoder: NSCoder) {
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let subject = (aDecoder.decodeObject(forKey: "subject") as! String)
        let deadline = (aDecoder.decodeObject(forKey: "deadline") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let duration_affinity = aDecoder.decodeInteger(forKey: "duration_affinity")
        let completion = aDecoder.decodeDouble(forKey: "completion")
        let descript = (aDecoder.decodeObject(forKey: "descript") as! String)

        self.init(name: name, deadline: deadline, duration: duration, duration_affinity:duration_affinity, subject: subject,description:descript)
        self.completion = completion
    }
    
    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.subject, forKey: "subject")
        aCoder.encode(self.deadline, forKey: "deadline")
        aCoder.encode(self.duration , forKey: "duration")
        aCoder.encode(self.duration_affinity, forKey: "duration_affinity")
        aCoder.encode(self.completion, forKey: "completion")
        aCoder.encode(self.descript, forKey: "descript")
    }

    //NSCopying
    public func copy(with zone: NSZone? = nil) -> Any {
        return PVRTask(name: self.name, deadline: self.deadline, duration: self.duration, duration_affinity: self.duration_affinity, subject: self.subject, description: self.descript)
    }

    /*
     * public func vaild() -> Bool
     * - Determines if the task is still vaild
     * [Return]
     * Bool - true if task is vaild, false otherwise
    */
    public func vaild() -> Bool
    {
        if self.deadline.compare(Date()) != ComparisonResult.orderedDescending && self.completion != 1.0
        {
            return false
        }
        else
        {
            return true
        }
    }

    /*
     * public func complete()
     * - Mark task as complete (invailate)
    */
    public func complete()
    {
        self.completion = 1.0
    }

    /*
     * public func update(date:NSDate)
     * - Update task data with date as virtual current date
     *
     * [Argument]
     * date - virtual current date
     * PVRTask::nextVoid() - Does not do anything
     */
    public func update(date:NSDate)
    {

    }

    /*
     * public func priority() -> Double
     * - Returns task priority based on task.duration and task.deadline
     * [Return]
     * Double - Priority of task
    */
    public func priority() -> Double
    {
        let factor = Double(self.duration)
        let base = self.deadline.timeIntervalSinceNow - TimeInterval(self.duration)
        return factor / base
    }
}

/*
 * public class PVRRepeatTask: PVRTask
 * - Defines an object that represents a task that repeats
 */
public class PVRRepeatTask: PVRTask
{
    //Properties
    //Repeat
    var repeat_duration:Int /* Duration of each repeat of the task */
    var repeat_loop:[TimeInterval] /* List of time intervals to increment per repeat of task */
    var repeat_index:Int /* Current position in repeat_loop */
    var repeat_enabled:Bool /* Whether repeat is enabled */
    var repeat_deadline:NSDate? /* date/time when repeat will terminate. */

    //Init
    /*
     * init(name:String, deadline:NSDate, duration:Int, subject:String,description:String)
     * [Argument]
     * name - name of the task
     * duration - duration of time in seconds that the task needs to complete
     * duration_affinity - user desired subtask length
     * repeat_loop - List of time intervals to increment per repeat of task
     * subject - subject of the task
     * description - description of the task
     * deadline - date/time task must be finished
     */
    init(name:String, duration:Int,duration_affinity:Int, repeat_loop:[TimeInterval],subject:String,description:String,deadline:NSDate? = nil)
    {
        self.repeat_loop = repeat_loop
        self.repeat_enabled = true
        self.repeat_index = 0
        self.repeat_duration = duration
        self.repeat_deadline = deadline

        super.init(name: name, deadline: NSDate(), duration:duration, duration_affinity:duration_affinity, subject:subject,description:description)
    }

    //NSCoding
    required public convenience init?(coder aDecoder: NSCoder) {
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let subject = (aDecoder.decodeObject(forKey: "subject") as! String)
        let deadline = (aDecoder.decodeObject(forKey: "deadline") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let duration_affinity = aDecoder.decodeInteger(forKey: "duration_affinity")
        let completion = aDecoder.decodeDouble(forKey: "completion")
        let repeat_loop = (aDecoder.decodeObject(forKey: "repeat_loop") as! [TimeInterval])
        let repeat_enabled = aDecoder.decodeBool(forKey: "repeat_enabled")
        let repeat_index = aDecoder.decodeInteger(forKey: "repeat_index")
        let repeat_duration = aDecoder.decodeInteger(forKey: "repeat_duration")
        let descript = (aDecoder.decodeObject(forKey: "descript") as! String)

        self.init(name: name, duration: duration,duration_affinity:duration_affinity, repeat_loop: repeat_loop, subject: subject,description: descript,deadline: deadline)
        self.completion = completion
        self.repeat_enabled = repeat_enabled
        self.repeat_index = repeat_index
        self.repeat_duration = repeat_duration
    }

    public override func encode(with aCoder: NSCoder) {
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.subject, forKey: "subject")
        aCoder.encode(self.deadline, forKey: "deadline")
        aCoder.encode(self.duration , forKey: "duration")
        aCoder.encode(self.duration_affinity, forKey: "duration_affinity")
        aCoder.encode(self.completion, forKey: "completion")
        aCoder.encode(self.repeat_loop, forKey: "repeat_loop")
        aCoder.encode(self.repeat_enabled, forKey: "repeat_enabled")
        aCoder.encode(self.repeat_index, forKey: "repeat_index")
        aCoder.encode(self.descript, forKey: "descript")
    }

    //NSCopying
    public override func copy(with zone: NSZone?) -> Any {
        return PVRRepeatTask(name: self.name, duration: self.duration, duration_affinity:self.duration_affinity,repeat_loop: self.repeat_loop, subject: self.subject, description: self.description, deadline: self.deadline)
    }
    
    //Data

    /*
     * public func vaild() -> Bool
     * - Determines if the task is still vaild
     * [Return]
     * Bool - true if task is vaild, false otherwise
     */
    public override func vaild() -> Bool {
        if self.repeat_enabled == false || self.repeat_deadline?.compare(Date()) != ComparisonResult.orderedDescending
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
     * - Update task data with date as virtual current date
     *
     * [Argument]
     * date - virtual current date
     * PVRRepeatTask::update() - Updates based on date as current date
     */
    public override func update(date: NSDate)
    {
        if self.deadline.compare(date as Date) == ComparisonResult.orderedAscending
        {
            //current deadline earlier than virtual current date
            //Update Forwards in Time
            while self.deadline.compare(date as Date) == ComparisonResult.orderedAscending
            {
                //Update Void Duration Data
                self.repeat_index = self.repeat_index &+ 1 //Overflow Addition
                let tint = self.repeat_loop[self.repeat_index % self.repeat_loop.count]
                self.deadline = NSDate(timeInterval: tint, since: (self.deadline as Date))
            }
        }
        else if self.deadline.compare(date as Date) == ComparisonResult.orderedDescending
        {
            //current deadline is later than virtual current date
            //Update Backwards in Time
            while self.deadline.compare(date as Date) == ComparisonResult.orderedDescending
            {
                //Update Void Duration Data
                self.repeat_index = self.repeat_index &- 1 //Overflow Subtraction
                let tint = -(self.repeat_loop[self.repeat_index % self.repeat_loop.count]) //Negative Time Interval
                self.deadline = NSDate(timeInterval: tint, since: (self.deadline as Date))
            }
        }
    }
}

/*
 * public enum PVRTaskSort
 * - Defines constants to specify sort attribute
*/
public enum PVRTaskSort
{
    case name //Sort by name
    case deadline //Sort by deadline date/time
    case duration //Sort by duration

}

/*
 * public struct PVRTaskSortFunc
 * - Defines functions for use in sorting PVRTask
*/
public struct PVRTaskSortFunc
{
    /*
     * public func name(task1:PVRTask,task2:PVRTask) -> Bool
     * - Defines sort by name. Sort Stable.   
     * - Smaller Alphanumeric First
    */
    public static func name(task1:PVRTask,task2:PVRTask) -> Bool
    {
        //if task1.name <= task2.name, task1 should before task2
        return task1.name <= task2.name
    }

    /*
     * public func deadline(task1:PVRTask,task2:PVRTask) -> Bool
     * - Defines sort by deadline.Sort Stable.
     * - Earlier deadline first
    */
    public static func deadline(task1:PVRTask,task2:PVRTask) -> Bool
    {
        //if task.deadline <= task2.deadline, task1 should be before task2
        return task1.deadline.compare((task2.deadline as Date)) != ComparisonResult.orderedDescending
    }

    /*
     * public func duration(task1:PVRTask,task2:PVRTask) -> Bool
     * - Defines sort by duration of task. Sort Stable.
     * - Smaller Duration First
    */
    public static func duration(task1:PVRTask,task2:PVRTask) -> Bool
    {
        //if task1.duration <= task2.duration, task1 should be before task2
        return task1.duration <= task2.duration
    }

    /*
     * public func priority(task1:PVRTask,task2:PVRTask) -> Bool
     * - Defines sort by priority defined by task. Sort Stable.
     * - Smaller Duration First
    */
    public static func priority(task1:PVRTask,task2:PVRTask) -> Bool
    {
        //if task.deadline <= task2.deadline, task1 shoudl be before task2
        return task1.priority() <= task2.priority()
    }
}
