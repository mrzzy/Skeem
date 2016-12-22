from datetime import *

class TimeBlock(object):
    #This class imitates a task
    #Only simulates core elements of the orignal java class.
    #See: Task.java
    #
    #Properties:
    # task_scheduled - Timeblock scheduled to the period
    # start - Datetime Start of TimeBlock 
    # period_block - period of the block
    
    def __init__(self, start, stop):
        self.task_scheduled = []
        self.period_block = start - stop
        self.start = start
