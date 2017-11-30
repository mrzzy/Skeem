import unittest
from datetime import datetime, timedelta
from ..utils import Task, Interrupt, Factory

from ..round_robin import RoundRobinScheduler


def minutes(x):
    return timedelta(minutes=x)


class TestOddDurations(unittest.TestCase):
    def setUp(self):
        self.epoch = datetime.utcfromtimestamp(0)
        self.block_size = minutes(20)

    def test_alternate_odd_duration(self):
        test_input = [
                Task("A", minutes(30), self.epoch+minutes(50)),
                Task("B", minutes(20), self.epoch+minutes(50))]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.epoch, self.block_size)

        answer1 = [
                Task("A", minutes(20), self.epoch+minutes(50)),
                Task("B", minutes(20), self.epoch+minutes(50)),
                Task("A", minutes(10), self.epoch+minutes(50))]

        answer2 = [
                Task("A", minutes(20), self.epoch+minutes(50)),
                Task("B", minutes(20), self.epoch+minutes(50)),
                Task("A", minutes(10), self.epoch+minutes(50))]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_split(self):
        test_input = [
                Task("A", minutes(10), self.epoch+minutes(50)),
                Task("B", minutes(40), self.epoch+minutes(50))]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.epoch, self.block_size)

        answer = [
                Task("B", minutes(20), self.epoch+minutes(50)),
                Task("A", minutes(10), self.epoch+minutes(50)),
                Task("B", minutes(20), self.epoch+minutes(50))]

        self.assertEquals(answer, test_output)

    def test_prefer_to_split_with_longer_durations(self):
        test_input = [
                Task("A", minutes(30), self.epoch+minutes(70)),
                Task("B", minutes(40), self.epoch+minutes(70))]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.epoch, self.block_size)

        answer1 = [
                Task("A", minutes(10), self.epoch+minutes(70)),
                Task("B", minutes(20), self.epoch+minutes(70)),
                Task("A", minutes(20), self.epoch+minutes(70)),
                Task("B", minutes(20), self.epoch+minutes(70))]

        answer2 = [
                Task("B", minutes(20), self.epoch+minutes(70)),
                Task("A", minutes(20), self.epoch+minutes(70)),
                Task("B", minutes(20), self.epoch+minutes(70)),
                Task("A", minutes(10), self.epoch+minutes(70))]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_split_multiple_with_long_durations(self):
        test_input = [
                Task("A", minutes(30), self.epoch+minutes(60)),
                Task("B", minutes(30), self.epoch+minutes(100)),
                Task("C", minutes(30), self.epoch+minutes(160)),
                Task("D", minutes(30), self.epoch+minutes(160))]

        test_output = RoundRobinScheduler.schedule(
            test_input, [], self.epoch, self.block_size)

        answer1 = [
                Task("A", minutes(10), self.epoch+minutes(60)),
                Task("B", minutes(10), self.epoch+minutes(100)),
                Task("D", minutes(20), self.epoch+minutes(160)),
                Task("A", minutes(20), self.epoch+minutes(60)),
                Task("C", minutes(20), self.epoch+minutes(160)),
                Task("B", minutes(20), self.epoch+minutes(100)),
                Task("D", minutes(10), self.epoch+minutes(160)),
                Task("C", minutes(10), self.epoch+minutes(160))]

        answer2 = [
                Task("A", minutes(10), self.epoch+minutes(60)),
                Task("B", minutes(10), self.epoch+minutes(100)),
                Task("C", minutes(20), self.epoch+minutes(160)),
                Task("A", minutes(20), self.epoch+minutes(60)),
                Task("D", minutes(20), self.epoch+minutes(160)),
                Task("B", minutes(20), self.epoch+minutes(100)),
                Task("C", minutes(10), self.epoch+minutes(160)),
                Task("D", minutes(10), self.epoch+minutes(160))]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_split_with_interrupts(self):
        test_input_tasks = [
                Task("A", minutes(30), self.epoch+minutes(80)),
                Task("B", minutes(30), self.epoch+minutes(80))]

        test_input_interrupts = [Interrupt("iA", self.epoch+minutes(10),
                                           self.epoch+minutes(30))]

        test_output = RoundRobinScheduler.schedule(
            test_input_tasks, test_input_interrupts,
            self.epoch, self.block_size)

        answer1 = [
                Task("A", minutes(10), self.epoch+minutes(80)),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(30)),
                Task("B", minutes(20), self.epoch+minutes(80)),
                Task("A", minutes(20), self.epoch+minutes(80)),
                Task("B", minutes(10), self.epoch+minutes(80))]

        answer2 = [
                Task("B", minutes(10), self.epoch+minutes(80)),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(30)),
                Task("A", minutes(20), self.epoch+minutes(80)),
                Task("B", minutes(20), self.epoch+minutes(80)),
                Task("A", minutes(10), self.epoch+minutes(80))]

        self.assertTrue(answer1 == test_output or answer2 == test_output)

    def test_alternate_with_interrupts(self):
        # Test still failing
        test_input_tasks = [
                Task("A", minutes(30), self.epoch+minutes(100)),
                Task("B", minutes(30), self.epoch+minutes(100))]

        test_input_interrupts = [
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(30)),
                Interrupt("iB", self.epoch+minutes(40),
                          self.epoch+minutes(60))]

        test_output = RoundRobinScheduler.schedule(
            test_input_tasks, test_input_interrupts,
            self.epoch, self.block_size)

        answer1 = [
                Task("A", minutes(10), self.epoch+minutes(100)),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(30)),
                Task("B", minutes(10), self.epoch+minutes(100)),
                Interrupt("iB", self.epoch+minutes(40),
                          self.epoch+minutes(60)),
                Task("A", minutes(20), self.epoch+minutes(100)),
                Task("B", minutes(20), self.epoch+minutes(100))]

        answer2 = [
                Task("B", minutes(10), self.epoch+minutes(100)),
                Interrupt("iA", self.epoch+minutes(10),
                          self.epoch+minutes(30)),
                Task("A", minutes(10), self.epoch+minutes(100)),
                Interrupt("iB", self.epoch+minutes(40),
                          self.epoch+minutes(60)),
                Task("B", minutes(20), self.epoch+minutes(100)),
                Task("A", minutes(20), self.epoch+minutes(100))]

        self.assertTrue(answer1 == test_output or answer2 == test_output)
