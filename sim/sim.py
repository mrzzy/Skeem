#!/usr/local/bin/python3
#
# sim.py
# Skeem Simulator
#
# Create Nov 24, 2017

import pprint
import multiprocessing
import random
import cProfile
import pstats
import os
import sys
import getopt
import uuid
import copy
import pickle
import datetime
import time

import algorithm
import skeem
from skeem import epoch_time


#Utility Functions
def pretty(arg):
    pprint.pprint(arg, indent=2)

def proppretty(arg):
    pretty({name: getattr(arg, name)
            for name in dir(arg) if not name.startswith('__')})

def prettytime(arg):
    print(datetime.timedelta(seconds=arg))

def pdivider():
    print("="*80, end="\n\n")


#Parse Options
#Default Program Configuration
opts = \
    {
        "verbose": False,
        "threads": multiprocessing.cpu_count(),
        "repetitions": 10,
        "directory": "sim_out",
        "test_size": 100
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
        raise ValueError("Unknown Argument")

if not os.path.exists(opts["directory"]):
    os.makedirs(opts["directory"])
os.chdir(opts["directory"])

if opts["verbose"]:
    print("Skeem Simulator")
    print("Program options:")
    pretty(opts)
    pdivider()

#Progress
simulations = opts["repetitions"] * len(algorithm.algorithms)
completed = multiprocessing.Value('i')

def simulation_callback(case, schedule, stats):
    global completed

    itinerary = []
    iterator = schedule.begin()
    while iterator != schedule.end():
        itinerary.append(iterator.value())
        iterator = iterator.next()
    completed.value += 1

    #Write output
    algorithm_name = schedule.algorithm.__class__.__name__

    fname = case.name + "." + algorithm_name
    with open(fname, 'wb') as f:
        pickle.dump({"case": case.name,
                     "itinerary": itinerary,
                     "time": stats.total_tt}, f)

    fname = case.name + "." + algorithm_name + "." + "stats"
    stats.dump_stats(fname)

    if opts["verbose"]:
        print("Simulation Completed!")
        print("Simulation Completed with the following itinerary:")
        pretty(itinerary)
        print("\nSimulation Completed with the following performance stats:")
        stats.print_stats()
        pdivider()

    print(("%.1f%%" % (completed.value/simulations * 100)))

if opts["verbose"]:
    print("Number of simulations:", simulations)
    pdivider()


#Algorithms
if opts["verbose"]:
    print("Algorithms Loaded:", len(algorithm.algorithms))
    pretty(algorithm.algorithms)
    pdivider()


#Simulation
class ScheduleTestCase:
    def __init__(self, size, verbose=False):
        self.size = size
        self.name = "TestCase:" + str(uuid.uuid4())
        self.verbose = verbose
        random.seed()

    def generate(self):
        self.name = "TestCase:" + str(uuid.uuid4())
        factor = random.random()
        ntasks = int(factor * self.size)
        ninterrupts = self.size - ntasks
        self.irpt_tpointer = epoch_time()

        #Generate random tasks and interrupts
        self.schedule = skeem.Schedule(None)

        for i in range(ntasks):
            self.randomTask()
        for i in range(ninterrupts):
            self.randomInterrupt()

        if self.verbose:
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
        duration = random.randint(1, 8 * 60 * 60)
        deadline = epoch_time() + duration +\
            random.randint(0, 1 * 24 * 60 * 60)
        task = skeem.Task("Task:" + str(uuid.uuid4()), duration, deadline)
        task.weight = random.random()

        if self.verbose:
            print("Random Task Generated:")
            pretty(task)
            proppretty(task)
            pdivider()

        self.schedule.add(task)

    def randomInterrupt(self):
        #Duration range 1 second to 1 day
        #Begin range 0 second to 2 month
        duration = random.randint(1, 24 * 60 * 60)
        begin = self.irpt_tpointer + random.randint(1, 24 * 60 * 60)
        interrupt = skeem.Interrupt("Interrupt:" + str(uuid.uuid4()),
                                    duration, begin)
        self.irpt_tpointer = interrupt.end()

        if self.verbose:
            print("Random Interrupt Generated:")
            pretty(interrupt)
            proppretty(interrupt)
            pdivider()

        self.schedule.add(interrupt)

def simulate(alg, case):
    schedule = case.case()

    profile = cProfile.Profile()
    profile.enable()
    schedule.switch(alg)
    try:
        schedule.commit()
    except AssertionError:
        print("Test Case Invaild:" + case.name)
        return

    profile.disable()
    profile.create_stats()
    stats = pstats.Stats(profile)

    return simulation_callback(case, schedule, stats)


#Main
pool = multiprocessing.Pool(processes=opts["threads"])
try:
    print("Simulation Commencing.")

    tcase = ScheduleTestCase(opts["test_size"], opts["verbose"])
    for i in range(opts["repetitions"]):
        tcase.generate()
        #Write Test case
        with open(tcase.name + ".case", 'wb') as f:
            case = tcase.case()
            pickle.dump({"tasks": case.tasks,
                         "interrupts": case.interrupts}, f)
        for alg in algorithm.algorithms:
            pool.apply_async(simulate, args=(alg, tcase))
            #simulate(alg, tcase)

    pool.close()
    pool.join()

    print("Simulation Finished.")

except KeyboardInterrupt:
    pool.terminate()
