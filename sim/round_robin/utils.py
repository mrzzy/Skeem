class Task(object):
    def __init__(self, name, duration, deadline):
        super(Task, self).__init__()
        self.name = name
        self.duration = duration
        self.deadline = deadline

    def __str__(self):
        return "Task({}, {}, {})".format(self.name, self.duration,
                                         self.deadline)

    def __repr__(self):
        return self.__str__()

    def __eq__(self, other):
        return isinstance(other, Task) and\
            self.name == other.name and\
            self.duration == other.duration and\
            self.deadline == other.deadline

    def __hash__(self):
        return hash((self.name, self.duration, self.deadline))


class Interrupt(object):
    def __init__(self, name, start, end):
        self.name = name
        self.start = start
        self.end = end
        self.duration = self.end-self.start

    def __str__(self):
        return "Interrupt({}, {}, {})".format(self.name, self.start, self.end)

    def __repr__(self):
        return __str__()

    def __eq__(self, other):
        return isintance(other, Interrupt) and\
            self.name == other.name and\
            self.start == other.start and\
            self.end == other.end and\
            self.duration == other.duration


class Factory(object):
    def __init__(self, start_time):
        self.start_time = start_time

    def create_task(self, name, duration, relative_deadline):
        return Task(name, duration, self.start_time+relative_deadline)

    def create_interrupt(self, name, relative_start, relative_end):
        return Task(name,
                    self.start_time+relative_start,
                    self.start_time+relative_end)
