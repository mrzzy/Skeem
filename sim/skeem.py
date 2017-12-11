#
# skeem.py
# Skeem Simulator - Skeem Model Object
#
# Nov 21, 2017

from datetime import datetime, timedelta
import functools
import copy
import algorithm


#Utility Functions
def epoch_time(time=datetime.now()):
    epoch = datetime.utcfromtimestamp(0)
    return int((time-epoch).total_seconds())


#Prove of concept only, not actually used.
#Method overloading of __init__ does not actually work
class Repetition:
    def __init__(self, interval, stopdate=epoch_time(datetime.max), offset=0):
        self.interval = interval
        self.stopdate = stopdate
        self.offset = offset

        self.pointer = 0
        self.prev_date = datetime.now()

    #Create from days of week
    def __init__(self, days_of_week, stopdate=epoch_time(datetime.max)):
        self.stopdate = stopdate
        self.prev_date = datetime.now()

        now = self.prev_date
        a_day = timedelta(days=1)
        sorted_days = sorted(days_of_week)

        #Find the next repetition from today
        start_date = now + a_day
        while start_date.weekday() not in sorted_days:
            start_date += a_day
        self.offset = (start_date-now).total_seconds()

        self.pointer = sorted_days.index(start_date.weekday())

        self.interval = []
        prev_day = sorted_days[0]
        for day in sorted_days:
            diff_day = abs(day - prev_day)
            self.interval.append(td_day * diff_day)
            prev_day = day

    def valid(self):
        return self.stopdate > datetime.now()

    def next_repeat(self):
        if not self.valid():
            return

        nextdate = self.prev_date
        nextdate += timedelta(seconds=self.offset)
        nextdate += self.interval[self.pointer]
        self.offset = 0

        self.prev_date = nextdate
        self.pointer += 1
        self.pointer %= len(self.interval)

        return epoch_time(nextdate)


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
    def __init__(self, name, duration, deadline, describe="", tags=[],
                 repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.deadline = deadline
        self.tags = tags
        self.repeat = repeat
        self.weight = 0.0

    def weigh(self):
        #Computer from tags disabled: see sim.txt
        #weight = 0.0
        #Compute weight from Tags
        #for tag in self.tags:
            #weight += tag.weight
        #weight /= float(len(self.tags))
        #return weight
        return self.weight


class Interrupt(Schedulable):
    def __init__(self, name, duration, begin, describe="", tags=[],
                 repeat=None):
        self.name = name
        self.describe = describe
        self.duration = duration
        self.begin = begin
        self.tags = tags
        self.repeat = repeat

    def end(self):
        return self.begin + self.duration


class ScheduleIterator:
    def __init__(self, schedule, pointer=0):
        self.pointer = pointer
        self.schedule = schedule

    def __eq__(self, other):
        return self.schedule == other.schedule and\
                self.pointer == other.pointer

    def valid(self):
        return self.schedule.itinerary is not None

    def next(self):
        if self.valid():
            return ScheduleIterator(self.schedule, self.pointer + 1)

    def prev(self):
        if self.valid():
            return ScheduleIterator(self.schedule, self.pointer - 1)

    def value(self):
        if self.valid():
            return self.schedule.itinerary[self.pointer]
        else:
            raise ValueError("Invaild Iterator: No schedule to iterate")

    #Iterator Protocol
    def __next__(self):
        if self.vaild() and \
                (self.pointer + 1) < len(self.schedule.itinerary):
            self.pointer += 1
            return self.schedule.itinerary[self.pointer]
        else:
            raise StopIteration

class Schedule:
    def __init__(self, algorithm):
        self.algorithm = algorithm
        self.tasks = []
        self.interrupts = []
        self.flat_tasks = None
        self.flat_interrupts = None
        self.tidx = None
        self.itinerary = None
        self.genesis = epoch_time()
        self.ordered = False
        self.drsn = None

    def switch(self, algorithm):
        self.algorithm = algorithm
        self.invalidate()

    def add(self, schedulable):
        if isinstance(schedulable, Task):
            if [t for t in self.tasks if t.name == schedulable.name]:
                raise ValueError("Already has Task")
            self.tasks.append(schedulable)
        elif isinstance(schedulable, Interrupt):
            if [i for i in self.interrupts if i.name == schedulable.name]:
                raise ValueError("Already has Interrupt")
            self.interrupts.append(schedulable)
        else:
            raise ValueError("Type not supported")
        self.invalidate()

    def remove(self, schedulable):
        if isinstance(schedulable, Task):
            self.tasks.remove(schedulable)
        elif isinstance(schedulable, Interrupt):
            self.interrupts.remove(schedulable)
        else:
            raise ValueError("Schedulable not found")
        self.invalidate()

    def update(self, schedulable):
        if isinstance(schedulable, Task):
            for i, sched in enumerate(self.tasks):
                if sched.name == schedulable.name:
                    self.tasks[i] = schedulable
                    break
        elif isinstance(schedulable, Interrupt):
            for i, sched in enumerate(self.interrupts):
                if sched.name == schedulable.name:
                    self.interrupts[i] = schedulable
                    break
        else:
            raise ValueError("Schedulable not found")
        self.invalidate()

    def size(self):
        return len(self.tasks + self.interrupts)

    def duration(self):
        if self.drsn is None:
            total = 0
            for schedulable in (self.tasks+self.interrupts):
                total += schedulable.duration
            self.drsn = total
            return total
        else:
            return self.drsn

    def commit(self, genesis=epoch_time()):
        if self.itinerary is None:

            self.genesis = genesis
            #Order State
            self.interrupts = sorted(self.interrupts,
                                     key=(lambda interrupt: interrupt.begin))
            #Unroll Repeats
            self.unroll()
            #Create Itinerary
            self.knit()

            if len(self.itinerary) < self.size():
                #Generated itinerary had LESS scheduables than is stored
                #This means that some of the schedulables were NOT scheduled.
                raise AssertionError("Internal Inconsistency Error")

        else:
            pass #Do nothing if itinerary has not been invalidated

    def begin(self):
        if self.itinerary:
            return ScheduleIterator(self)
        else:
            raise ValueError("No itinerary to iterate")

    def end(self):
        if self.itinerary:
            #Point to one past the last on schedule
            return ScheduleIterator(self, len(self.itinerary))
        else:
            raise ValueError("No itinerary to iterate")


    #Iterator Protocol
    def __iter__(self):
        if self.itinerary:
            return ScheduleIterator(self)
        else:
            raise ValueError("No itinerary to iterate")


    def invalidate(self):
        self.flat_tasks = None
        self.flat_interrupts = None
        self.itinerary = None
        self.ordered = False
        self.tidx = None
        self.drsn = None

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
            if not self.algorithm.compare(lhs, rhs):
                return 1 #Swap Position
            else:
                return -1 #Don't swap position

        if self.algorithm.order() & algorithm.SchedulingOrder.resort == 0 and\
                self.ordered:
            #Not resort and already sorted
            return tasks #Return unsorted tasks
        elif self.algorithm.order() & algorithm.SchedulingOrder.sort == 0:
            #Sorting disabled
            return tasks #Return unsorted tasks

        #Sort
        stasks = sorted(tasks, key=functools.cmp_to_key(cmp))
        self.ordered = True

        return stasks

    def knit(self):
        if isinstance(self.algorithm, algorithm.SchedulingAlgorithm):
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
                    else:
                        tpointer = result
                elif interrupt.begin <= tpointer < interrupt.end():
                    #Pointer is at or during the interrupt
                    #Schedule the interrupt and move pointer to end.
                    self.itinerary.append(interrupt)
                    tpointer = interrupt.end()

                    irpt_idx += 1
                else:
                    raise AssertionError("Overlaps in Interrupts detected")
        elif isinstance(self.algorithm, algorithm.RoundRobinScheduler):
            self.itinerary = self.algorithm.schedule(
                self.flat_tasks, self.flat_interrupts,
                self.genesis, timdelta(minutes=30))
        else:
            raise NotImplementedError("Algorithm not recognised.")

        #Schedule any tasks left
        result = 0
        while result != -1:
            result = self.schedule(tpointer, tpointer + self.duration())

    def schedule(self, begin, end):
        if self.tidx is None:
            self.tidx = 0

        scheduled = 0
        tpointer = begin
        while (end - tpointer != 0) and len(self.flat_tasks) > scheduled:
            self.flat_tasks = self.reorder(self.flat_tasks)
            task = self.flat_tasks[self.tidx]

            if task.duration > 0:
                tschedule = self.algorithm.schedule(task, end - tpointer)
                if tschedule > end - tpointer:
                    raise AssertionError("Algorithm's schedule method is faulty")
                subtask = copy.deepcopy(task)
                subtask.duration = tschedule
                self.itinerary.append(subtask)

                tpointer += tschedule
                task.duration -= subtask.duration
            else:
                scheduled += 1

            #Determine next task based on order
            if self.algorithm.order() & algorithm.SchedulingOrder.iterative:
                self.tidx = (self.tidx + 1) % len(self.flat_tasks)
            if self.algorithm.order() & algorithm.SchedulingOrder.sequential:
                if task.duration > 0:
                    pass #Remain on same task
                else:
                    self.tidx = (self.tidx + 1) % len(self.flat_tasks)

        if scheduled == len(self.flat_tasks): #All Tasks scheduled
            return -1
        else:
            return tpointer
