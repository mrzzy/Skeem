//
//  PVRSchduler.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 10/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public class PVRScheduler : NSObject
 * - Determines Schedulable time from void duration
 * - Schedules Tasks based on attribute
*/
public class PVRScheduler: NSObject
{
    //Properties
    var sch_duration:[PVRDuration] /* Duration where we can schedule tasks*/
    var sch_list:[PVRTask] /* Tasks determined pirority */
}
