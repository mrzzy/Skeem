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

from utils import pretty, proppretty, prettytime, pdivider
from skeem import epoch_time, Schedule, Task, Interrupt
from algorithm import algorithms as ALGORITHMS


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
        self.schedule = Schedule(None)

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

        task = Task("Task." + str(uuid.uuid4()), duration, deadline)
        task.weight = random.random()

        return task

    @staticmethod
    def generateRandomInterrupt(starts_after):
        #Duration range: 1 second to 1 day
        #Begin range: 0 seconds to 2 months
        duration = random.randint(1, 24*60*60)
        begin = starts_after + random.randint(1, 24*60*60)

        interrupt = Interrupt("Interrupt." + str(uuid.uuid4()),
                              duration, begin)

        return interrupt

    def case(self):
        return copy.deepcopy(self.schedule)


def simulate(test_case, algorithm):
    schedule = test_case.case()
    schedule.switch(algorithm)

    profile = cProfile.Profile()
    profile.enable()

    try:
        schedule.commit(test_case.genesis)
    except AssertionError:
        print("Test Case Invalid:", test_case.name)
        return (None, None)

    profile.disable()
    profile.create_stats()

    stats = pstats.Stats(profile)
    return (schedule, stats)


def extract_itinerary(schedule):
    itinerary = []
    iterator = schedule.begin()
    while iterator != schedule.end():
        itinerary.append(iterator.value())
        iterator = iterator.next()

    return itinerary


def write_algorithm_schedule(test_case_name, algorithm_name,
                             schedule, time_taken):
    itinerary = extract_itinerary(schedule)

    with open(test_case_name + "." + algorithm_name, "wb") as f:
        pickle.dump({"case": test_case_name,
                     "itinerary": itinerary,
                     "time": time_taken}, f)


def simulate_and_record(test_case, algorithm, completed, completed_lock,
                        simulations, verbose=False):
    schedule, stats = simulate(test_case, algorithm)

    completed_lock.acquire()
    completed.value += 1
    completed_lock.release()

    if stats is None:
        return

    # Write test case
    with open(test_case.name + ".case", "wb") as f:
        pickle.dump(test_case, f)

    algorithm_name = algorithm.__class__.__name__
    write_algorithm_schedule(test_case.name, algorithm_name,
                             schedule, stats.total_tt)

    # Write stats
    stats.dump_stats(test_case.name + "." + algorithm_name + ".profile")

    print("%.1f%%" % (completed.value/simulations * 100))

    if verbose:
        print("Simulation Completed!")
        print("Simulation Completed with the following itinerary:")
        pretty(itinerary)
        print("\nSimulation Completed with the following performance stats:")
        stats.print_stats()
        pdivider()


def main():
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
            return
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
    simulations = opts["repetitions"] * len(ALGORITHMS)
    m = multiprocessing.Manager()
    completed = m.Value('i', 0)
    completed_lock = m.Lock()


    if opts["verbose"]:
        print("Number of simulations:", simulations)
        pdivider()


    #Algorithms
    if opts["verbose"]:
        print("Algorithms Loaded:", len(ALGORITHMS))
        pretty(ALGORITHMS)
        pdivider()

    pool = multiprocessing.Pool(processes=opts["processes"])
    try:
        print("Simulation Commencing.")

        test_case = ScheduleTestCase(opts["test_size"], opts["verbose"])
        for i in range(opts["repetitions"]):
            test_case.generate()
            #Write Test case
            with open(test_case.name + ".case", 'wb') as f:
                pickle.dump(test_case, f)

            for algorithm in ALGORITHMS:
                args = (test_case, algorithm, completed, completed_lock,
                        simulations, opts["verbose"])
                pool.apply_async(simulate_and_record, args=args)

        pool.close()
        pool.join()

        print("Simulation Finished.")

    finally:
        pool.terminate()


#Main
if __name__ == "__main__":
    main()