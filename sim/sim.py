#!/usr/local/bin/python3
#
# sim.py
# Skeem Simulator
#
# Create Nov 24, 2017

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
import timeit
import glob

from utils import pretty, proppretty, prettytime, pdivider
import algorithm
import skeem
from skeem import epoch_time


#Simulation
class ScheduleTestCase:
    def __init__(self, size, verbose=False):
        self.size = size
        self.verbose = verbose

        self.genesis = epoch_time()

        random.seed()

    @staticmethod
    def print_schedulable(message, schedulable):
        print(message)
        pretty(schedulable)
        proppretty(schedulable)
        pdivider()

    def generate(self):
        self.name = "TestCase." + str(uuid.uuid4())
        factor = random.random()
        ntasks = int(self.size * factor)
        ninterrupts = self.size - ntasks

        #Generate random tasks and interrupts
        self.schedule = skeem.Schedule(None)

        for i in range(ntasks):
            task = self.generateRandomTask()
            self.schedule.add(task)

            if self.verbose:
                self.print_schedulable("Generated Random Task:", task)

        starts_after = epoch_time()
        for i in range(ninterrupts):
            interrupt = self.generateRandomInterrupt(starts_after)
            self.schedule.add(interrupt)
            starts_after = interrupt.end()

            if self.verbose:
                self.print_schedulable("Generated Random Interrupt:",
                                       interrupt)

        if self.verbose:
            print("Generated Test Case:")
            pretty(self.schedule)
            print("Number of tasks:", ntasks)
            print("Number of interrupts", ninterrupts)
            pdivider()

    @staticmethod
    def generateRandomTask():
        #Duration range: 1 second to 8 hours
        #Deadline range: 0 seconds to 1 day
        #Task Weight 0.0 to 1.0
        duration = random.randint(1, 8*60*60)
        deadline = epoch_time() + duration + random.randint(1, 24*60*60)

        task = skeem.Task("Task." + str(uuid.uuid4()), duration, deadline)
        task.weight = random.random()

        return task

    @staticmethod
    def generateRandomInterrupt(starts_after):
        #Duration range: 1 second to 1 day
        #Begin range: 0 seconds to 2 months
        duration = random.randint(1, 24*60*60)
        begin = starts_after + random.randint(1, 24*60*60)

        interrupt = skeem.Interrupt("Interrupt." + str(uuid.uuid4()),
                                    duration, begin)

        return interrupt

    def case(self):
        return copy.deepcopy(self.schedule)

invalid_case = []
def simulate(alg, case):
    global invalid_case
    schedule = case.case()

    profile = cProfile.Profile()
    profile.enable()
    begin = timeit.default_timer()
    schedule.switch(alg)
    try:
        schedule.commit(case.genesis)
    except AssertionError:
        print("Test Case Invalid:" + case.name)
        invalid_case.append(case.name)
        
        return
    elapse = timeit.default_timer() - begin
    profile.disable()
    profile.create_stats()
    stats = pstats.Stats(profile)

    return simulation_callback(case, schedule, stats, elapse)


#Main
if __name__ == "__main__":
#Parse Options
#Default Program Configuration
    opts = \
        {
            "verbose": False,
            "processes": multiprocessing.cpu_count(),
            "repetitions": 100,
            "directory": "sim_out",
            "test_size": 100
        }

    opt_list, args = getopt.getopt(sys.argv[1:], "hvj:t:o:l:")

    for opt, arg in opt_list:
        if opt == "-v":
            opts["verbose"] = True
        elif opt == "-j":
            opts["processes"] = int(arg)
        elif opt == "-t":
            opts["repetitions"] = int(arg)
        elif opt == "-l":
            opts["test_size"] = int(arg)
        elif opt == "-o":
            opts["directory"] = arg
        elif opt == "-h":
            print("""Usage: sim.py [options]
    -v Verbose mode - debugging information
    -p <processes> - Number of processes to use while simulating
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

    def simulation_callback(case, schedule, stats, time):
        global completed
        
        #Extract Itinerary
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
                         "time": time}, f)

        fname = case.name + "." + algorithm_name + "." + "profile"
        stats.dump_stats(fname)

        print(("%.1f%%" % (completed.value/simulations * 100)))

        if opts["verbose"]:
            print("Simulation Completed!")
            print("Simulation Completed with the following itinerary:")
            pretty(itinerary)
            print("\nSimulation Completed with the following performance stats:")
            stats.print_stats()
            pdivider()


    if opts["verbose"]:
        print("Number of simulations:", simulations)
        pdivider()


#Algorithms
    if opts["verbose"]:
        print("Algorithms Loaded:", len(algorithm.algorithms))
        pretty(algorithm.algorithms)
        pdivider()

    pool = multiprocessing.Pool(processes=opts["processes"])
    try:
        print("Simulation Commencing.")

        tcase = ScheduleTestCase(opts["test_size"], opts["verbose"])
        for i in range(opts["repetitions"]):
            tcase.generate()
            #Write Test case
            with open(tcase.name + ".case", 'wb') as f:
                pickle.dump(tcase, f)
            for alg in algorithm.algorithms:
                #pool.apply_async(simulate, args=(alg, tcase))
                simulate(alg, tcase)

        pool.close()
        pool.join()
        
        for cname in invalid_case:
            rmname = glob.glob(cname + ".*")
            for name in rmname:
                os.remove(name)
            
        print("Simulation Finished.")

    except KeyboardInterrupt:
        pool.terminate()
