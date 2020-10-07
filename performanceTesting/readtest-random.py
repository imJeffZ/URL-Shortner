#!/usr/bin/python3

import random, string, subprocess
import requests

import random
import string

def get_random_string(length):
    letters = string.ascii_lowercase
    result_str = ''.join(random.choice(letters) for i in range(length))
    return result_str


def test(n):
	response1 = {"success": 0, "fail": 0}
	response2 = {"success": 0, "fail": 0}
	response3 = {"success": 0, "fail": 0}
	for i in range(n):
		# print(request)
		# subprocess.call(["curl", "-X", "GET", request], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
		request1 = requests.get("http://dh2020pc05:8030/"+get_random_string(8))
		if request1.status_code == 404:
			response1["success"] += 1
		else:
			response1["fail"] += 1
		
		request2 = requests.get("http://dh2020pc05:8030/"+get_random_string(8))
		if request2.status_code == 404:
			response2["success"] += 1
		else:
			response2["fail"] += 1
		
		request3 = requests.get("http://dh2020pc05:8030/"+get_random_string(8))
		if request3.status_code == 404:
			response3["success"] += 1
		else:
			response3["fail"] += 1

	print("response1:" + str(response1))
	print("response2:" + str(response2))
	print("response3:" + str(response3))

if __name__=='__main__':
	import time
	tic = time.perf_counter()
	test(1000)
	toc = time.perf_counter()
	print(f"Took {toc - tic:0.4f} seconds")
