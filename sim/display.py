#
# sim/display.py
# Skeem Simulator Display
#
# Created on Dec 01, 2017
#

import skeem
import algorithm
import matplotlib.pyplot as plt
import sys
import profile
import os
import pickle
import glob
import getopt
import pstats

from utils import pretty, proppretty, prettytime, pdivider
from sim import ScheduleTestCase


if __name__ == "__main__":
#Program Options
    opts = \
        {
            "directory": "sim_out",
            "verbose": False,
            "constraint": 85, #Percentage Constraint for duration
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
            sys.exit()
        else:
            #Unknown Argument
            raise ValueError
#Working Directory
    print(os.getcwd())
    if not os.path.exists(opts["directory"]):
        os.makedirs(opts["directory"])
    os.chdir(opts["directory"])

    if opts["verbose"]:
        print("Skeem Display")
        print("Program options:")
        pretty(opts)
        pdivider()

#Read Test Cases
    tpaths = glob.glob("TestCase:*.case")
    tcase_map = {}
    for tpath  in tpaths:
        with open(tpath, 'rb') as f:
            tcase = pickle.load(f)

            if opts["verbose"]:
                print("Loaded Test case:" + tcase.name)
            tcase_map[tcase.name] = tcase
    tcases = list(tcase_map.values())

    if opts["verbose"]:
        print(("Loaded %d Test Cases" % len(tcases)))

#Algorithms
    aname = [alg.__class__.__name__ for alg in algorithm.algorithms]

#Read Simulation Data
    csim_data = {} #Test Case as key
    asim_data = {} #Algorithm as key


    for case in tcases:
        if not case in csim_data:
            csim_data[case] = []
        for alg in algorithm.algorithms:
            if not alg in asim_data:
                asim_data[alg] = []
            algname = alg.__class__.__name__
            with open(case.name + '.' + algname, 'rb') as f:
                sdata = pickle.load(f)
                csim_data[case] += [ sdata ]
                asim_data[alg] += [ sdata ]

                if opts["verbose"]:
                    print(("Loaded Simulation Data for Case %s for Algorithm %s"\
                            % (case.name, algname)))
        
    if opts["verbose"]:
        print("Loaded Simulation Data")

#Interactive Command Prompt
    try:
        while True:
            uinput = input("(display):")
            uargv = uinput.split()

            #List Test Cases
            if len(uargv) == 0: pass
            elif uargv[0] == 'l':
                for i in range(0, len(tcases)):
                    print(("[%d] %s" % (i, tcases[i].name)))
            #List Algorithms
            elif uargv[0] == 'a':
                for i in range(0, len(algorithm.algorithms)):
                    print(("[%d] %s" % (i, 
                        algorithm.algorithms[i].__class__.__name__)))
            #Set Duration Constraint
            elif uargv[0] == 'c':
                if len(uargv) == 2 and 0 <= int(uargv[1]) <= 100:
                    opts["constraint"] = int(uargv[1])
                else:
                    print("Unknown constraint percentage. Usage: c <percentage>")
            #Display Processing Time
            elif uargv[0] == 't':
                #Extract Data
                atime = {}
                for alg in algorithm.algorithms:
                    if not alg in atime:
                        atime[alg] = 0
                    for sdata in asim_data[alg]:
                        atime[alg] += sdata["time"]
                
                #Plot Graph
                y_val = range(len(algorithm.algorithms))
                x_val = list(atime.values())
                
                plt.barh(y_val, x_val, align="center")
                plt.yticks(y_val, aname)
                plt.ylabel("Algorithm")
                plt.xlabel("Time (seconds)")
                plt.title("Processing Time")
                plt.show()
            #Display Percentage of Completed Weight
            elif uargv[0] == 'w':
                disp_data = {}
                for alg in algorithm.algorithms:
                    maximum = 0
                    actual = 0

                    for sdata in asim_data[alg]:
                        case = tcase_map[sdata["case"]]
                        binder = case.case().duration() * (opts["constraint"] / 100.0)
                        pointer = 0
                        itinerary = sdata["itinerary"]
                        for schedulable in itinerary:
                            if isinstance(schedulable, skeem.Task):
                                maximum += schedulable.weigh()
                                if pointer <= binder:
                                    actual += schedulable.weigh()
                            pointer += schedulable.duration
                    
                    #Compute Percentage
                    disp_data[alg.__class__.__name__] = actual / maximum * 100.0
                

                y_val = range(len(algorithm.algorithms))
                x_val = list(disp_data.values())

                plt.barh(y_val, x_val, align="center")
                plt.yticks(y_val, aname)
                plt.title("Completed Weight")
                plt.xlabel("%Weight")
                plt.ylabel("Algorithm")
                plt.show()
            
            #Display Percentage of Deadlines Met
            elif uargv[0] == 'd':
                disp_data = {}
                for alg in algorithm.algorithms:
                    maximum = 0
                    actual = 0

                    for sdata in asim_data[alg]:
                        case = tcase_map[sdata["case"]]
                        binder = case.case().duration() * (opts["constraint"] / 100.0)
                        pointer = 0
                        itinerary = sdata["itinerary"]
                        for schedulable in itinerary:
                            if isinstance(schedulable, skeem.Task):
                                maximum += 1
                                adjust_deadline = schedulable.deadline - \
                                        case.genesis
                                if pointer <= binder and pointer <= adjust_deadline:
                                    actual += 1
                            pointer += schedulable.duration
                    
                    #Compute Percentage
                    disp_data[alg.__class__.__name__] = actual / maximum * 100.0
                

                y_val = range(len(algorithm.algorithms))
                x_val = list(disp_data.values())

                plt.barh(y_val, x_val, align="center")
                plt.yticks(y_val, aname)
                plt.title("Deadline Met")
                plt.xlabel("% Deadline met")
                plt.ylabel("Algorithm")
                plt.show()
            
            
            #Display Average Standard Deviation between Tasks
            elif uargv[0] == 'b':
                disp_data = {}
                for alg in algorithm.algorithms:
                    sum_dev = 0.0
                    for sdata in asim_data[alg]:
                        case = tcase_map[sdata["case"]]
                        binder = case.case().duration() * (opts["constraint"] / 100.0)
                        pointer = 0
                        prev_ptr = 0
                        itinerary = sdata["itinerary"]
                        for schedulable in itinerary:
                            if isinstance(schedulable, skeem.Task):
                                if pointer <= binder:
                                    sum_dev += pointer - prev_ptr
                                    prev_ptr = pointer
                            pointer += schedulable.duration
                    
                    #Compute Percentage
                    disp_data[alg.__class__.__name__] = sum_dev / len(itinerary)

                y_val = range(len(algorithm.algorithms))
                x_val = list(disp_data.values())

                plt.barh(y_val, x_val, align="center")
                plt.yticks(y_val, aname)
                plt.title("Standard Deviation")
                plt.xlabel("Deviation")
                plt.ylabel("Algorithm")
                plt.show()

            #Display Performance profile.
            elif uargv[0] == 'p':
                if not len(uargv) == 3:
                    print("Usage: p <test case id> <algorithm id>.")
                    print("Run 'a' to list algorithm, Run 'l' to list test cases")
                elif int(uargv[1]) in range(0, len(tcases)) and\
                        int(uargv[2]) in range(0, len(algorithm.algorithms)):
                    case = tcases[int(uargv[1])]
                    alg = algorithm.algorithms[int(uargv[2])]
                    aname = alg.__class__.__name__
                    
                    print(("Profile of %s running %s" % (aname, case.name)))
                    pstats.Stats(case.name + "." + aname + ".profile").print_stats()
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
- b - display graph plotting standard deviation^-1 between each task for each
      each algorithm.
- p <test case> <algorithm> - print performance profile for algorithm and test 
                              case.
- i <test case> <algorithm> - print itinerary generated by algorithm from test 
                              case.
- c <test case> - print test case
                              case.""")
    except EOFError:
        print()
        sys.exit(0)
