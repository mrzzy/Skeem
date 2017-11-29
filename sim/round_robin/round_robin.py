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
    def valid(tasks, start_time):
        cum_time = start_time

        for task in tasks:
            if (task.duration + cum_time) > task.deadline:
                return False
            cum_time += task.duration

        return True

    @staticmethod
    def stddev_sum(tasks):
        start_times = {}
        cum_minutes = 0

        for task in tasks:
            equiv_task = Task(task.name, timedelta(0), task.deadline)
            start_times[equiv_task] = start_times.get(equiv_task, []) +\
                [cum_minutes]

            cum_minutes += task.duration.total_seconds()//60

        return sum(map(RoundRobinScheduler.stddev, start_times.values()))

    @staticmethod
    def calculate_rating(tasks):
        return RoundRobinScheduler.stddev_sum(tasks) +\
            RoundRobinScheduler.min_distance(tasks)/10.0

    @staticmethod
    def min_distance(tasks):
        latest_tasks = {}
        min_dist = None

        cum_minutes = 0
        for task in tasks:
            task_equiv = Task(task.name, timedelta(0), task.deadline)
            if task_equiv not in latest_tasks:
                latest_tasks[task_equiv] = cum_minutes
            else:
                diff = cum_minutes - latest_tasks[task_equiv]
                min_dist = min(x for x in (diff, min_dist) if x is not None)

            latest_tasks[task_equiv] = cum_minutes +\
                task.duration.total_seconds()//60
            cum_minutes += task.duration.total_seconds()//60

        return min_dist or 0

    @staticmethod
    def schedule(tasks, interrupts, start_time, block_size):
        split_tasks = [RoundRobinScheduler.split_task(task, block_size)
                       for task in tasks]
        split_tasks = itertools.chain.from_iterable(split_tasks)

        perms = set(list(itertools.permutations(split_tasks)))

        best_perm = (None, -1)
        for perm in perms:
            if not RoundRobinScheduler.valid(perm, start_time):
                continue

            rating = RoundRobinScheduler.calculate_rating(perm)
            if rating > best_perm[1]:
                best_perm = (perm, rating)

        return list(best_perm[0])
