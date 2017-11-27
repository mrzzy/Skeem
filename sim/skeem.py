#
# skeem.py
# Skeem Simulator - Skeem Model Object
# 
# Nov 21, 2017
import datetime
import functools
import copy
import pprint

#Program Output
def pdivider():
    print("================================================================================")
    print()

def pretty(arg):
    pprint.pprint(arg, indent=2)
    
def proppretty(arg):
    pretty(dict((name, getattr(arg, name)) for name in dir(arg) if not name.startswith('__')))


def epoch_time(time=datetime.datetime.now()):
    return int((time - datetime.datetime(1970,1,1)).total_seconds())

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
    onesort = 0 << 3 | 1 << 2
    resort = 1 << 3 | 1 << 2
    

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

    def __eq__(self, other):
        return self.pointer == other.pointer

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
        return duration

    def commit(self, genesis=epoch_time()):
        if self.itinerary == None:

            self.genesis = genesis
            #Order State
            self.interrupts = sorted(self.interrupts, \
                    key=(lambda interrupt: interrupt.begin))
            #Unroll Repeats
            self.unroll()
            #Create Itinenary
            self.generate()

            pdivider()
            pretty(self.itinerary)
            pdivider()
            #for schd in self.itinerary:
            #    print(schd)
            #    if isinstance(schd, Task):
            #        print("time:" + str(schd.deadline - self.genesis))
            #    elif isinstance(schd, Interrupt):
            #        print("time:" + str(schd.begin - self.genesis))
                
                
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

    def reorder(self, tasks):
        if not self.algorithm.order() & SchedulingOrder.resort == 0:
            pass #Sort
        elif not self.algorithm.order() & SchedulingOrder.sort == 0 and \
                self.ordered ==  False:
            pass
        else: #Assume nosort
            return tasks #Return unsorted tasks

        tasks = sorted(tasks, \
            key=functools.cmp_to_key(self.algorithm.compare))
        self.ordered = True
        return tasks
    
    def generate(self):
        tasks = self.flat_tasks
        tasks = self.reorder(tasks)
        task_index = 0

        interrupts = self.flat_interrupts
        irpt_index = 0

        pointer = self.genesis
        self.itinerary = []
        
        
        while len(interrupts) > irpt_index:
            interrupt = interrupts[irpt_index]

            print(pointer)
            
            if interrupt.begin <= pointer and \
                interrupt.begin + interrupt.duration > pointer:
                #Interrupt started before or coincides with time pointer and
                #the interrupt has not yet ended.
                #Schedule the interrupt
                    self.itinerary += [ interrupt ]
                    pointer = interrupt.begin + interrupt.duration
                    irpt_index += 1
            elif interrupt.begin > pointer and len(tasks) > 0:
                #Interrupt will start after the time pointer
                #Schedulable time from pointer to interrrupt begin time
                #Hence tasks would be scheduled in that time.
                while not interrupt.begin - pointer <= 0 and len(tasks) > 0:
                    task = tasks[task_index]
                    time_left = interrupt.begin - pointer

                    #Schedule Task in itinerary
                    time_scheduled = self.algorithm.schedule(task, time_left)
                    #Sanity Check: Dont schedule more than there is available...
                    if time_scheduled > time_left: 
                        raise AssertionError
                    subtask = copy.deepcopy(task)
                    subtask.duration = time_scheduled
                    self.itinerary += [ subtask ]
                    
                    task.duration -= time_scheduled
                    if task.duration <= 0:
                        del tasks[task_index]
                    pointer += time_scheduled

                    tasks = self.reorder(tasks) #Reorder if resort enabled

                    #Pick Next Task Based on order
                    if not self.algorithm.order() & SchedulingOrder.sequential == 0:
                        if task.duration <= 0: pass #Since task is deleted,
                                                #task_index now points to next task.
                        else: pass #Continue scheduling current task
                    elif not self.algorithm.order() & SchedulingOrder.iterative == 0:
                        if task.duration <= 0: pass #Since task is deleted,
                                                #task_index now points to next task.
                        else: task_index = (task_index + 1) % len(tasks)
                    else: raise NotImplementedError
            else:
                #Interrupt would start after the time pointer
                #Schedulable time from pointer to intterupt begin time
                #However, there are no tasks to schedule, hence
                #we will just move the pointer to schedule the next interrupt
                pointer = interrupt.begin

        
