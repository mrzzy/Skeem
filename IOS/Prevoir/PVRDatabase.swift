//
//  PVRDatabase.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 13/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public class PVRDatabase: NSObject,NSCoding {
    var task:[String:PVRTask]
    var schedule: [[PVRTask]]
    
    override init()
    {
        self.task = [:]
        self.schedule = [[]]
    }
    
    public required init?(coder aDecoder: NSCoder)
    {
        self.task = (aDecoder.decodeObject(forKey: "task") as! [String:PVRTask])
        self.schedule = (aDecoder.decodeObject(forKey: "schedule") as! [[PVRTask]])
    }
    
    public func encode(with aCoder: NSCoder) {
        aCoder.encode(self.task, forKey:"task")
        aCoder.encode(self.schedule, forKey:"schedule")
    }
    
    
}
