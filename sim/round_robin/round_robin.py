from utils import Task, Interrupt
from datetime import timedelta
import itertools


class RoundRobinScheduler:
    @staticmethod
    def stddev(data):
        n = len(data)
        if n < 2:
            return 0

        mean = sum(data)/float(n)
        ss = sum((x-mean)**2 for x in data)
        return (ss/n)**0.5

    @staticmethod
    def modulo_time(dividend, divisor):
        return dividend.total_seconds() % divisor.total_seconds()

    @staticmethod
    def floor_divide_time(dividend, divisor):
        return int(dividend.total_seconds()/divisor.total_seconds())

    @staticmethod
    def split_task(task, block_size):
        quotient = RoundRobinScheduler.floor_divide_time(
                   task.duration, block_size)
        remainder = RoundRobinScheduler.modulo_time(task.duration, block_size)

        tasks = [Task(task.name, block_size, task.deadline)
                 for x in range(quotient)]

        if remainder != 0:
            tasks.append(Task(task.name, timedelta(seconds=remainder),
                              task.deadline))

        return tasks

    @staticmethod
    def valid(schedule, start_time):
        cum_time = start_time

        for schedulable in schedule:
            if isinstance(schedulable, Task) and\
                    (schedulable.duration + cum_time) > schedulable.deadline:
                return False
            cum_time += schedulable.duration

        return True

    @staticmethod
    def stddev_sum(schedule):
        start_times = {}
        cum_minutes = 0

        for schedulable in schedule:
            if isinstance(schedulable, Interrupt):
                cum_minutes += schedulable.duration.total_seconds()//60
                continue
            equiv_task = Task(schedulable.name, timedelta(0),
                              schedulable.deadline)
            start_times[equiv_task] = start_times.get(equiv_task, []) +\
                [cum_minutes]

            cum_minutes += schedulable.duration.total_seconds()//60

        return sum(map(RoundRobinScheduler.stddev, start_times.values()))

    @staticmethod
    def calculate_rating(schedule):
        return RoundRobinScheduler.stddev_sum(schedule) +\
            RoundRobinScheduler.min_distance(schedule)/10.0

    @staticmethod
    def min_distance(schedule):
        latest_tasks = {}
        min_dist = None

        cum_minutes = 0
        for schedulable in schedule:
            if isinstance(schedulable, Interrupt):
                cum_minutes += schedulable.duration.total_seconds()//60
                continue

            task_equiv = Task(schedulable.name, timedelta(0),
                              schedulable.deadline)
            if task_equiv not in latest_tasks:
                latest_tasks[task_equiv] = cum_minutes
            else:
                diff = cum_minutes - latest_tasks[task_equiv]
                min_dist = min(x for x in (diff, min_dist) if x is not None)

            latest_tasks[task_equiv] = cum_minutes +\
                schedulable.duration.total_seconds()//60
            cum_minutes += schedulable.duration.total_seconds()//60

        return min_dist or 0

    @staticmethod
    def insert_interrupts(tasks, interrupts, start_time):
        interrupts_to_insert = sorted(interrupts, key=lambda x: x.start)
        schedule = []
        cur_time = start_time

        for task in tasks:
            if interrupts_to_insert:
                interrupt = interrupts_to_insert[0]

                task_end_time = cur_time + task.duration
                if task_end_time > interrupt.start:
                    time_till_interrupt = interrupt.start-cur_time
                    if time_till_interrupt:
                        task_front = Task(task.name, time_till_interrupt,
                                          task.deadline)
                        schedule.append(task_front)

                    while task_end_time >= interrupt.start:
                        schedule.append(interrupt)
                        interrupts_to_insert.pop(0)
                        cur_time += interrupt.duration
                        if interrupts_to_insert:
                            interrupt = interrupts_to_insert[0]
                        else:
                            break

                    task_back = Task(task.name,
                                     task.duration-time_till_interrupt,
                                     task.deadline)
                    schedule.append(task_back)
                    cur_time += task.duration
                    continue
                elif task_end_time == interrupt.start:
                    schedule.append(task)
                    schedule.append(interrupt)
                    interrupts_to_insert.pop(0)

                    cur_time += task.duration + interrupt.duration
                    continue
            schedule.append(task)
            cur_time += task.duration

        return schedule

    @staticmethod
    def schedule(tasks, interrupts, start_time, block_size):
        split_tasks = [RoundRobinScheduler.split_task(task, block_size)
                       for task in tasks]
        split_tasks = itertools.chain.from_iterable(split_tasks)

        # Reduce the number of the same permutations. However, it takes more
        # time as it converts from a generator to a set
        perms = set(list(itertools.permutations(split_tasks)))

        best_perm = (None, -1)
        for _perm in perms:
            perm = RoundRobinScheduler.insert_interrupts(_perm, interrupts,
                                                         start_time)
            if not RoundRobinScheduler.valid(perm, start_time):
                continue

            rating = RoundRobinScheduler.calculate_rating(perm)
            if rating > best_perm[1]:
                best_perm = (perm, rating)

        return list(best_perm[0])
