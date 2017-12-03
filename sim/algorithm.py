#
# algorithm.py
# Skeem Simulator - Algorithms
#
# Created Nov 24, 2017
#

import random

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

#Basic Algorithms - Privative Non dynamic algorithms
class RandomAlgorithm(SchedulingAlgorithm):
    def order(self):
        return SchedulingOrder.nosort | SchedulingOrder.iterative

    def compare(self, lhs, rhs):
        return bool(random.getrandbits(1))

    def schedule(self, task, available):
        return min(task.duration, available)
        

class EarliestDeadlineAlgorithm(SchedulingAlgorithm):
    def order(self):
        return SchedulingOrder.sort | SchedulingOrder.onesort | SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.deadline <= rhs.deadline

    def schedule(self, task, available):
        return min(task.duration, available)
    
class HeaviestWeightAlgorithm(SchedulingAlgorithm):
    def order(self):
        return SchedulingOrder.sort | SchedulingOrder.onesort | SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.weigh >= rhs.weigh

    def schedule(self, task, available):
        return min(task.duration, available)

class ShortestDurationAlgorithm(SchedulingAlgorithm):
    def order(self):
        return SchedulingOrder.sort | SchedulingOrder.onesort | SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.duration <= lhs.duration

    def schedule(self, task, available):
        return min(task.duration, available)

#Simple Dynamic Algorithms
class DynamicShortestDurationAlgorithm(SchedulingAlgorithm):
    def order(self):
        return SchedulingOrder.sort | SchedulingOrder.resort | SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.duration <= lhs.duration

    def schedule(self, task, available):
        return min(task.duration, available)

class ShortestSlackAlgorithm(SchedulingAlgorithm):
    def slack(self, task):
        return task.deadline - task.duration

    def order(self):
        return SchedulingOrder.sort | SchedulingOrder.resort | SchedulingOrder.sequential

    def compare(self, lhs, rhs):
        return self.slack(lhs) <= self.slack(rhs)
    
    def schedule(self, task, available):
        return min(task.duration, available)

#Schedulers
class RoundRobinScheduler:
    @staticmethod
    def schedule(tasks, interrupts, start_time, block_size):
        return None

#List of Algorithms used in the simulator
algorithms = \
    [
        EarliestDeadlineAlgorithm(),
        RandomAlgorithm(),
        HeaviestWeightAlgorithm(),
        ShortestDurationAlgorithm(),
        DynamicShortestDurationAlgorithm(),
        ShortestSlackAlgorithm()
    ]
