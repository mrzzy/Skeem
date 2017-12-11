#
# sim/display.py
# Skeem Simulator Display
#
# Created on Dec 01, 2017
#

import skeem
import matplotlib.pyplot as plt
import sys
import profile
import os
import pickle
import glob
import getopt
import pstats

from functools import reduce

from utils import pretty, proppretty, prettytime, pdivider
from algorithm import algorithms as ALGORITHMS
from sim import ScheduleTestCase


def list_test_cases(test_cases):
    for i, test_case in enumerate(test_cases):
        print("[%d] %s" % (i, test_case.name))


def list_algorithms(algorithms):
    for i, algorithm in enumerate(algorithms):
        algorithm_name = algorithm.__class__.__name__
        print("[%d] %s" % (i, ALGORITHMS[i].__class__.__name__))


def display_processing_time(algorithms, simulation_data):
    x_val = []
    y_val = range(len(ALGORITHMS))
    y_ticks = []

    for algorithm in algorithms:
        data = simulation_data[algorithm]
        total_time = reduce(lambda x, y: x + y["time"], data, 0)
        x_val.append(total_time)
        y_ticks.append(algorithm.__class__.__name__)

    #Plot Graph
    plt.barh(y_val, x_val, align="center")
    plt.yticks(y_val, y_ticks)
    plt.ylabel("Algorithm")
    plt.xlabel("Time (seconds)")
    plt.title("Processing Time")
    plt.tight_layout()
    plt.show()


def completed_weight(simulation_data, test_case_dict, constraint_percent):
    maximum = 0
    actual = 0

    for data in simulation_data:
        test_case = test_case_dict[data["case"]]
        itinerary = data["itinerary"]
        constraint = test_case.case().duration() * (constraint_percent/100)

        current_time = 0
        for schedulable in itinerary:
            if isinstance(schedulable, skeem.Task):
                maximum += schedulable.weigh()
                if current_time <= constraint:
                    actual += schedulable.weigh()
            current_time += schedulable.duration

    return (actual, maximum)

def display_completed_weight_percentage(algorithms, simulation_data,
                                        test_case_dict, constraint_percent):
    y_val = range(len(ALGORITHMS))
    y_ticks = []
    x_val = []
    for algorithm in algorithms:
        actual, maximum = completed_weight(simulation_data[algorithm],
                                           test_case_dict, constraint_percent)

        algorithm_name = algorithm.__class__.__name__
        percentage = actual/maximum * 100
        x_val.append(percentage)
        y_ticks.append(algorithm_name)

    plt.barh(y_val, x_val, align="center")
    plt.yticks(y_val, y_ticks)
    plt.title("Completed Weight")
    plt.xlabel("% Weight")
    plt.ylabel("Algorithm")
    plt.tight_layout()
    plt.show()


def deadlines_met(simulation_data, test_case_dict, constraint_percent):
    maximum = 0
    actual = 0

    for data in simulation_data:
        test_case = test_case_dict[data["case"]]
        itinerary = data["itinerary"]
        constraint = test_case.case().duration() * (constraint_percent/100)

        current_time = 0
        for schedulable in itinerary:
            if isinstance(schedulable, skeem.Task):
                maximum += 1
                relative_deadline = schedulable.deadline - test_case.genesis
                if constraint >= current_time <= relative_deadline:
                    actual += 1
            current_time += schedulable.duration

    return (actual, maximum)


def display_percentage_deadlines_met(algorithms, simulation_data,
                                     test_case_dict, constraint_percent):
    y_val = range(len(algorithms))
    y_ticks = []
    x_val = []

    for algorithm in algorithms:
        actual, maximum = deadlines_met(simulation_data[algorithm],
                                        test_case_dict, constraint_percent)

        algorithm_name = algorithm.__class__.__name__
        percentage = actual/maximum * 100
        x_val.append(percentage)
        y_ticks.append(algorithm_name)

    plt.barh(y_val, x_val, align="center")
    plt.yticks(y_val, y_ticks)
    plt.title("Deadline Met")
    plt.xlabel("% Deadline met")
    plt.ylabel("Algorithm")
    plt.tight_layout()
    plt.show()

def display_standard_deviation(algorithms, simulation_data, test_case_dict,\
        constraint_percent):
    x_val = []
    y_val = range(len(algorithms))
    y_ticks = []

    for algorithm in algorithms:
        algorithm_name = algorithm.__class__.__name__
        y_ticks.append(algorithm_name)

        deviation_sum = 0
        for data in simulation_data[algorithm]:
            case = test_case_dict[data["case"]]
            binder = case.case().duration() * (constraint_percent / 100.0)
            pointer = 0
            previous = 0
            itinerary = data["itinerary"]
            for schedulable in itinerary:
                if isinstance(schedulable, skeem.Task):
                    if pointer <= binder:
                        deviation_sum += pointer - previous
                        previous = pointer
                pointer += schedulable.duration

        print("Deviation sum %d: " % deviation_sum)
        #Compute Standard Deviation
        x_val.append(deviation_sum / len(itinerary))

    plt.barh(y_val, x_val, align="center")
    plt.yticks(y_val, y_ticks)
    plt.title("Standard Deviation")
    plt.xlabel("Deviation")
    plt.ylabel("Algorithm")
    plt.show()


def main():
    #Program Options
    opts = \
        {
            "directory": "sim_out",
            "verbose": False,
            "constraint": 70, #Percentage Constraint for duration
        }

    opt_list, args = getopt.getopt(sys.argv[1:], "hvi:l:")

    for opt, arg in opt_list:
        if opt == "-v":
            opts["verbose"] = True
        elif opt == "-i":
            opts["directory"] = arg
        elif opt == "-h":
            print("""Usage: display.py [options]
    -v Verbose mode - debugging information
    -i <directory> - read simulation data for this directory
    """)
            # return
        else:
            raise ValueError("Unknown Argument")

    if opts["verbose"]:
        print("Skeem Display")
        print("Program options:")
        pretty(opts)
        pdivider()

#Working Directory
    if not os.path.exists(opts["directory"]):
        os.makedirs(opts["directory"])
    os.chdir(opts["directory"])
    if opts["verbose"]:
        print("Data Directory: " + os.getcwd())

    #Read Test Cases
    test_case_paths = glob.glob("TestCase.*.case")
    test_case_dict = {}
    for test_case_path in test_case_paths:
        with open(test_case_path, "rb") as f:
            test_case = pickle.load(f)

            if opts["verbose"]:
                print("Loaded Test Case:", test_case.name)

            test_case_dict[test_case.name] = test_case

    test_cases = list(test_case_dict.values()) ## TODO: Try to remove

    if opts["verbose"]:
        print("Loaded %d Test Cases" % len(test_cases))

    #Algorithms
    algorithm_names = [x.__class__.__name__ for x in ALGORITHMS]

    #Read Simulation Data
    # {algorithm_instance: [test_case_data]}
    simulation_data = {}

    for test_case in test_cases:
        for algorithm in ALGORITHMS:
            algorithm_name = algorithm.__class__.__name__

            with open(test_case.name + "." + algorithm_name, "rb") as f:
                data = pickle.load(f)
                simulation_data[algorithm] =\
                    simulation_data.get(algorithm, []) + [data]

            if opts["verbose"]:
                print("Loaded Simulation Data for Case %s for Algorithm %s" %
                      (test_case.name, algorithm_name))

    if opts["verbose"]:
        print("Loaded Simulation Data")
        size = 0
        for list_data in simulation_data.values():
            size += len(list_data)
            
        print("Loaded %d Algorithms with %d sets of simulation data." \
                % (len(ALGORITHMS), size))

    #Interactive Command Prompt
    try:
        while True:
            user_input = input("(display):")
            argv = user_input.split()
            
            if argv[0] == 'l':
                list_test_cases(test_cases)
            elif argv[0] == 'a':
                list_algorithms(ALGORITHMS)
            elif argv[0] == 'c':
                if len(argv) == 2 and 0 <= int(argv[1]) <= 100:
                    opts["constraint"] = int(argv[1])
                else:
                    print("Unknown constraint percentage. \
Usage: c <percentage>")
            elif argv[0] == 't':
                display_processing_time(ALGORITHMS, simulation_data)
            elif argv[0] == 'w':
                display_completed_weight_percentage(
                    ALGORITHMS, simulation_data,
                    test_case_dict, opts["constraint"])
            elif argv[0] == 'd':
                display_percentage_deadlines_met(
                    ALGORITHMS, simulation_data,
                    test_case_dict, opts["constraint"])
            elif argv[0] == 'b':
                #Display Average Standard Deviation between Tasks
                display_standard_deviation(ALGORITHMS, simulation_data,\
                        test_case_dict, opts["constraint"])
                
            elif argv[0] == 'p':
                #Display Performance profile.
                if len(argv) != 3:
                    print("Usage: p <test case id> <algorithm id>.")
                    print("Run 'a' to list algorithm, Run 'l' to list test \
cases")
                elif int(argv[1]) in range(len(test_cases)) and\
                        int(argv[2]) in range(len(ALGORITHMS)):
                    test_case = test_cases[int(argv[1])]
                    alg = ALGORITHMS[int(argv[2])]

                    algorithm_name = alg.__class__.__name__
                    print("Profile of %s running %s" % (algorithm_name,
                                                        test_case.name))

                    stats_filename = test_case.name + "." + algorithm_name +\
                        ".profile"
                    pstats.Stats(stats_filename).print_stats()
                else:
                    print("Unknown Algorithm or Test Case")
                    print("Usage: p <test case id> <algorithm id>.")

            else:
                print("Unknown subcommand")
                print("""Usage:
- l - list test cases with id
- a - list algorithms with id
- t - display graph plotting processing time taken to schedule for each
     algorithm
- w - display graph plotting weights complete for each algorithm.
- d - display graph plotting task complete before deadline for each algorithm.
- b - display graph plotting standard deviation between each task for each
      each algorithm.
- p <test case> <algorithm> - print performance profile for algorithm and test
                              case.
- i <test case> <algorithm> - print itinerary generated by algorithm from test
                              case.
- c <percentage> - change time constriant to percentage of duration""")
    except EOFError:
        print()


if __name__ == "__main__":
    main()
