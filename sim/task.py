from datetime import *

class Task(object):
    #This class imitates a task
    #Only simulates core elements of the orignal java class.
    #See: Task.java
    #
    #Properties:
    # name - name of the Task
    # deadline - deadline of the task
    # period_req - Period Required to complete the task
    # period_min - Recommanded subtask period length
    
    def __init__(self, name, deadline, period_req, period_min):
        self.name = name
        self.deadline = deadline
        self.period_req = period_need
        self.period_min = period_min
