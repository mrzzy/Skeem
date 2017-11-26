#
# algorithm.py
# Skeem Simulator - Algorithms
#
# Created Nov 24, 2017
#

import skeem
import random

#Basic Algorithms - Privative Non dynamic algorithms
class RandomAlgorithm(skeem.SchedulingAlgorithm):
    def order(self):
        return skeem.SchedulingOrder.nosort | skeem.SchedulingOrder.iterative

    def compare(self, lhs, rhs):
        return bool(random.getrandbits(1))

    def schedule(self, task, available):
        return max(task.duration, available)
        

class EarliestDeadlineAlgorithm(skeem.SchedulingAlgorithm):
    def order(self):
        return skeem.SchedulingOrder.onesort | skeem.SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.deadline < rhs.deadline

    def schedule(self, task, available):
        return max(task.duration, available)
    
class HeaviestWeightAlgorithm(skeem.SchedulingAlgorithm):
    def order(self):
        return skeem.SchedulingOrder.onesort | skeem.SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.weigh() > rhs.weigh()

    def schedule(self, task, available):
        return max(task.duration, available)

class ShortestDurationAlgorithm(skeem.SchedulingAlgorithm):
    def order(self):
        return skeem.SchedulingOrder.onesort | skeem.SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.duration < lhs.duration

    def schedule(self, task, available):
        return max(task.duration, available)

#Simple Dynamic Algorithms
class DynamicShortestDurationAlgorithm(skeem.SchedulingAlgorithm):
    def order(self):
        return skeem.SchedulingOrder.resort | skeem.SchedulingOrder.sequential
    
    def compare(self, lhs, rhs):
        return lhs.duration < lhs.duration

    def schedule(self, task, available):
        return max(task.duration, available)

class ShortestSlackAlgorithm(skeem.SchedulingAlgorithm):
    def slack(task):
        return task.deadline - task.duration

    def order(self):
        return skeem.SchedulingOrder.resort | skeem.SchedulingOrder.sequential

    def compare(self, lhs, rhs):
        return self.slack(lhs) < self.slack(rhs)
    
    def schedule(self, task, available):
        return max(task.duration, available)
    
#List of Algorithms used in the simulator
algorithms = \
    [
        RandomAlgorithm(),
        EarliestDeadlineAlgorithm(),
        HeaviestWeightAlgorithm(), 
        ShortestDurationAlgorithm(),
        DynamicShortestDurationAlgorithm(),
        ShortestDurationAlgorithm()
    ]
