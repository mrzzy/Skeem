#!/usr/local/bin/python3
#
# sim.py
# Skeem Simulator
#
# Create Nov 24, 2017

import skeem
from skeem import epoch_time
import algorithm
import random

import cProfile
import multiprocessing
import queue
import os
import getopt
import uuid
import copy
import sys
import pprint
import datetime

#Utility Functions
def pretty(arg):
    pprint.pprint(arg, indent=2)
    
def proppretty(arg):
    pretty(dict((name, getattr(arg, name)) for name in dir(arg) if not name.startswith('__')))

def prettytime(arg):
    print(str(datetime.timedelta(seconds=arg)))

def pdivider():
    print("================================================================================")
    print()

def epoch_time(time=datetime.datetime.now()):
    return int((time - datetime.datetime(1970,1,1)).total_seconds())


#Parse Options
#Default Program Configuration
opts = \
    {
        "verbose": False,
        "threads": multiprocessing.cpu_count(),
        "repetitions": 100,
        "directory": os.getcwd(),
        "test_size": 1000
    }

opt_list, args = getopt.getopt(sys.argv[1:], "hvj:t:o:l:")

for opt, arg in opt_list:
    if opt == "-v":
        opts["verbose"] = True
    elif opt == "-j":
        opts["threads"] = int(arg)
    elif opt == "-t":
        opts["repetitions"] = int(arg)
    elif opt == "-l":
        opts["test_size"] = int(arg)
    elif opt == "-o":
        opts["directory"] = arg
    elif opt == "-h":
        print("""Usage: sim.py [options]
-v Verbrose mode - debugging infomation
-j <threads> - Number of threads to use while simulating
-t <tests> - Number of number tests cases to test per algorithm
-l <size> - Size of the test cases to run.
-o <directory> - put the output in this directory
""")
        sys.exit()
    else:
        #Unknown Argument
        raise ValueError

os.chdir(opts["directory"])

if opts["verbose"]:
    print("Skeem Simulator")
    print("Program options:")
    pretty(opts)
    pdivider()

#Progress
simulations = opts["repetitions"] * len(algorithm.algorithms)
completed = multiprocessing.Value('i')

def progress_callback(result):
    global completed
    if opts["verbose"]:
        print("Simulation Completed with result:")
        print("Duration:")
        pdivider()
    print(("%.1f%%" % (float(completed.value) / float(simulations) * 100.0)))

if opts["verbose"]:
    print("Number of simulations:" + str(simulations))
    pdivider()

    
#Algorithms
if opts["verbose"]:
    print("Algorithms Loaded:" + str(len(algorithm.algorithms)))
    pretty(algorithm.algorithms)
    pdivider()


#Simulation
class ScheduleTestCase:
    def __init__(self, size):
        self.size = size

    def generate(self):
        random.seed()
        factor = random.random()
        ntasks = int(factor * self.size)
        ninterrupts = self.size - ntasks
        self.irpt_tpointer = epoch_time()

        #Generate random tasks and interrupts
        self.schedule = skeem.Schedule( None)

        for i in range(ntasks):
            self.randomTask()
        for i in range(ninterrupts): 
            self.randomInterrupt()
        
        if opts["verbose"] == True:
            print("Generated Test Case:")
            pretty(self.schedule)
            print("number of tasks:" + str(ntasks))
            print("number of interrupts:" + str(ninterrupts))
            pdivider()

    def case(self):
        return copy.deepcopy(self.schedule)

    def randomTask(self):
        #Duration range 1 second to 8 hours
        #Deadline range 0 second to 1 month
        #Task Weight 0.0 to 1.0
        random.seed()
        duration = random.randint(1, 8 * 60 * 60) 
        deadline = epoch_time() + duration + random.randint(0, 1 * 24 * 60 * 60)
        task = skeem.Task("Task:" + str(uuid.uuid4()), duration,deadline)
        task.weight = random.random()
        
        if opts["verbose"] == True:
            print("Random Task Generated:")
            pretty(task)
            proppretty(task)
            pdivider()

        self.schedule.add(task)
    
    def randomInterrupt(self):
        #Duration range 1 second to 1 day
        #Begin range 0 second to 2 month
        random.seed()
        duration = random.randint(1, 24 * 60 * 60) 
        begin = self.irpt_tpointer + random.randint(0, 1 * 24 * 60 * 60)
        interrupt = skeem.Interrupt("Interrupt:" + str(uuid.uuid4()), duration, begin)
        self.irpt_tpointer = interrupt.end()

        if opts["verbose"] == True:
            print("Random Interrupt Generated:")
            pretty(interrupt)
            proppretty(interrupt)
            pdivider()

        self.schedule.add(interrupt)

def simulate(alg, case):
    global completed

    case.switch(alg)
    case.commit()
    
    schedule = []
    iterator = case.begin()
    while not iterator == case.end():
        schedule += [ iterator.value() ]
        iterator = iterator.next()

    completed.value += 1
    return schedule
    
#Main
pool = multiprocessing.Pool(processes=opts["threads"])
try:
    print("Simulation Commencing.")
    
    tcase = ScheduleTestCase(opts["test_size"])
    for repeat in range(0, opts["repetitions"]):
        tcase.generate()
        for alg in algorithm.algorithms:
            pool.apply_async(simulate, args=(alg, tcase.case()), callback=progress_callback)
        
    pool.close()
    pool.join()

    print("Simulation Finished.")

except KeyboardInterrupt:
    pool.kill()
