//
//  PVRDataController.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 17/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public class PVRDataController: NSObject
 * - Defines an adaptor between the database and other objects
 * - Suppliments functionality provided by database
*/
public class PVRDataController: NSObject
{
    //Properties
    public weak var DB:PVRDatabase! /* Links PVRDataController to Database
                                     * NOTE: Will terminate execuable if Database is missing
                                    */

    //Methods
    /*
     * init(db:PVRDatabase)
     * [Argument]
     * db - Data to link to
    */
    init(db:PVRDatabase)
    {
        self.DB = db

        super.init()

        self.pruneTask()
        self.pruneVoidDuration()
    }

    //Data
    //Data - Tasks
    /*
     * public func pruneTask()
     * - Remove all invaild tasks
    */
    public func pruneTask()
    {
        for (name,task) in (self.DB.retrieveAllEntry(lockey: PVRDBKey.task) as! [String:PVRTask])
        {
            if task.vaild() == false
            {
                self.DB.deleteEntry(lockey: PVRDBKey.task, key: name)
            }
        }
    }

    /*
     * public func updateTask()
     * - Update tasks data for subsequent task for current date
    */
    public func updateTask()
    {
        for (name,task) in (self.DB.retrieveAllEntry(lockey: PVRDBKey.task) as! [String:PVRTask])
        {
            task.update(date: NSDate()) //Update for current date/time
            try! self.DB.updateEntry(lockey: PVRDBKey.task, key: name, val: task)
        }
    }

    /*
     * public func createOneshotTask(name:String,subject:String,description:String,deadline:NSDate,duration:Int) -> Bool
     * - Create Single time Task
     * [Argument]
     * name - name of the task
     * subject - subject of the task
     * description - description of the task
     * deadline - Date/Time that the task must be completed
     * duration - Duration need in seconds to complete task
     * duration_affinity - User desired subtask length
     * [Return]
     * Bool - true if successful in creating task, false otherwise
    */
    public func createOneshotTask(name:String,subject:String,description:String,deadline:NSDate,duration:Int,duration_affinity:Int) -> Bool
    {
        let crt_task = PVRTask(name: name, deadline: deadline, duration: duration ,duration_affinity:duration_affinity, subject: subject,description:description)

        do
        {
            try self.DB.createEntry(locKey: PVRDBKey.task, key: crt_task.name, val: crt_task)
        }
        catch PVRDBError.entry_exist
        {
            print("ERR:PVRDataController: Failed to Create task, entry already exists in database.")
            return false
        }
        catch
        {
            abort()
        }

        return true
    }

    /*
     * public func createRepeativeTask(name:String,subject:String,description:String,repeat_loop:[TimeInterval],duration:Int) -> Bool
     * - Create Repeatable task
     * [Argument]
     * name - name of the task
     * subject - subject of the task
     * description - description of the task
     * repeat_loop - a loop of intervals of time to increment for each repeat.
     * deadline = nil - Date/Time repeat stops
     * duration - Duration need in seconds to complete task
     * duration_affinity - User desired subtask length
     * [Return]
     * Bool - true if successful in creating task, false otherwise
    */
    public func createRepeativeTask(name:String,subject:String,description:String,repeat_loop:[TimeInterval],repeat_deadline:NSDate?=nil,duration:Int,duration_affinity:Int,deadline:NSDate) -> Bool
    {
        let crt_rttask = PVRRepeatTask(name: name, duration: duration,duration_affinity:duration_affinity, repeat_loop: repeat_loop, subject: subject,description:description,deadline:deadline,repeat_deadline:repeat_deadline)

        do
        {
            try self.DB.createEntry(locKey: PVRDBKey.task, key: crt_rttask.name, val:crt_rttask)
        }
        catch PVRDBError.entry_exist
        {
            print("ERR:PVRDataController: Failed to create task, entry already exists in database.")
            return false
        }
        catch
        {
            abort()
        }

        return true
    }

    /*
     * public func updateOneshotTask(name:String,subject:String?=nil,description:String?=nil,deadline:NSDate?=nil,duration:Int?=nil,duration_affinity:Int?=nil) -> Bool
     * - Updates oneshot task specified by name
     * [Argument]
     * name - name of the task
     * subject - subject of the task
     * description - description of the task
     * deadline - Date/Time that the task must be completed
     * duration - Duration need in seconds to complete task
     * duration_affinity - User desired subtask length
     * [Return]
     * Bool - true if successful in creating task, false otherwise
    */
    public func updateOneshotTask(name:String,subject:String?=nil,description:String?=nil,deadline:NSDate?=nil,duration:Int?=nil,duration_affinity:Int?=nil) -> Bool
    {
        var up_task:PVRTask!

        //Obtain task
        do
        {
            up_task = (try self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRTask)
        }
        catch PVRDBError.entry_not_exist
        {
            print("ERR:PVRDataController: Failed to update task, entry does not exist in database")
            return false
        }
        catch
        {
            abort()
        }

        //Update Task Data
        if let sub = subject
        {
            up_task.subject = sub
        }

        if let descript = description
        {
            up_task.descript = descript
        }

        if let dl = deadline
        {
            up_task.deadline = dl
        }

        if let drt = duration
        {
            up_task.duration = drt
        }

        if let drt_aft = duration_affinity
        {
            up_task.duration_affinity = drt_aft
        }

        //Update DataBase
        do
        {
            try self.DB.updateEntry(lockey: PVRDBKey.task, key: name, val: up_task)
        }
        catch
        {
            abort()
        }

        return true
    }

    /*
     * public func updateRepeativeTask(name:String,subject:String?=nil,description:String?=nil,repeat_loop:[TimeInterval]?=nil,repeat_deadline:NSDate?=nil,duration:Int?=nil,duration_affinity:Int?=nil,deadline:NSDate?=nil) -> Bool
     * - Update Repeative Task Data
     * name - name of the task
     * subject - subject of the task
     * description - description of the task
     * repeat_loop - a loop of intervals of time to increment for each repeat.
     * deadline = nil - Date/Time repeat stops
     * duration - Duration need in seconds to complete task
     * duration_affinity - User desired subtask length
     * [Return]
     * Bool - true if successful in creating task, false otherwise
    */
    public func updateRepeativeTask(name:String,subject:String?=nil,description:String?=nil,repeat_loop:[TimeInterval]?=nil,repeat_deadline:NSDate?=nil,duration:Int?=nil,duration_affinity:Int?=nil,deadline:NSDate?=nil) -> Bool
    {
        var up_task:PVRRepeatTask!

        //Obtain task
        do
        {
            up_task = (try self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRRepeatTask)
        }
        catch PVRDBError.entry_not_exist
        {
            print("ERR:PVRDataController: Failed to update task, entry does not exist in database")
            return false
        }
        catch
        {
            abort()
        }

        //Update Task Data
        if let sub = subject
        {
            up_task.subject = sub
        }

        if let descript = description
        {
            up_task.descript = descript
        }

        if let dl = deadline
        {
            up_task.deadline = dl
        }

        if let drt = duration
        {
            up_task.duration = drt
        }

        if let drt_aft = duration_affinity
        {
            up_task.duration_affinity = drt_aft
        }

        if let rpt_lp = repeat_loop
        {
            up_task.repeat_loop = rpt_lp
        }

        if let rpt_dl = repeat_deadline
        {
            up_task.repeat_deadline = rpt_dl
        }

        //Update Database
        do
        {
            try self.DB.updateEntry(lockey: PVRDBKey.task, key: name, val: up_task)
        }
        catch
        {
            abort()
        }

        return false
    }

    /*
     * public func completeTask(name:String) -> Bool
     * - Mark task specified by name as complete
     * [Argument]
     * name - name of the task to mark as complete
     * [Return]
     * Bool - true if successful in marking task as complete, false if task does not exist.
    */
    public func completeTask(name:String) -> Bool
    {
        if let comp_task = try? (self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRTask)
        {
            comp_task.complete()
            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to complete task, task does not exists.")
            return false
        }
    }


    /*
     * public func deleteTask(name:String) -> Bool
     * - Deletes task specified by name
     * [Argument]
     * name - name of the task to delete
     * [Return]
     * Bool - true if successful in deleting task, false if task does not exist.
    */
    public func deleteTask(name:String) -> Bool
    {
        if let del_task = try? (self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRTask)
        {
            del_task.complete()
            self.DB.deleteEntry(lockey: PVRDBKey.task, key: name)

            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to delete task, Task does not exist.")
            return false
        }
    }

    /*
     * public func updateTaskStatus(name:String,duration:Int,completion:Double) -> Bool
     * - Reset task duration and completion to new values
     * NOTE: Will terminate execuable if unknown database error occurs
     * [Argument]
     * name - name of the task
     * duration - duration to reset to
     * completion - completion to reset to
     * [Return]
     * Bool - true if successful in updating task, false if task does not exist.
    */
    public func updateTaskStatus(name:String,duration:Int,completion:Double) -> Bool
    {
        if let up_task = try? (self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRTask)
        {
            up_task.duration = duration
            up_task.completion = completion

            do
            {
                try self.DB.updateEntry(lockey: PVRDBKey.task, key: name, val: up_task)
            }
            catch
            {
                //Unknown Error
                abort() //Terminates executable
            }

            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to update task, Task does not exist.")
            return false
        }
    }

    /*
     * public func adjustTaskStatus(name:String, duration:Int, completion:Double) -> Bool
     * - Admend Task duration and completion relative to current values
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * name - Name of the task
     * duration - duration to be added to the current duration
     * completion - completion to added to the current duration
     * [Return]
     * Bool - true if successful in adjusting task, false if task does not exist.
    */
    public func adjustTaskStatus(name:String, duration:Int, completion:Double) -> Bool
    {
        if let ad_task = try? (self.DB.retrieveEntry(lockey: PVRDBKey.task, key: name) as! PVRTask)
        {
            ad_task.duration += duration
            ad_task.completion += completion

            do
            {
                try self.DB.updateEntry(lockey: PVRDBKey.task,key: name, val: ad_task)
            }
            catch
            {
                //Unkown Error
                abort() //Terminates executable
            }

            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to adjust task, Task does not exist.")
            return false
        }
    }

    /*
     * public func sortedTask(sorder:PVRTaskSort) -> [PVRTask]
     * - Sorts task based on specifed attribute
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * sattr - Attribute to sort by
     * [Return]
     * Array<PVRTask] - Sorted Array of tasks
    */
    public func sortedTask(sattr:PVRTaskSort) -> [PVRTask]
    {
        if let task = (self.DB.retrieveAllEntry(lockey:PVRDBKey.task) as? [String:PVRTask])?.values
        {
            switch sattr
            {
            case PVRTaskSort.name:
                return task.sorted(by:PVRTaskSortFunc.name)
            case PVRTaskSort.deadline:
                return task.sorted(by:PVRTaskSortFunc.deadline)
            case PVRTaskSort.duration:
                return task.sorted(by:PVRTaskSortFunc.duration)
            }
        }
        else
        {
            //Task is missing, should not happen
            abort() //Terminates Executable
        }
    }

    /*
     * public func pruneVoidDuration()
     * - Remove all invaild Void Duration
     */
    public func pruneVoidDuration()
    {
        for (name,voidd) in (self.DB.retrieveAllEntry(lockey: PVRDBKey.void_duration) as! [String:PVRVoidDuration])
        {
            if voidd.vaild() == false
            {
                self.DB.deleteEntry(lockey: PVRDBKey.void_duration, key: name)
            }
        }
    }

    /*
     * public func updateVoidDuration()
     * - Update PVRVoidDuration data for current date
     */
    public func updateVoidDuration()
    {
        for (name,voidd) in (self.DB.retrieveAllEntry(lockey: PVRDBKey.void_duration) as! [String:PVRVoidDuration])
        {
            voidd.update(date: NSDate()) //Update for current date/time
            try? self.DB.updateEntry(lockey: PVRDBKey.void_duration, key: name, val: voidd)
        }
    }

    /*
     * public func createVoidDuration(name:String,duration:Int,asserted:Bool) -> Bool
     * - Create single time void duration
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * name - name of Void Duration
     * begin - begin date/time of void duration
     * duration - duration of void duration in seconds
     * asserted - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
     * [Return]
     * Bool - true if successful in creating void duration, false if entry already exists in database.
    */
    public func createVoidDuration(name:String,begin:NSDate,duration:Int,asserted:Bool) -> Bool
    {
        let crt_voidd = PVRVoidDuration(begin: begin, duration:duration , name: name, asserted: asserted)

        do
        {
            try self.DB.createEntry(locKey: PVRDBKey.void_duration, key: crt_voidd.name, val:  crt_voidd)
        }
        catch PVRDBError.entry_exist
        {
            print("ERR:PVRDataController: Failed to create void duration, entry already exists in database.")
            return false
        }
        catch
        {
            abort()
        }

        return true
    }

    /* 
     * public func createRepeatVoidDuration(name:Stirn,duration:Int,repeat_loop:[TimeInterval],repeat_deadline:NSDate,asserted:Bool) -> Bool
     * - Create Repeatable Void Duration
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * name - name of the void duration
     * begin - begin date/time of void duration
     * duration - duration of void duration in seconds
     * repeat_loop - a loop of intervals of time to increment for each repeat
     * deadline - date/time to stop repeat
     * asserted - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
     * [Return]
     * Bool - true if successful in creating void duration, false if entry already exists in database.
    */
    public func createRepeatVoidDuration(name:String,begin:NSDate,duration:Int,repeat_loop:[TimeInterval],repeat_deadline:NSDate?,asserted:Bool) -> Bool
    {
        let crt_rptvoidd = PVRRepeatVoidDuration(begin: begin, duration: duration, name: name, repeat_loop: repeat_loop, deadline: repeat_deadline, asserted: asserted)

        do
        {
            try self.DB.createEntry(locKey: PVRDBKey.void_duration, key: crt_rptvoidd.name, val:  crt_rptvoidd)
        }
        catch PVRDBError.entry_exist
        {
            print("ERR:PVRDataController: Failed to create void duration, entry already exists in database.")
            return false
        }
        catch
        {
            abort()
        }

        return true
    }

    /*
     * public func updateVoidDuration(name:String,begin:NSDate?=nil,duration:Int?=nil,asserted:Bool?=nil) -> Bool
     * - Update oneshot void duration
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * name - name of Void Duration
     * begin - begin date/time of void duration
     * duration - duration of void duration in seconds
     * asserted - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
     * [Return]
     * Bool - true if successful in creating void duration, false if entry already exists in database.
     */
    public func updateVoidDuration(name:String,begin:NSDate?=nil,duration:Int?=nil,asserted:Bool?=nil) -> Bool
    {
        //Obtain Void Duration
        var voidd:PVRVoidDuration!
        do
        {
            voidd = (try self.DB.retrieveEntry(lockey: PVRDBKey.void_duration, key: name) as! PVRVoidDuration)
        }
        catch PVRDBError.entry_not_exist
        {
            print("ERR:PVRDataController: Faild to update void duration, entry does not exist in database.")
            return false
        }
        catch
        {
            abort()
        }

        //Update Void Duration Data
        if let bgn = begin
        {
            voidd.begin = bgn
        }

        if let drsn = duration
        {
            voidd.duration = drsn
        }

        if let asrt = asserted
        {
            voidd.asserted = asrt
        }


        //Update Database
        do
        {
            try self.DB.updateEntry(lockey: PVRDBKey.void_duration, key: name, val: voidd)
        }
        catch
        {
            abort()
        }

        return true
    }

    /*
     * public func updateRepeatVoidDuration(name:String,begin:NSDate?=nil,duration:Int?=nil,repeat_loop:[TimeInterval]?=nil,repeat_deadline:NSDate??=nil,asserted:Bool?=nil) -> Bool
     * - Create Repeatable Void Duration
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * name - name of the void duration
     * begin - begin date/time of void duration
     * duration - duration of void duration in seconds
     * repeat_loop - a loop of intervals of time to increment for each repeat
     * deadline - date/time to stop repeat
     * asserted - Whether void duration asserts to not have task scheduled during the "Duration of Time", true if void duration asserts
     * [Return]
     * Bool - true if successful in creating void duration, false if entry already exists in database.
    */
    public func updateRepeatVoidDuration(name:String,begin:NSDate?=nil,duration:Int?=nil,repeat_loop:[TimeInterval]?=nil,repeat_deadline:NSDate??=nil,asserted:Bool?=nil) -> Bool
    {
        //Obtain Void Duration
        var voidd:PVRRepeatVoidDuration!
        do
        {
            voidd = (try self.DB.retrieveEntry(lockey: PVRDBKey.void_duration, key: name) as! PVRRepeatVoidDuration)
        }
        catch PVRDBError.entry_not_exist
        {
            print("ERR:PVRDataController: Faild to update void duration, entry does not exist in database.")
            return false
        }
        catch
        {
            abort()
        }

        //Update Void Duration Data
        if let bgn = begin
        {
            voidd.begin = bgn
        }

        if let drsn = duration
        {
            voidd.duration = drsn
        }

        if let asrt = asserted
        {
            voidd.asserted = asrt
        }

        if let rpt_lp = repeat_loop
        {
            voidd.repeat_loop = rpt_lp
        }

        if let rpt_dl = repeat_deadline
        {
            voidd.repeat_deadline = rpt_dl
        }

        //Update Database
        do
        {
            try self.DB.updateEntry(lockey: PVRDBKey.void_duration, key: name, val: voidd)
        }
        catch
        {
            abort()
        }
        
        return true
    }

    /*
     * public func deleteVoidDuration(name:String) -> Bool
     * - Deletes the void duration specifed by name
     * [Argument]
     * name - name of the void duration
     * [Return]
     * Bool - true if successful in deleting void duration, false if void duration does not exist.
    */
    public func deleteVoidDuration(name:String) -> Bool
    {
        if let _ = try? (self.DB.retrieveEntry(lockey: PVRDBKey.void_duration, key: name) as! PVRVoidDuration)
        {
            self.DB.deleteEntry(lockey: PVRDBKey.void_duration, key: name)
            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to delete void duration, void duration does not exist.")
            return false
        }
    }

    /*
     * public func sortedVoidDuration(sattr:PVRVoidDurationSort) -> [PVRVoidDuration]
     * - Sorts stored Void Duration based on specifed attribute
     * NOTE: Will terminate executable if unknown database error occurs
     * [Argument]
     * sattr - Attriute to sort by
     * [Return]
     * Array<PVRVoidDuration> - Sorted array of Void Duration.
    */
    public func sortedVoidDuration(sattr:PVRVoidDurationSort) -> [PVRVoidDuration]
    {
        if let voidd = (self.DB.retrieveAllEntry(lockey: PVRDBKey.void_duration) as? [String:PVRVoidDuration])?.values
        {
            switch sattr
            {
            case PVRVoidDurationSort.name:
                return voidd.sorted(by: PVRVoidDurationSortFunc.name)
            case PVRVoidDurationSort.begin:
                return voidd.sorted(by: PVRVoidDurationSortFunc.begin)
            }
        }
        else
        {
            //Void Duration is Missing, should not happen.
            abort() //Terminates executable
        }
    }

}
