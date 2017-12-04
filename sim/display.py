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
from sim import ScheduleTestCase

#Utility Functinns
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


#Program Options
opts = \
    {
        "directory": "sim_out",
        "verbose": False,
        "constraint": 85, #Percentage Constraint for duration
        "selector": -1
    }

opt_list, args = getopt.getopt(sys.argv[1:], "hvi:l:")

for opt, arg in opt_list:
    if opt == "-v":
        opts["verbose"] = True
    elif opt == "-i":
        opts["directory"] = arg
    elif opt == "-l":
        opts["constraint"] = arg
    elif opt == "-h":
        print("""Usage: display.py [options]
-v Verbrose mode - debugging infomation
-i <directory> - read simulation data for this directory
""")
        sys.exit()
    else:
        #Unknown Argument
        raise ValueError
#Working Directory
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
tcases = []
opts["selector"] = -1
for tpath  in tpaths:
    with open(tpath, 'rb') as f:
        tcase = pickle.load(f)

        if opts["verbose"]:
            print("Loaded Test case:" + tcase.name)
        tcases += [ tcase ]

if opts["verbose"]:
    print(("Loaded %d Test Cases" % len(tcases)))

#Read Simuation Data
csim_data = {} #Test Case as key
asim_data = {} #Algorithm as key


for case in tcases:
    if not case in csim_data:
        csim_data[case] = []
    for alg in algorithm.algorithms:
        if not alg in aim_data:
            asim_data[alg] = []
        aname = alg.__class__.__name__
        with open(case.name + '.' + aname, 'rb') as f:
            sdata = pickle.load(f)
            csim_data[case] += [ sdata ]
            asim_data[alg] += [ sdata ]

            if opts["verbose"]:
                print(("Loaded Simulation Data for Case %s for Algorithm %s" % (case.name, aname)))
    
if opts["verbose"]:
    print("Loaded Simulation Data")

#Interative Command Prompt
try:
    while True:
        uinput = input("(display):")
        uargv = uinput.split()

        #List Test Cases
        if uargv[0] == 'l':
            for i in range(0, len(tcases)):
                print(("[%d] %s" % (i, tcases[i].name)))
        #List Algorithms
        elif uargv[0] == 'a':
            for i in range(0, len(algorithm.algorithms)):
                print(("[%d] %s" % (i, 
                    algorithm.algorithms[i].__class__.__name__)))

        elif uargv[0] == 'c':
            if len(uargv) == 2 and 0 <= c <= 100:
                opts["constraint"] = int(uargv[1])
            else:
                print("Unknown constraint percentage. Usage: c <percentage>")
        #Plot Graph Processin Time
        elif uargv[0] == 't':
            #Extract Data
            atime = {}
            aname = []
            for alg in algorithm.algorithms:
                if not alg in atime:
                    atime[alg] = 0
                    aname[alg] = alg.__class__.__name__
                for sdata in asim_data[alg]:
                    atime[alg] += sdata["time"]
            
            #Plot Graph
            x_val = rang(len(algorithm.algorithms))
            y_val = atime.values()
            
            plt.hbar(x_val, y_val, align="center")
            plt.xticks(x_val, aname)
            plt.xlabel("Time (seconds)")
            plt.title("Processing Time")
            plt.show()

        #Select Test Case
        elif uargv[0] == 's':
            if len(uargv) == 1:
                print("Unselected Test Case:" + tcases[opts["selector"]].name)
                opts["selector"] = -1
            elif uargv[1] in range(0, len(tcases)):
                opts["selector"] = uargv[1]
                print("Selected Test Case:" + tcases[opts["selector"]].name)
            else:
                print("Unknown Test Case id. Run 'l' to list test cases")
        
        elif not opts["selector"] == -1:
            #Test Case Selected
            if uargv[0] == 'p':
                if len(uargv) < 2:
                    print "Usage: p <algorithm id>. Run 'a' to list algorithm"
                elif uargv[1] in range(0, len(algorithm.algorithms)):
                    alg = algorithm.algorithms[uargv[1]]
                    aname = alg.__class__.__name__
                    case = tcases[opts["selector"]]
                    
                    with open(case.name + "." + aname + ".profile", "rb") as f:
                        print(("Profile of %s running %s" % (aname, case.name)))
                        #Dump Profile file to STDOUT
                        print f.read()
                else:
                    print("Unknown Algorithm id. Run 'a' to list algorithms")
                
        else:
            raise NotImplementedError

except EOFError:
    sys.exit(0)
