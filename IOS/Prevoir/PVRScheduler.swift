//
//  PVRSchduler.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 10/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum PVRSchedulerError:Error
{
    case DataMissing
}


/*
 * public class PVRScheduler : NSObject
 * - Determines Schedulable time from void duration
 * - Determines Size of Task to Schedule
 * - Schedules Tasks based on attribute
*/
public class PVRScheduler: NSObject
{
    //Properties
    //Link
    weak var dataCtrl:PVRDataController! /* Link to Data Controller
                                          * NOTE: Will Terminate executable if Missing
                                         */
    var dataViewCtrl:PVRDataController /*Link to Data View Data controller */
    var dataView:PVRDataView /*Link to Data View */

    //Data
    var scheduleDuration:[PVRDuration] /* Schedulable Duration sorted by date until Last Task Date*/

    //Methods
    /*
     * init(datactrl:PVRDataController)
     * [Arguments]
     * datactrl - PVRDataController to link to
    */
    init(dataCtrl:PVRDataController)
    {
        self.dataCtrl = dataCtrl
        self.dataView = PVRDataView(db: dataCtrl.DB)
        self.dataViewCtrl = PVRDataController(db: self.dataView)
        self.scheduleDuration = []

        super.init()
    }

    /*
     * public func lastTaskDate() -> NSDate
     * - Determine the last determinable date/time deadline for tasks
     * [Return]
     * NSDate - Last Deadline (Last Date in time determinable)
    */
    public func lastTaskDate() -> NSDate
    {
        if let lastTask = self.dataCtrl.sortedTask(sattr: PVRTaskSort.deadline).last
        {
            return lastTask.deadline
        }
        else
        {
            return NSDate() //Current Date/Time
        }
    }

}
