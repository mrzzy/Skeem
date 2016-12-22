from datetime import *
from task import *
from time_block import *
from math import log

class Scheduler(object):
    #This class imitates a task
    #Only simulates core elements of the orignal java class.
    #See: Scheduler.java
    #
    #Properties:
    # name - Name of the Scheduler
    # tasks - List of Tasks to Schedule
    # timeblocks - List of times to schedule
    # schedule - Scheduled Tasks
    
    def __init__(self, name, tasks, timeblocks):
        self.name = name
        self.tasks = tasks
        self.timeblocks = timeblocks
        self.schd = []

    def __str__(self):
        return self.name

    def schd_time(self, deadline):
        schd_time = 0
        for tbck in self.timeblocks:
            diff = deadline - tbck.start
            if diff.total_seconds() >= 0:
                schd_time += max(diff.total_seconds(), tbck.period_block.total_seconds())
        return schd_time
    
    def schedule(self):
        raise NotImplementedError("Abstract Method")

    def check(): #Check if tasks can be scheduled
        raise NotImplementedError("Abstract Method")

class EDFScheduler(Scheduler):
    #Schedules Earlest Deadline first
    def schedule(self):
        self.schd = []
        unschd_tasks = list(self.tasks)
        while len(unschd_tasks) > 0:
            schd_task = unschd_tasks[0]
            for task in unschd_tasks:
                if (task.deadline - schd_task.deadline).total_seconds() > 0:
                    schd_task = task
            self.schd.append(schd_task)
        return self.schd

    def check():
        for task in self.tasks:
            if task.period_block.total_seconds() > self.schd_time(task.deadline):
                return False
        else:
            return True

class LSFScheduler(Scheduler): 
    #Schedules Least Slack first
    def slack(self, task):
        return self.schd_time(task.deadline) - task.period_block.total_seconds() 

    def schedule(self):
        self.schd = []
        unschd_tasks = list(self.task)
        while len(unschd_tasks)> 0:
            schd_task = unschd_tasks[0]
            for task in unschd_tasks:
                if self.slack(task) < self.slack(schd_task):
                    schd_task = task
            self.schd.append(schd_task)
        return self.schd

    def check():
        for task in self.tasks:
            if task.period_block.total_seconds() > self.schd_time(task.deadline):
                return False
        else:
            return True
