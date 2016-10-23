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

    //Data
    var dataViewCtrl:PVRDataController
    var dataView:PVRDataView

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

    /*
     * public func generateDuration() -> [PVRDuration]
     * - Generates Schedulable Durations from Void Durations
     * [Return]
     * Array<PVRDuration> - Array of schedulable durations
    */
    public func generateDuration() throws -> [PVRDuration]
    {
        //Data Check
        if self.dataView.voidDuration.count > 1
        {
            throw PVRSchedulerError.DataMissing
        }

        self.dataView.loadFromDB(db: self.dataCtrl.DB) //Reset Data View
        let last_date = self.lastTaskDate()
        var current_time = NSDate()
        var current_voidd = self.dataViewCtrl.sortedVoidDuration(sattr: PVRVoidDurationSort.begin).last!
        var arr_duration = Array<PVRDuration>()

        //Compute Schedulable Duration
        while current_time.compare(current_voidd.begin as Date) == ComparisonResult.orderedAscending
        {
            //current date/time < current void duration begin date/time
            //Duration from current date/time to void duration begin date/time
            let time = current_voidd.begin.timeIntervalSince(current_voidd.begin as Date)
            let duration = PVRDuration(begin: current_time, duration: Int(time))
            arr_duration.append(duration)

            //Prepare for Next Interation
            let time_increment = current_voidd.begin.timeIntervalSince(current_time as Date) + TimeInterval(current_voidd.duration) + 1
            current_time = current_time.addingTimeInterval(time_increment) //Right After Void Duration
            self.dataView.simulateDate(date: current_time)
            self.dataViewCtrl.pruneVoidDuration() //Remove Now Invaild Void Duration
            if self.dataView.voidDuration.count > 1
            {
                break
            }
            else
            {
                current_voidd = self.dataViewCtrl.sortedVoidDuration(sattr: PVRVoidDurationSort.begin).last!
            }
        }
    }

    //public func crt_subtask()
}
