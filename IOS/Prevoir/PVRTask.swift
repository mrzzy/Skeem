//
//  PVRTask.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public class PVRTask: NSObject,NSCoding{
    var name:String
    var subject:String
    var deadline:NSDate
    var duration:Int
    var completion:Double = 0.0
    
    init(name:String, deadline:NSDate, duration:Int, subject:String = "No Subject")
    {
        self.name = name
        self.subject = subject
        self.deadline = deadline
        self.duration = duration
    }
    
    required public init?(coder aDecoder: NSCoder) {
        self.name = (aDecoder.decodeObject(forKey: "name") as! String)
        self.subject = (aDecoder.decodeObject(forKey: "subject") as! String)
        self.deadline = (aDecoder.decodeObject(forKey: "deadline") as! NSDate)
        self.duration = aDecoder.decodeInteger(forKey: "duration")
        self.completion = aDecoder.decodeDouble(forKey: "completion")
    }
    
    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.name, forKey: "name")
        aCoder.encode(self.subject, forKey: "subject")
        aCoder.encode(self.deadline, forKey: "deadline")
        aCoder.encode(self.duration , forKey: "duration")
        aCoder.encode(self.completion, forKey: "completion")
    }
    
    public func complete() -> Bool
    {
        return (completion == 1.0) ? true : false
    }
    
    public func commitSubtask(duration:Int,completion:Double)
    {
        self.duration -= duration
        self.completion += completion
    }
}

