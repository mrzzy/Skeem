#
# skeem.py
# Skeem Simulator - Skeem Model Object
# 
# Nov 21, 2017
import datetime
import functools
import copy

def epoch_time(time=datetime.datetime.now()):
    return (time - datetime.datetime(1970,1,1)).total_seconds()


class Tag:
    def __init__(self, name, weight):
        self.name = name
        self.weight = weight


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
            startdate += td_day
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
                nextdate += datetime.timedelta(seconds=self.offset)
                self.offset = 0
            nextdate += self.interval[self.pointer]
            
            self.pointer += 1
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

    def weight(self):
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
    
    def weight():
        weigh = 0.0
        #Compute Weight from Tags
        for tag in self.tags:
            weigh += tag.weight
        weigh /= float(len(self.tags))
        return weigh
        
class Interrupt(Schedulable):
    def __init__(self, name, duration, begin, describe="", tags=[], repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.begin = begin
        self.tags = tags
        self.repeat = repeat

    def end(self):
        return self.begin + datetime.timedelta(seconds=self.duration)

class SchedulingOrder:
    sequential = 0
    cyclic = 1
    #Dynamic Scheduling Order not implemented
    #dynamic = 2
    

class SchedulingAlgorithm: #Abstract Class
    def order(self):
        raise NotImplementedError

    def compare(self,lhs, rhs):
        raise NotImplementedError
    
    def schedule(self,task, avail_time):
        raise NotImplementedError

class ScheduleIterator:
    def __init__(self, schedule, pointer):
        self.schedule = schedule
        self.pointer = pointer

    @classmethod
    def iterate(cls, schedule, pointer=0):
        cls.vaild = True
        return ScheduleIterator(schedule, pointer)
    
    @classmethod
    def invaildate(cls):
        cls.vaild = False

    def next(self):
        if ScheduleIterator.vaild == True:
            return ScheduleIterator(schedule, self.pointer + 1)
        else: return None

    def prev(self):
        if ScheduleIterator.vaild == True:
            return ScheduleIterator(schedule, self.pointer - 1)
        else: return None

    def value(self):
        if ScheduleIterator.vaild == True:
            return self.schedule[self.pointer]
        else: raise ValueError
        

class Schedule:
    def __init__(self,algorithm):
        self.algorithm = algorithm
        self.tasks = []
        self.interrupts = []
        self.flat_tasks = False
        self.flat_interrupts = None
        self.itinerary = False
        self.genesis = epoch_time()

    def switch(self, algorithm):
        self.algorithm = algorithm
        self.invaildate()
        self.commit()

    def add(self, schedulable):
        if isinstance(schedulable, Task.kind):
            if not len([t for t in self.tasks if t.name == schedulable.name]) \
                    == 0:
                #Already has Task
                raise ValueError
            self.tasks += schedulable
        elif isinstance(schedulable, Interrupt.kind) :
            if not len([i for i in self.tasks if i.name == schedulable.name]) \
                    == 0:
                #Already has Interrupt
                raise ValueError 
            self.interrupts += schedulable
        else:
            #Type not supported
            raise ValueError
        self.invaildate()
    
    def remove(self, schedulable):
        if isinstance(schedulable, Task.kind):
            self.tasks.remove(schedulable)
        elif isinstance(schedulable, Interrupt.kind):
            self.interrupts.remove(schedulable)
        else:
            #Schedulable not found
            raise ValueError
        self.invaildate()
    
    def update(self, schedulable):
        if isinstance(schedulable, Task.kind):
            for i,sched in enumerate(this.tasks):
                if sched.name == schedulable.name:
                    this.tasks[i] = schedulable
                    break
        elif isinstance(schedulable, Interrupt.kind):
            for i,sched in enumerate(this.interrupts):
                if sched.name == schedulable.name:
                    this.interrupts[i] = schedulable
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
        return duration

    def commit(self, genesis=epoch_time()):
        if self.itinerary == None and not self.genesis == genesis:
            self.genesis = genesis
            #Order State
            self.interrupt = sorted(self.interrupts, \
                    key=(lambda interrupt: interrupt.begin))
            #Unroll Repeats
            self.unroll()
            #Create Itinenary
            self.generate()
            
        else: pass # Do nothing if itinerary has not been invaildated
    
    def begin(self):
        if not self.itinerary == None:
            return ScheduleIterator.iterate(self.itinerary, 0)
        #No itinerary to interate
        else: raise ValueError

    def end(self):
        if not self.itinerary == None:
            return ScheduleIterator.iterate(self.itinerary, len(self.itinerary)\
                    - 1) #Point to last on schedule
        #No itinerary to interate
        else: raise ValueError
    
    def invaildate(self):
        ScheduleIterator.vaild = False
        self.flat_tasks = None
        self.flat_interrupts = None
        self.itinerary = None
    
    def unroll(self):
        # Stub Implementation - Does not actually unroll schedulables
        self.flat_tasks = self.tasks
        self.flat_interrupts = self.interrupts
        
    def limit(self):
        dlimit = epoch_time() #Current Time
        for task in self.tasks:
            dlimit = task.deadline if task.deadline > dlimit else dlimit
        for interrupt in self.interrupts:
            end = interrupt + duration
            dlimit = end if end > dlimit else dlimit
        return dlimit
    
    def generate(self):
        tasks = sorted(self.flat_tasks, \
                key=functools.cmp_to_key(self.algorithm.compare))
        tasks = copy.deepcopy(tasks)
        pointer = self.genesis
        index = 0
        
        for interrupt in self.flat_interrupts:
            if interrupt.begin <= pointer and \
                interrupt.begin + interrupt.duration > pointer:
                #Interrupt started before or coincides with time pointer and
                #the interrupt has not yet ended.
                #Schedule the interrupt
                    self.itinerary += interrupt
                    pointer = interrupt.begin + interrupt.duration
            elif interrupt.begin > pointer:
                #Interrupt will start after the time pointer
                #Schedulable time from pointer to interrrupt begin time
                #Hence tasks would be scheduled in that time.
                while not interrupt.begin - pointer <= 0:
                    task = tasks[index]
                    time_left = interrupt.begin - pointer

                    #Schedule Task in itinerary
                    time_scheduled = self.algorithm.schedule(task, time_left)
                    #Sanity Check: Dont schedule more than there is available...
                    if time_scheduled > time_left: raise AssertionError
                    subtask = copy.deepcopy(task)
                    subtask.duration = time_scheduled
                    self.itinerary += subtask

                    task.duration -= time_scheduled
                    if task.duration <= 0:
                        del tasks[index]
                    pointer += time_scheduled

                    #Pick Next Task Based on order
                    if self.algorithm.order() == SchedulingOrder.sequential:
                        if task.duration <= 0: pass #Since task is deleted,
                                                #index now points to next task.
                        else: pass #Continue scheduling current task
                    elif self.algorithm.order() == SchedulingOrder.cyclic:
                        if task.duration <= 0: pass #Since task is deleted,
                                                #index now points to next task.
                        else: index = (index + 1) % len(tasks)
                    else: raise NotImplementedError
