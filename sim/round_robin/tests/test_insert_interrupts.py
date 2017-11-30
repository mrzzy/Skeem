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
        self.block_size = minutes(20)

    def to_time(self, blocks):
        return self.block_size*blocks

    def create_task(self, name, duration_blocks, end_blocks):
        return self.factory.create_task(name,
                                        self.to_time(duration_blocks),
                                        self.to_time(end_blocks))

    def create_interrupt(self, name, start_blocks, end_blocks):
        return self.factory.create_interrupt(name,
                                             self.to_time(start_blocks),
                                             self.to_time(end_blocks))

    def test_split_tasks_with_interrupts(self):
        test_input_tasks = [Task("A", minutes(20), self.epoch)]
        test_input_interrupts = [Interrupt("iA", self.epoch+minutes(10),
                                           self.epoch+minutes(20))]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                Task("A", minutes(10), self.epoch),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(20)),
                Task("A", minutes(10), self.epoch)]

        self.assertEqual(answer, test_output)

    def test_split_tasks_with_continuous_interrupts(self):
        test_input_tasks = [Task("A", minutes(20), self.epoch)]
        test_input_interrupts = [
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(20)),
                Interrupt("iB", self.epoch+minutes(20),
                          self.epoch+minutes(30))]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                Task("A", minutes(10), self.epoch),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(20)),
                Interrupt("iB", self.epoch+minutes(20),
                          self.epoch+minutes(30)),
                Task("A", minutes(10), self.epoch)]

        self.assertEqual(answer, test_output)

    def test_separate_tasks_with_continuous_interrupts(self):
        test_input_tasks = [
                self.create_task("A", 1, 4),
                self.create_task("A", 1, 4)]
        test_input_interrupts = [
                self.create_interrupt("iA", 1, 2),
                self.create_interrupt("iB", 2, 3)]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                self.create_task("A", 1, 4),
                self.create_interrupt("iA", 1, 2),
                self.create_interrupt("iB", 2, 3),
                self.create_task("A", 1, 4)]

        self.assertEqual(answer, test_output)

    def test_separate_multiple_tasks_with_interrupts(self):
        test_input_tasks = [
                self.create_task("A", 1, 5),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 5),
                self.create_task("A", 1, 5)]

        test_input_interrupts = [self.create_interrupt("iA", 1, 2)]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                self.create_task("A", 1, 5),
                self.create_interrupt("iA", 1, 2),
                self.create_task("B", 1, 5),
                self.create_task("A", 1, 5),
                self.create_task("A", 1, 5)]

        self.assertEqual(answer, test_output)

    def test_separate_tasks_with_multiple_interrupts(self):
        test_input_tasks = [
                self.create_task("A", 1, 5),
                self.create_task("A", 1, 5),
                self.create_task("A", 1, 5)]

        test_input_interrupts = [
                self.create_interrupt("iA", 1, 2),
                self.create_interrupt("iB", 3, 4)]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                self.create_task("A", 1, 5),
                self.create_interrupt("iA", 1, 2),
                self.create_task("A", 1, 5),
                self.create_interrupt("iB", 3, 4),
                self.create_task("A", 1, 5)]

        self.assertEqual(answer, test_output)

        test_input_tasks = [
                self.create_task("A", 1, 7),
                self.create_task("C", 1, 7),
                self.create_task("A", 1, 7),
                self.create_task("B", 1, 7)]

        test_input_interrupts = [
                self.create_interrupt("iA", 1, 2),
                self.create_interrupt("iB", 4, 5),
                self.create_interrupt("iC", 6, 7)]

        test_output = RoundRobinScheduler.insert_interrupts(
            test_input_tasks, test_input_interrupts, self.epoch)

        answer = [
                self.create_task("A", 1, 7),
                self.create_interrupt("iA", 1, 2),
                self.create_task("C", 1, 7),
                self.create_task("A", 1, 7),
                self.create_interrupt("iB", 4, 5),
                self.create_task("B", 1, 7),
                self.create_interrupt("iC", 6, 7)]

        self.assertEqual(answer, test_output)
