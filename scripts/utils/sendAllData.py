#!/usr/bin/python3

import random, string, subprocess
import requests, threading

class testThread (threading.Thread):
    def __init__(self, threadID, num_of_tests):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.num_of_tests = num_of_tests
    
    def run(self):
        self.sendGETRequest(self.num_of_tests)

    def get_random_string(self, length):
        letters = string.ascii_lowercase
        result_str = ''.join(random.choice(letters) for i in range(length))
        return result_str

    def sendGETRequest(self):
        response1 = {"success": 0, "fail": 0}

        for i in range(self.num_of_tests):
            request1 = requests.get("http://localhost:8026/" + self.get_random_string(8))
            if request1.status_code == 404:
                response1["success"] += 1
            else:
                response1["fail"] += 1
        print("response1:" + str(response1))





def test(num_of_threads):
    threads = []
    for i in range(num_of_threads):
        threads.append(testThread(i, num_of_total_tests))
    for t in threads:
        t.start()
    for t in threads:
        t.join()

if __name__=='__main__':
    test(7)

