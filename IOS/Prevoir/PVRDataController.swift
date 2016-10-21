//
//  PVRDataController.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 17/10/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum PVRDataSort
{
    case date
    case name
}

public class PVRDataController: NSObject {

    public weak var DB:PVRDatabase! //Crashes When Database is missing

    init(db:PVRDatabase)
    {
        self.DB = db

        super.init()

        self.pruneTask()
        self.updateTask()
    }

    //Data
    public func pruneTask()
    {
        for (name,task) in self.DB.task
        {
            if task.status_complete() == true
            {
                self.DB.deleteEntry(lockey: PVRDBKey.task, key: name)
            }
        }
    }

    public func updateTask()
    {
        for (_,task) in self.DB.task
        {
            task.nextTask()
        }
    }

    public func createOneshotTask(name:String,subject:String,description:String,deadline:NSDate,duration:Int) -> Bool
    {
        let crt_task = PVRTask(name: name, deadline: deadline, duration: duration , subject: subject,description:description)

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

    public func createRepeativeTask(name:String,subject:String,description:String,repeat_loop:[TimeInterval],duration:Int) -> Bool
    {
        let crt_rttask = PVRRepeatTask(name: name, duration: duration, repeat_loop: repeat_loop, subject: subject,description:description)

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

    public func completeTask(name:String) -> Bool
    {
        if let comp_task = self.DB.task[name]
        {
            comp_task.complete()
            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to complete taks, task does not exists.")
            return false
        }
    }

    public func deleteTask(name:String) -> Bool
    {
        if let del_task = self.DB.task[name]
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


    public func updateTaskStatus(name:String,duration:Int,completion:Double) -> Bool
    {
        if let up_task = self.DB.task[name]
        {
            up_task.duration = duration
            up_task.completion = completion

            do
            {
                try self.DB.updateEntry(lockey: PVRDBKey.task, key: name, val: up_task)
            }
            catch PVRDBError.entry_not_exist
            {
                print("ERR:PVRDataController: Failed to update task, Task does not exist.")
                return false
            }
            catch
            {
                abort()
            }

            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to update task, Task does not exist.")
            return false
        }
    }

    public func adjustTaskStatus(name:String, duration:Int, completion:Double) -> Bool
    {
        if let ad_task = self.DB.task[name]
        {
            ad_task.duration += duration
            ad_task.completion += completion

            do
            {
                try self.DB.updateEntry(lockey: PVRDBKey.task,key: name, val: ad_task)
            }
            catch PVRDBError.entry_not_exist
            {
                print("ERR:PVRDataController: Failed to adjust task, Task does not exist.")
                return false
            }
            catch
            {
                abort()
            }

            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to adjust task, Task does not exist.")
            return false
        }
    }

    public func sortedTask(sorder:PVRDataSort) -> [PVRTask]
    {
        switch sorder {
        case PVRDataSort.date:
            let srt_date_task = self.DB.task.values.sorted(by: {(task1:PVRTask,task2:PVRTask) -> Bool in
                return task1.deadline.compare(task2.deadline as Date) == ComparisonResult.orderedAscending
                })

            return srt_date_task

        case PVRDataSort.name:
            let srt_name_task = self.DB.task.values.sorted(by: {(task1:PVRTask,task2:PVRTask) -> Bool in
                return task1.name < task2.name
            })

            return srt_name_task
        }
    }

    public func createVoidDuration(name:String,duration:Int,asserted:Bool) -> Bool
    {
        let crt_voidd = PVRVoidDuration(begin: NSDate(), duration:duration , name: name, asserted: asserted)

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

    public func createRepeatVoidDuration(name:String,duration:Int,repeat_loop:[TimeInterval],repeat_deadline:NSDate,asserted:Bool) -> Bool
    {
        let crt_rptvoidd = PVRRepeatVoidDuration(begin: NSDate(), duration: duration, name: name, repeat_loop: repeat_loop, deadline: repeat_deadline, asserted: asserted)

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

    public func deleteVoidDuration(name:String) -> Bool
    {
        if let _ = self.DB.voidDuration[name]
        {
            self.DB.deleteEntry(lockey: PVRDBKey.void_duration, key: name)
            return true
        }
        else
        {
            print("ERR:PVRDataController: Failed to delete task, Task does not exist.")
            return false
        }
    }

    public func sortedVoidDuration(sorder:PVRDataSort) -> [PVRVoidDuration]
    {
        switch sorder {
        case PVRDataSort.date:
            let srt_date_voidd = self.DB.voidDuration.values.sorted(by: {(void1:PVRVoidDuration,void2:PVRVoidDuration) -> Bool in
                return NSDate(timeInterval: Double(void1.duration), since: void1.begin as Date).compare(NSDate(timeInterval: Double(void2.duration), since: void2.begin as Date) as Date) == ComparisonResult.orderedAscending
            })

            return srt_date_voidd

        case PVRDataSort.name:
            let srt_name_voidd = self.DB.voidDuration.values.sorted(by: {(void1:PVRVoidDuration,void2:PVRVoidDuration) ->
                Bool in
                return void1.name < void2.name
            })

            return srt_name_voidd
        }
    }
    
    public func commitSchedule(schedule:[PVRTask]) -> Bool
    {
        do
        {
            try self.DB.updateEntry(lockey: PVRDBKey.cache, key: "schedule", val: schedule)
        }
        catch PVRDBError.entry_not_exist
        {
            do
            {
                try self.DB.createEntry(locKey: PVRDBKey.cache, key:"schedule", val: schedule)
            }
            catch
            {
                return false
            }
        }
        catch
        {
            return false
        }

        return true
    }

    public func retrieveSchedule() -> [PVRTask]?
    {
        if let sch = (self.DB.cache["schedule"] as! [PVRTask]?)
        {
            return sch
        }
        else
        {
            print("ERR:PVRDataController: Failed to retrieve schdule, entry does not exist.")
            return nil
        }
    }
}
