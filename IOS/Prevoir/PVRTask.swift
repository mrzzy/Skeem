//
//  PVRTask.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public protocol PVRTaskProt
{
    //Status Func
    func status_complete() -> Bool

    //Manipulation Func
    func complete()
    func nextTask()
}

public class PVRTask: NSObject,NSCoding,PVRTaskProt{
    var name:String
    var subject:String
    var descript:String
    var deadline:NSDate
    var duration:Int
    var completion:Double = 0.0
    
    init(name:String, deadline:NSDate, duration:Int, subject:String,description:String)
    {
        self.name = name
        self.subject = subject
        self.deadline = deadline
        self.duration = duration
        self.descript = description
    }
    
    required public convenience init?(coder aDecoder: NSCoder) {
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let subject = (aDecoder.decodeObject(forKey: "subject") as! String)
        let deadline = (aDecoder.decodeObject(forKey: "deadline") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let completion = aDecoder.decodeDouble(forKey: "completion")
        let descript = (aDecoder.decodeObject(forKey: "descript") as! String)

        self.init(name: name, deadline: deadline, duration: duration, subject: subject,description:descript)
        self.completion = completion
    }
    
    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.subject, forKey: "subject")
        aCoder.encode(self.deadline, forKey: "deadline")
        aCoder.encode(self.duration , forKey: "duration")
        aCoder.encode(self.completion, forKey: "completion")
        aCoder.encode(self.descript, forKey: "descript")
    }

    public func status_complete() -> Bool
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

    public func complete()
    {
        self.completion = 1.0
    }

    public func nextTask() {
        //Do Nothing
    }
}

/*
 PVRRepeatTask describes an repeatitive task, with the following admendments to the following variables
 | PVRRepeatTask.deadline points to the next due date
 | PVRRepeatTask.duration and PVRRepeatTask.point to current task

*/
public class PVRRepeatTask: PVRTask
{
    var repeat_duration:Int
    var repeat_loop:[TimeInterval]  //Repeat Run Loop
    var repeat_index:Int
    var repeat_enabled:Bool
    var repeat_deadline:NSDate?

    init(name:String, duration:Int, repeat_loop:[TimeInterval],subject:String,description:String,deadline:NSDate? = nil)
    {
        self.repeat_loop = repeat_loop
        self.repeat_enabled = true
        self.repeat_index = 0
        self.repeat_duration = duration
        self.repeat_deadline = deadline

        super.init(name: name, deadline: NSDate(), duration:duration , subject:subject,description:description)
        self.nextTask()
    }

    required public convenience init?(coder aDecoder: NSCoder) {
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let subject = (aDecoder.decodeObject(forKey: "subject") as! String)
        let deadline = (aDecoder.decodeObject(forKey: "deadline") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let completion = aDecoder.decodeDouble(forKey: "completion")
        let repeat_loop = (aDecoder.decodeObject(forKey: "repeat_loop") as! [TimeInterval])
        let repeat_enabled = aDecoder.decodeBool(forKey: "repeat_enabled")
        let repeat_index = aDecoder.decodeInteger(forKey: "repeat_index")
        let repeat_duration = aDecoder.decodeInteger(forKey: "repeat_duration")
        let descript = (aDecoder.decodeObject(forKey: "descript") as! String)

        self.init(name: name, duration: duration,repeat_loop: repeat_loop, subject: subject,description: descript,deadline: deadline)
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
        aCoder.encode(self.completion, forKey: "completion")
        aCoder.encode(self.repeat_loop, forKey: "repeat_loop")
        aCoder.encode(self.repeat_enabled, forKey: "repeat_enabled")
        aCoder.encode(self.repeat_index, forKey: "repeat_index")
        aCoder.encode(self.descript, forKey: "descript")
    }

    public override func status_complete() -> Bool {
        if self.repeat_enabled == false || self.repeat_deadline?.compare(Date()) != ComparisonResult.orderedDescending
        {
            return true
        }
        else
        {
            return false
        }
    }

    public override func nextTask() {
        //Update Only if deadline is outdated
        if self.deadline.compare(Date()) != ComparisonResult.orderedDescending
        {
            //Reset Variables
            self.duration = self.repeat_duration
            self.completion = 0.0

            //Compute Next Deadline
            self.repeat_index += 1
            let tint = self.repeat_loop[self.repeat_index % self.repeat_index]
            self.deadline = NSDate(timeInterval: tint, since: self.deadline as Date)
        }
    }

}
