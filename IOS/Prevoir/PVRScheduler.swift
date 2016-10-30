//
//  PVRSchduler.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 10/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public enum PVRSchedulerError:Error
 * - Describles the errors that may occur when using PVRScheduler
*/
public enum PVRSchedulerError:Error
{
    case DurationOverflow /* Schedulable duration is insufficent to schedule all tasks*/
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
    var setting:[String:NSCoding] /*Link to settings in App Delegate*/
    var dataViewCtrl:PVRDataController /*Link to Data View Data controller */
    var dataView:PVRDataView /*Link to Data View */

    //Status
    var sch_date:NSDate /*Date Schedule was generated*/
    var sch_affinity:Bool /*Whether Duration Afinity is followed */

    //Data
    var schedule:[PVRDuration:[PVRTask]] /* Generated Schedule */


    //Data
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
        self.sch_date = NSDate.distantPast as NSDate
        self.sch_affinity = true
        self.schedule = Dictionary<PVRDuration,Array<PVRTask>>()
        self.setting = (UIApplication.shared.delegate as! AppDelegate).setting

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
        self.dataCtrl.updateTask()
        self.dataCtrl.pruneTask()

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
     * public func generateSchedulableDuration()
     * - Genrate an Array of PVRDuration where tasks may be scheduled, sorted by time
     * [Return]
     * Array<PVRDuration> - Array of PVRDuration where tasks may be scheduled
    */
    public func generateSchedulableDuration() -> [PVRDuration]
    {
        //Prepare Data
        self.dataCtrl.updateVoidDuration()
        self.dataCtrl.pruneVoidDuration()
        self.dataView.loadFromDB()
        var date = Date() //Init to Current Date
        var voidd = self.dataViewCtrl.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)[0] //Closest begin date
        var arr_drsn = Array<PVRDuration>()

        //Extract Duration from Void Duration
        while date.compare(self.lastTaskDate() as Date) == ComparisonResult.orderedAscending && self.dataView.voidDuration.count > 0
        {
            //date < last task date
            //Duration from date to voidd.begin
            let tint = voidd.begin.timeIntervalSince(date)
            let drsn = PVRDuration(begin: date as NSDate, duration: Int(round(tint)))
            arr_drsn.append(drsn)

            //Update Data
            let tadd = voidd.duration + 1
            date = (NSDate(timeInterval: TimeInterval(tadd), since: voidd.begin as Date) )as Date //1 Second after voidd
            self.dataView.simulateDate(date: date as NSDate) //Update Repeat Task
            self.dataViewCtrl.pruneVoidDuration()
            voidd = self.dataCtrl.sortedVoidDuration(sattr: PVRVoidDurationSort.begin)[0] //Closest begin date
        }

        //Duration from date to last task date
        let tint = self.lastTaskDate().timeIntervalSince(date)
        let drsn = PVRDuration(begin: (date as NSDate), duration: Int(tint))
        arr_drsn.append(drsn)

        //Cleanup Data
        self.dataView.loadFromDB()

        return arr_drsn
    }

    /*
     * public func generateSubtask(task:PVRTask) -> [PVRTask]
     * - Generate subtasks from task
     * [Return]
     * Array<PVRTask> - Array of subtasks of task, sorted by time
     */
    public func generateSubtask(task:PVRTask) -> [PVRTask]
    {
        //Prepar Data
        var arr_stsk = Array<PVRTask>()
        let stsk_cnt = Int(floor(Double(task.duration / task.duration_affinity)))
        var duration_left = task.duration
        var completion_left = task.completion

        //Generate Subtasks
        for stsk_idx in 0..<stsk_cnt
        {
            let stsk = PVRTask(name: task.name, deadline: task.deadline, duration:task.duration, duration_affinity: task.duration_affinity, subject: task.subject, description: task.descript)

            if stsk_idx == (stsk_cnt - 1)
            {
                //Last Subtask
                stsk.duration = duration_left
                stsk.completion = completion_left
            }
            else
            {
                stsk.duration = task.duration_affinity
                stsk.completion = Double(task.duration_affinity)/Double(task.duration)
                completion_left -= Double(task.duration_affinity)/Double(task.duration)
            }

            duration_left -= stsk.duration
            arr_stsk.append(stsk)
        }

        return arr_stsk
    }

    /*
     * public func genrateAllSubtask() -> [PVRTask]
     * - Generate subtasks from all tasks
     * [Return]
     * Array<PVRTask> - Array of subtasks
    */
    public func genrateAllSubtask() -> [PVRTask]
    {
        //Prepare Data
        self.dataCtrl.updateTask()
        self.dataCtrl.pruneTask()
        self.dataView.loadFromDB()
        var date = Date()
        var task = self.dataViewCtrl.sortedTask(sattr: PVRTaskSort.deadline)[0] //Closest Deadline
        var arr_stsk = Array<PVRTask>()

        //Iterate Tasks
        while date.compare(self.lastTaskDate() as Date) != ComparisonResult.orderedDescending && self.dataView.task.count > 0
        {
            //Generate Subtask
            let task_stsk = self.generateSubtask(task: task)
            for stsk in task_stsk
            {
                arr_stsk.append(stsk)
            }

            //Update Data
            date = (task.deadline.addingTimeInterval(1) as Date)
            self.dataView.simulateDate(date: date as NSDate) //Update Repeat Tasks
            self.dataViewCtrl.pruneTask()
            task = self.dataViewCtrl.sortedTask(sattr: PVRTaskSort.deadline)[0] //Closest Deadline
        }

        //Cleanup Data
        self.dataView.loadFromDB()

        return arr_stsk.reversed()
    }


    /*
     * public validateDurationAdequate() -> Bool
     * - Determines whether schedulable duration is sufficent to schedule all tasks
     * NOTE: Does not determine if duration affinity can be followed.
     * [Return]
     * Bool - Retuns true if schedulable duration sufficent, false otherwise
    */
    public func vaildateDurationAdequate() -> Bool
    {
        //Prepare Data
        var sch_drsn = 0
        var stsk_drsn = 0
        let arr_sch_drsn = self.generateSchedulableDuration()
        let arr_stsk =  self.genrateAllSubtask()

        //Compute Total Schedulable duration
        for drsn in arr_sch_drsn
        {
            sch_drsn += drsn.duration
        }

        //Compute Total Task Duration
        for stsk in arr_stsk
        {
            stsk_drsn += stsk.duration
        }

        return (sch_drsn >= stsk_drsn)
    }

    /*
     * public scheduleTask()
     * - Generate Schedule
     * [Return]
     * [PVRDuration:[PVRTask]] - Generated Schedule.
     * [Error]
     * PVRSchedulerError.DurationOverflow - Schedulable duration is insufficent to schedule all tasks
    */
    public func scheduleTask() throws -> [PVRDuration:[PVRTask]]
    {
        //Validate Sufficent Duration
        if self.vaildateDurationAdequate() == false
        {
            throw PVRSchedulerError.DurationOverflow
        }

        //Prepare Data
        var arr_drsn = self.generateSchedulableDuration()
        var arr_task = (Array(self.dataView.retrieveAllEntry(lockey: PVRDBKey.task).values) as! [PVRTask])
        let date = Date() //Init Current Date
        var schedule = Dictionary<PVRDuration,Array<PVRTask>>()
        var task_idx = 0
        var drsn_idx = 0
        var drsn_left = arr_drsn[drsn_idx].duration //Closest Duration

        //Init Schedular Stavation Status
        self.dataViewCtrl.updateTask()
        self.dataViewCtrl.pruneTask()
        self.dataView.loadFromDB()
        var sch_stsk = Dictionary<String,Array<PVRTask>>()
        var sch_stv = Dictionary<String,Int>()
        for task in arr_task
        {
            sch_stsk[task.name] = self.generateSubtask(task: task).reversed() //Earlist Subtask Last
        }
        let sort_stv = {(tsk1:PVRTask,tsk2:PVRTask) -> Bool
            in
            return sch_stv[tsk1.name]! <= sch_stv[tsk2.name]! //Most Staved First
        }

        //Generate Schedule
        while arr_task.count > 0
        {
            //Update Data
            //Update Stavation Status
            for task in arr_task
            {
                sch_stv[task.name] = Int(task.deadline.timeIntervalSince(date)) / sch_stsk[task.name]!.count
            }
            arr_task = arr_task.sorted(by: sort_stv)
            //Update Duration
            if drsn_left <= 0
            {
                //Next Duration
                drsn_idx += 1
                drsn_left = arr_drsn[drsn_idx].duration
            }
            if drsn_left == arr_drsn[drsn_idx].duration
            {
                //New Duration
                schedule[arr_drsn[drsn_idx]] = Array<PVRTask>()
            }
            if drsn_idx >= (arr_drsn.count - 1)
            {
                //No Duration Left
                //Retry Scheduling without following time afinity
                self.sch_affinity = false
                do
                {
                    schedule = try self.scheduleTask()
                }
                catch PVRSchedulerError.DurationOverflow
                {
                    //No Duration Left

                    throw PVRSchedulerError.DurationOverflow
                }

                break
            }

            //Schedule Subtask
            let task = arr_task[task_idx]
            let stsk = sch_stsk[task.name]!.popLast()!
            if self.sch_affinity == true && drsn_left < stsk.duration
            {
                //Following affinity && Unable fit subtask into duration
                //Try Scheduling another Task's subtask
                task_idx += 1
                continue
            }
            else if self.sch_affinity == false && drsn_left < stsk.duration
            {
                //Unable fit subtask into duration
                //Split subtask into 2 subtasks
                //Subtask 1
                let stsk_bfr = (stsk.copy() as! PVRTask)
                stsk_bfr.duration = drsn_left
                schedule[arr_drsn[drsn_idx]]!.append(stsk_bfr)


                //Subtask 2
                stsk.duration = task.duration - stsk_bfr.duration
                sch_stsk[task.name]!.append(stsk) //Defer Scheduling of Subtask 2

                //Update Data
                drsn_left -= stsk_bfr.duration
                task.duration -= stsk_bfr.duration
                task.completion = stsk_bfr.completion

            }
            else
            {
                //Able fit subtask into duration
                schedule[arr_drsn[drsn_idx]]!.append(stsk)

                //Update Data
                drsn_left -= stsk.duration
                task.duration -= stsk.duration
                task.completion = stsk.completion
                task_idx = 0
            }

            //Prepare for next Iteration
            self.dataViewCtrl.pruneTask()
            arr_task = (Array(self.dataView.retrieveAllEntry(lockey: PVRDBKey.task).values) as! [PVRTask])
        }

        //Cleanup
        self.dataView.loadFromDB()

        //Output Results
        self.sch_date = NSDate() //Init to Current time
        self.schedule = schedule

        return schedule
    }
}
