import pprint


def pretty(arg):
    pprint.pprint(arg, indent=2)


def proppretty(arg):
    pretty({name: getattr(arg, name)
            for name in dir(arg) if not name.startswith('__')})


def prettytime(arg):
    print(datetime.timedelta(seconds=arg))


def pdivider():
    print("="*80, end="\n\n")
