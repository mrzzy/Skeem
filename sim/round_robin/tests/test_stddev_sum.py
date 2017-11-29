import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestStddevSum(unittest.TestCase):
    def to_time(self, blocks):
        return self.block_size*blocks

    def create_task(self, name, duration_blocks, end_blocks):
        return self.factory.create_task(name,
                                        self.to_time(duration_blocks),
                                        self.to_time(end_blocks))

    def setUp(self):
        self.epoch = datetime.utcfromtimestamp(0)
        self.factory = Factory(self.epoch)
        self.block_size = minutes(10)

    def test_simple_stddev_sum(self):
        test_input = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 3),
                self.create_task("A", 1, 3)]

        test_output = RoundRobinScheduler.stddev_sum(test_input)

        answer = self.to_time(1)

        self.assertEqual(answer, minutes(test_output))

    def test_stddev_sum_different_durations(self):
        test_input1 = [
                Task("A", minutes(10), self.epoch),
                Task("B", minutes(20), self.epoch),
                Task("A", minutes(10), self.epoch),
                Task("B", minutes(30), self.epoch)]

        test_input2 = [
                Task("A", minutes(10), self.epoch),
                Task("B", minutes(30), self.epoch),
                Task("A", minutes(10), self.epoch),
                Task("B", minutes(20), self.epoch)]

        test_output1 = RoundRobinScheduler.stddev_sum(test_input1)
        test_output2 = RoundRobinScheduler.stddev_sum(test_input2)

        self.assertTrue(test_output1 < test_output2)
