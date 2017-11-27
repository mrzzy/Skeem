#
# skeem.py
# Skeem Simulator - Skeem Model Object
# 
# Nov 21, 2017
import datetime
import functools
import copy

#Utility Functions
def epoch_time(time=datetime.datetime.now()):
    return int((time - datetime.datetime(1970,1,1)).total_seconds())

#Prove of concept only, not actually used.
#Rewrite required
class Repetition:
    def __init__(self, interval, stopdate=epoch_time(datetime.datetime.max), \
            offset=0):
        self.offset = offset
        self.interval = interval
        self.stopdate = stopdate
        self.pointer = 0
        self.prevdate = datetime.datetime.now()

    #Create from days of week
    def __init__(self, days_of_week, stopdate=epoch_time(datetime.datetime.max)):
        #Compute Start Offset
        days_of_week = sorted(days_of_week)
        now = datetime.datetime.now()
        td_day = datetime.timedelta(days=1)
        startdate = datetime.datetime.today() + td_day
        while not startdate.weekday() in days_of_week:
            startdate += [ td_day ]
        self.offset = (startdate - now).total_seconds()
        
        #Generate Interval Loop & Compute Index
        interval = []
        self.pointer = days_of_week.index(startdate.weekday())
        prev_day = days_of_week[0]
        for day in days:
            diff_day = abs(day - prev_day)
            interval += td_day * diff_day
            prev_day = day
        self.interval = interval

        self.stopdate = stopdate
        self.prevdate = datetime.datetime.now()
    
    def vaild(self):
        return self.stopdate > datetime.datetime.now()
    
    def next_repeat(self):
        if self.vaild() == True:
            nextdate = self.prevdate
            if not self.offset == 0:
                nextdate += [ datetime.timedelta(seconds=self.offset) ]
                self.offset = 0
            nextdate += [ self.interval[self.pointer] ]
            
            self.pointer +=  1
            self.pointer = self.pointer % len(self.interval)
            self.prevdate = nextdate

            return epoch_time(nextdate)
        else: 
            return None
            
class Schedulable:
    def __init__(self, name, duration, describe="", tags=[], repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.tags = tags
        self.repeat = repeat

    def weigh(self):
        return 1.0

class Task(Schedulable):
    def __init__(self, name, duration, deadline, describe="", tags=[], \
            repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.deadline = deadline
        self.tags = tags
        self.repeat = repeat
        self.weigh = 0.0
    
    def weigh():
        #Computer from tags disabled: see sim.txt
        #weight = 0.0
        #Compute weightt from Tags
        #for tag in self.tags:
            #weight += tag.weight
        #weight /= float(len(self.tags))
        #return weight
        return self.weight
        
class Interrupt(Schedulable):
    def __init__(self, name, duration, begin, describe="", tags=[], repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.begin = begin
        self.tags = tags
        self.repeat = repeat

    def end(self):
        return self.begin + self.duration 

class SchedulingOrder:
    #Ordering
    sequential = 1 << 0
    iterative = 1 << 1
    #Sorting
    nosort = 0 << 2
    sort =  1 << 2
    onesort = 0 << 3
    resort = 1 << 3
    

class SchedulingAlgorithm: #Abstract Class
    def order(self):
        raise NotImplementedError

    def compare(self,lhs, rhs):
        raise NotImplementedError
    
    def schedule(self,task, avail_time):
        raise NotImplementedError

class ScheduleIterator:
    def __init__(self, pointer):
        self.pointer = pointer

    def __eq__(self, other):
        return self.pointer == other.pointer

    @classmethod
    def iterate(cls, schedule, pointer=0):
        cls.vaild = True
        cls.schedule = schedule
        return ScheduleIterator( pointer)
    
    @classmethod
    def invaildate(cls):
        cls.vaild = False
        ScheduleIterator.schedule = None

    def next(self):
        if ScheduleIterator.vaild == True:
            return ScheduleIterator(self.pointer + 1)
        else: return None

    def prev(self):
        if ScheduleIterator.vaild == True:
            return ScheduleIterator(self.pointer - 1)
        else: return None

    def value(self):
        if ScheduleIterator.vaild == True:
            return ScheduleIterator.schedule[self.pointer]
        else: raise ValueError
        

class Schedule:
    def __init__(self,algorithm):
        self.algorithm = algorithm
        self.tasks = []
        self.interrupts = []
        self.flat_tasks = None
        self.flat_interrupts = None
        self.tidx = None
        self.itinerary = False
        self.genesis = epoch_time()
        self.ordered = False

    def switch(self, algorithm):
        self.algorithm = algorithm
        self.invaildate()

    def add(self, schedulable):
        if isinstance(schedulable, Task):
            if not len([t for t in self.tasks if t.name == schedulable.name]) \
                    == 0:
                #Already has Task
                raise ValueError
            self.tasks += [ schedulable ]
        elif isinstance(schedulable, Interrupt) :
            if not len([i for i in self.tasks if i.name == schedulable.name]) \
                    == 0:
                #Already has Interrupt
                raise ValueError 
            self.interrupts += [ schedulable  ]
        else:
            #Type not supported
            raise ValueError
        self.invaildate()
    
    def remove(self, schedulable):
        if isinstance(schedulable, Task):
            self.tasks.remove(schedulable)
        elif isinstance(schedulable, Interrupt):
            self.interrupts.remove(schedulable)
        else:
            #Schedulable not found
            raise ValueError
        self.invaildate()
    
    def update(self, schedulable):
        if isinstance(schedulable, Task):
            for i,sched in enumerate(self.tasks):
                if sched.name == schedulable.name:
                    self.tasks[i] = schedulable
                    break
        elif isinstance(schedulable, Interrupt):
            for i,sched in enumerate(self.interrupts):
                if sched.name == schedulable.name:
                    self.interrupts[i] = schedulable
                    break
        else:
            #Schedulable not found
            raise ValueError
        self.invaildate()
    

    def size(self):
        return len(self.tasks) + len(self.interrupts)
    
    def duration(self):
        total = 0
        for task in self.tasks:
            total += task.duration
        for interrupt in self.interrupts:
            total += interrupt.duration
        return total

        
    def commit(self, genesis=epoch_time()):
        if self.itinerary == None:

            self.genesis = genesis
            #Order State
            self.interrupts = sorted(self.interrupts, \
                    key=(lambda interrupt: interrupt.begin))
            #Unroll Repeats
            self.unroll()
            #Create Itinenary
            self.knit()

            if not len(self.itinerary) >= self.size():
                #Generated itinerary had LESS scheduables than is stored
                #This means that some of the schedulables were NOT scheduled.
                raise AssertionError
            
                
        else: pass # Do nothing if itinerary has not been invaildated
    
    def begin(self):
        if not self.itinerary == None:
            return ScheduleIterator.iterate(self.itinerary, 0)
        #No itinerary to interate
        else: raise ValueError

    def end(self):
        if not self.itinerary == None:
            #Point to one past the last on schedule
            return ScheduleIterator.iterate(self.itinerary, len(self.itinerary)) 
        #No itinerary to interate
        else: raise ValueError
    
    def invaildate(self):
        ScheduleIterator.vaild = False
        self.flat_tasks = None
        self.flat_interrupts = None
        self.itinerary = None
        self.ordered = False
        self.tidx = None
    
    def unroll(self):
        # Stub Implementation - Does not actually unroll schedulables
        self.flat_tasks = self.tasks
        self.flat_interrupts = self.interrupts
        
    def limit(self):
        dlimit = epoch_time() #Current Time
        for task in self.tasks:
            dlimit = task.deadline if task.deadline > dlimit else dlimit
        for interrupt in self.interrupts:
            end = interrupt.end()
            dlimit = end if end > dlimit else dlimit
        return dlimit

    def reorder(self, tasks):
        def cmp(lhs, rhs):
            if self.algorithm.compare(lhs, rhs) == False: return 1 #Swap Position
            else: return -1 #Dont swap position

        if self.algorithm.order() & SchedulingOrder.resort == 0 and self.ordered == True:
            #Not resort and already sorted
            return tasks #Return unsorted tasks
        elif self.algorithm.order() & SchedulingOrder.sort == 0:
            #Sorting disabled
            return tasks #Return unsorted tasks

        #Sort
        stasks = sorted(tasks, key=functools.cmp_to_key(cmp))
        self.ordered = True
        
        return stasks
    
    def knit(self):
        tpointer = self.genesis
        finterrupts = self.flat_interrupts
        irpt_idx = 0
        self.itinerary = []
        
        #Process any interrupts
        while len(finterrupts) > irpt_idx:
            interrupt = finterrupts[irpt_idx]
                
            if tpointer < interrupt.begin:
                #Pointer before interrupt begins
                #Schedulable time from pointer to interrupt begin 
                result = self.schedule(tpointer, interrupt.begin)
                if result == -1:
                    tpointer = interrupt.begin
                else: tpointer = result
            elif tpointer >= interrupt.begin and tpointer < interrupt.end():
                #Pointer is at or during the interrupt
                #Schedule the interrupt and move pointer to end.
                self.itinerary += [ interrupt ]
                tpointer = interrupt.end() 

                irpt_idx += 1
            else:
                #Overlaps in Interrupts detected
                raise AssertionError
            
        #Schedule any tasks left
        result = 0
        while not result == -1:
            result =  self.schedule(tpointer, tpointer + self.duration())
    
    def schedule(self, begin, end):
        if self.tidx == None:
            self.tidx = 0
        
        scheduled = 0
        tpointer = begin
        while (not end - tpointer == 0) and len(self.flat_tasks) > scheduled:
            self.flat_tasks = self.reorder(self.flat_tasks)
            task = self.flat_tasks[self.tidx]

            if task.duration > 0:
                tschedule = self.algorithm.schedule(task, end - tpointer)
                if tschedule > end - tpointer: raise AssertionError
                subtask = copy.deepcopy(task)
                subtask.duration = tschedule
                self.itinerary += [ subtask ]

                tpointer += tschedule
                task.duration -= subtask.duration
            else: scheduled += 1
            
            #Determine next task based on order
            if not self.algorithm.order() & SchedulingOrder.iterative == 0:
                self.tidx = (self.tidx + 1) % len(self.flat_tasks)
            if not self.algorithm.order() & SchedulingOrder.sequential == 0:
                if task.duration > 0: pass #Remain on same task
                else: self.tidx = (self.tidx + 1) % len(self.flat_tasks)
        
        if scheduled == len(self.flat_tasks): #All Tasks scheduled
            return -1
        else: return tpointer

