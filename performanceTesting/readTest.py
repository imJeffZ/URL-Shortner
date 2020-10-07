#!/usr/bin/python3

import random, string, subprocess
import requests

def test():
	response1 = {"success": 0, "fail": 0}
	response2 = {"success": 0, "fail": 0}
	response3 = {"success": 0, "fail": 0}
	for i in range(10):
		# print(request)
		# subprocess.call(["curl", "-X", "GET", request], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
		request1 = requests.get("http://dh2020pc05:8030/c")
		if request1.status_code == 404:
			response1["success"] += 1
		else:
			response1["fail"] += 1
		
		request2 = requests.get("http://dh2020pc05:8030/b")
		if request2.status_code == 404:
			response2["success"] += 1
		else:
			response2["fail"] += 1
		
		request3 = requests.get("http://dh2020pc05:8030/gg")
		if request3.status_code == 200:
			response3["success"] += 1
		else:
			response3["fail"] += 1

	print("response1:" + str(response1))
	print("response2:" + str(response2))
	print("response3:" + str(response3))

if __name__=='__main__':
	import time
	start = time.time()
	test()
	end = time.time()
	print(end - start)
