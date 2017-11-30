import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestSplitTask(unittest.TestCase):
    def setUp(self):
        self.epoch = datetime.utcfromtimestamp(0)
        self.factory = Factory(self.epoch)
        self.block_size = minutes(10)

    def to_time(self, blocks):
        return self.block_size*blocks

    def create_task(self, name, duration_blocks, end_blocks):
        return self.factory.create_task(name,
                                        self.to_time(duration_blocks),
                                        self.to_time(end_blocks))

    def test_split_without_remainder(self):
        test_input = self.create_task("A", 2, 3)

        test_output = RoundRobinScheduler.split_task(
            test_input, self.block_size)

        answer = [
                self.create_task("A", 1, 3),
                self.create_task("A", 1, 3)]

        self.assertEqual(answer, test_output)

    def test_split_with_remainder(self):
        test_input = Task("A", minutes(15), self.epoch)

        test_output = RoundRobinScheduler.split_task(
            test_input, minutes(10))

        answer = [
                Task("A", minutes(10), self.epoch),
                Task("A", minutes(5), self.epoch)]

        self.assertEqual(answer, test_output)
