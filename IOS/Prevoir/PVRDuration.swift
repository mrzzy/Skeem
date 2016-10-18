//
//  PVRDuration.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 18/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public class PVRDuration: NSObject,NSCoding
{
    var begin:NSDate
    var duration:Int

    init(begin:NSDate,duration:Int)
    {
        self.begin = begin
        self.duration = duration
    }

    public required convenience init?(coder aDecoder: NSCoder) {
        let begin = (aDecoder.decodeObject(forKey: "begin") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")

        self.init(begin:begin, duration:duration)
    }

    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration, forKey: "duration")
    }
}

public class PVRVoidDuration: PVRDuration
{
    var name:String
    var asserted:Bool

    init(begin: NSDate, duration: Int, name:String, asserted:Bool = false)
    {
        self.name = name
        self.asserted = asserted

        super.init(begin: begin, duration: duration)
    }

    public override func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration, forKey: "duration")
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

    public func vaild() -> Bool
    {
        if NSDate(timeInterval: Double(self.duration), since: self.begin as Date).compare(Date()) == ComparisonResult.orderedAscending
        {
            return true
        }
        else
        {
            return false
        }
    }
}

public class PVRRepeatVoidDuration: PVRVoidDuration
{
    var repeat_enabled:Bool
    var repeat_loop:[TimeInterval]
    var repeat_index:Int
    var repeat_deadline:NSDate

    init(begin: NSDate, duration: Int, name: String,repeat_loop:[TimeInterval],deadline:NSDate, asserted: Bool = false) {
        self.repeat_enabled = false
        self.repeat_loop = repeat_loop
        self.repeat_index = 0
        self.repeat_deadline = deadline

        super.init(begin: begin, duration: duration, name: name, asserted: asserted)
    }

    public required convenience init?(coder aDecoder: NSCoder) {
        let begin = (aDecoder.decodeObject(forKey: "begin") as! NSDate)
        let duration = aDecoder.decodeInteger(forKey: "duration")
        let name = (aDecoder.decodeObject(forKey: "name") as! String)
        let asserted = aDecoder.decodeBool(forKey: "asserted")
        let repeat_enabled = aDecoder.decodeBool(forKey: "repeat_enabled")
        let repeat_loop = (aDecoder.decodeObject(forKey: "repeat_loop") as! [TimeInterval])
        let repeat_index = aDecoder.decodeInteger(forKey: "repeat_index")
        let repeat_deadline = (aDecoder.decodeObject(forKey: "repeat_deadline") as! NSDate)

        self.init(begin:begin, duration:duration, name:name, repeat_loop:repeat_loop, deadline:repeat_deadline,asserted:asserted)

        self.repeat_enabled = repeat_enabled
        self.repeat_index = repeat_index
    }

    public override func encode(with aCoder: NSCoder) {
        aCoder.encode(self.begin, forKey: "begin")
        aCoder.encode(self.duration, forKey: "duration")
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.asserted,forKey:"asserted")
        aCoder.encode(self.repeat_enabled, forKey: "repeat_enabled")
        aCoder.encode(self.repeat_index, forKey: "repeat_index")
        aCoder.encode(self.repeat_loop, forKey: "repeat_loop")
        aCoder.encode(self.repeat_deadline, forKey: "repeat_deadline")
    }

    public override func vaild() -> Bool
    {
        if self.repeat_enabled == false || self.repeat_deadline.compare(Date()) != ComparisonResult.orderedDescending
        {
            return false
        }
        else
        {
            return true
        }
    }
}

public class PVR
