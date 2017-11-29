import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestDeadline(unittest.TestCase):
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

    def test_schedule_due_first(self):
        test_input = [
                self.create_task("A", 1, 1),
                self.create_task("B", 1, 2)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 1),
                self.create_task("B", 1, 2)]
        self.assertEqual(answer, test_output)

    def test_schedule_due_first_despite_rating(self):
        test_input = [
                self.create_task("A", 2, 2),
                self.create_task("B", 1, 3)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 2),
                self.create_task("A", 1, 2),
                self.create_task("B", 1, 3)]
        self.assertEqual(answer, test_output)

    def test_schedule_multiple_due_first(self):
        test_input = [
                self.create_task("A", 1, 1),
                self.create_task("B", 1, 2),
                self.create_task("C", 1, 3)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 1),
                self.create_task("B", 1, 2),
                self.create_task("C", 1, 3)]
        self.assertEqual(answer, test_output)

    def test_schedule_multiple_due_first_unordered(self):
        test_input = [
                self.create_task("A", 2, 4),
                self.create_task("B", 1, 1),
                self.create_task("C", 1, 2)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("B", 1, 1),
                self.create_task("C", 1, 2),
                self.create_task("A", 1, 4),
                self.create_task("A", 1, 4)]
        self.assertEqual(answer, test_output)

    def test_split_based_on_deadline(self):
        test_input = [
                self.create_task("A", 2, 3),
                self.create_task("B", 2, 6),
                self.create_task("C", 2, 6)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 3),
                self.create_task("C", 1, 6),
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 6),
                self.create_task("C", 1, 6),
                self.create_task("B", 1, 6)]

        answer2 = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 6),
                self.create_task("A", 1, 3),
                self.create_task("C", 1, 6),
                self.create_task("B", 1, 6),
                self.create_task("C", 1, 6)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_single_split(self):
        test_input = [
                self.create_task("A", 1, 3),
                self.create_task("B", 4, 5)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("B", 1, 5),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 5),
                self.create_task("B", 1, 5)]
        self.assertEqual(answer, test_output)

    def test_double_split(self):
        test_input = [
                self.create_task("A", 2, 4),
                self.create_task("B", 4, 6)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 6),
                self.create_task("B", 1, 6),
                self.create_task("A", 1, 4),
                self.create_task("B", 1, 6),
                self.create_task("B", 1, 6)]

        self.assertEqual(answer, test_output)

    def test_alternate_based_on_deadline(self):
        test_input = [
                self.create_task("A", 1, 3),
                self.create_task("B", 3, 5),
                self.create_task("C", 1, 5)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 5),
                self.create_task("C", 1, 5),
                self.create_task("B", 1, 5)]

        self.assertEqual(answer, test_output)

    def test_alternate_unequally_based_on_deadline(self):
        # Error same as no_deadline
        test_input = [
                self.create_task("A", 2, 3),
                self.create_task("B", 2, 5),
                self.create_task("C", 1, 5)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 3),
                self.create_task("C", 1, 5),
                self.create_task("B", 1, 5)]

        self.assertEqual(answer, test_output)

    def test_deep_alternation_based_on_deadline(self):
        # Error same as no_deadline
        test_input = [
                self.create_task("A", 2, 3),
                self.create_task("B", 2, 5),
                self.create_task("C", 2, 8),
                self.create_task("D", 2, 8)]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.factory.start_time, self.block_size)

        answer1 = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 3),
                self.create_task("C", 1, 8),
                self.create_task("B", 1, 5),
                self.create_task("D", 1, 8),
                self.create_task("C", 1, 8),
                self.create_task("D", 1, 8)]

        answer2 = [
                self.create_task("A", 1, 3),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 3),
                self.create_task("D", 1, 8),
                self.create_task("B", 1, 5),
                self.create_task("C", 1, 8),
                self.create_task("D", 1, 8),
                self.create_task("C", 1, 8)]

        self.assertTrue(answer1 == test_output or answer2 == test_output)
