import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestMinDistance(unittest.TestCase):
    def setUp(self):
        self.epoch = datetime.utcfromtimestamp(0)
        self.block_size = minutes(20)

    def test_calculates_min_distance_correctly(self):
        test_input = [
                Task("A", minutes(20), self.epoch),
                Task("B", minutes(20), self.epoch),
                Task("A", minutes(10), self.epoch)]

        test_output = RoundRobinScheduler.min_distance(test_input)

        self.assertEquals(20, test_output)

    def test_multiple_of_same_task(self):
        test_input = [
                Task("A", minutes(10), self.epoch),
                Task("B", minutes(20), self.epoch),
                Task("A", minutes(10), self.epoch),
                Task("C", minutes(10), self.epoch),
                Task("A", minutes(10), self.epoch)]

        test_output = RoundRobinScheduler.min_distance(test_input)

        self.assertEquals(10, test_output)
