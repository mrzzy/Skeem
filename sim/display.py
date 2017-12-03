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
        "constraint": 100,
        "verbose": False
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

if not os.path.exists(opts["directory"]):
    os.makedirs(opts["directory"])
os.chdir(opts["directory"])

if opts["verbose"]:
    print("Skeem Display")
    print("Program options:")
    pretty(opts)
    pdivider()

tpaths = glob.glob("TestCase:*.case")
tcases = []
selector = -1
for tpath  in tpaths:
    with open(tpath, 'rb') as f:
        tcase = pickle.load(f)

        if opts["verbose"]:
            print("Loaded Test case:" + tcase.name)
        tcases += [ tcase ]

if opts["verbose"]:
    print(("Loaded %d Test Cases" % len(tcases)))

sim_data = {}
for case in tcases:
    sim_data[case] = {}
    for alg in algorithm.algorithms:
        aname = alg.__class__.__name__
        with open(case.name + '.' + aname, 'rb') as f:
            sim_data[case][alg] = pickle.load(f)
            if opts["verbose"]:
                print(("Loaded Simulation Data for Case %s for Algorithm %s" % (case.name, aname)))
    
if opts["verbose"]:
    print("Loaded Simulation Data")

try:
    while True:
        uinput = input("(display):")
        uargv = uinput.split()

        if uargv[0] == 'l':
            for i in range(0, len(tcases)):
                print(("[%d] %s" % (i, tcases[i].name)))
        elif uargv[0] == 'a':
            for i in range(0, len(algorithm.algorithms)):
                print(("[%d] %s" % (i, algorithm.algorithms[i].__class__.__name__)))
        elif uargv[0] == 's':
            if len(uargv) == 1:
                print("Unselected Test Case:" + tcases[selector].name)
                selector = -1
            elif uargv[1] in range(0, len(tcases)):
                selector = uargv[1]
                print("Selected Test Case:" + tcases[selector].name)
            else:
                print("Unknown Test Case id. Run 'l' to list test cases")
        elif uargv[0] == 't':
            atimes = {}
            for alg in algorithm.algorithms:
                atimes[alg] = 0
            
            for tdata in sim_data.values():
                for (alg, sdata) in tdata:
                    atime[alg] += sdata["time"]
            
            plt.bar(range(0, len(atimes.values())%, atimes.values())
            labels = [alg.__class__.__name__ for alg in algorithm.algorithms]
            plt.xticks(range(0, len(atimes.value())), lables, rotation='vertical')
        
            plt.show()
            
        elif not selector == -1:
            #Case Selected Mode 
            if uargv[0] == 'p':
                if len(uargv) < 2:
                    print "Usage: p <algorithm id>. Run 'a' to list algorithm"
                elif uargv[1] in range(0, len(algorithm.algorithms)):
                    alg = algorithm.algorithms[uargv[1]]
                    aname = alg.__class__.__name__
                    case = tcases[selector]
                    
                    with open(case.name + "." + aname + ".profile", "rb") as f:
                        print(("Profile of %s running %s" % (aname, case.name)))
                        print f.read()
                else:
                    print("Unknown Algorithm id. Run 'a' to list algorithms")
            elif uargv[0] == t:
                case = tcases[selector]
                sdata = sim_data[case]
                
                plt.bar
        
                
        else:
            raise NotImplementedError

except EOFError:
    sys.exit(0)
