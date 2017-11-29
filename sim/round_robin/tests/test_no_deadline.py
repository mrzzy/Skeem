import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestNoDeadline(unittest.TestCase):
    def setUp(self):
        epoch = datetime.utcfromtimestamp(0)
        self.factory = Factory(epoch)
        self.block_size = minutes(10)

    def to_time(self, blocks):
        return self.block_size*blocks

    def create_task(self, name, duration_blocks, end_blocks):
        return self.factory.create_task(name,
                                        self.to_time(duration_blocks),
                                        self.to_time(end_blocks))

    def test_split(self):
        test_input = [
                self.create_task("A", 2, 3),
                self.create_task("B", 1, 3)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 3),
                self.create_task("A", 1, 3)]
        self.assertEqual(answer, test_output)

    def test_odd_split(self):
        test_input = [
                self.create_task("A", 3, 4),
                self.create_task("B", 1, 4)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 4),
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4)]

        answer2 = [
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4),
                self.create_task("A", 1, 4)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_alternate_split(self):
        test_input = [
                self.create_task("A", 2, 4),
                self.create_task("B", 2, 4)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4)]

        answer2 = [
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_multiple_tasks(self):
        test_input = [
                self.create_task("A", 2, 4),
                self.create_task("B", 1, 4),
                self.create_task("C", 1, 4)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("C", 1, 4),
                self.create_task("A", 1, 4)]

        answer2 = [
                self.create_task("A", 1, 4),
                self.create_task("C", 1, 4),
                self.create_task("B", 1, 4),
                self.create_task("A", 1, 4)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_multiple_tasks_alternate(self):
        test_input = [
                self.create_task("A", 2, 5),
                self.create_task("B", 2, 5),
                self.create_task("C", 1, 5)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 5),
                self.create_task("B", 1, 5),
                self.create_task("C", 1, 5),
                self.create_task("A", 1, 5),
                self.create_task("B", 1, 5)]

        answer2 = [
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 5),
                self.create_task("C", 1, 5),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 5)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)
