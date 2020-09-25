Step 1: run Proxy.java
Step 2: run ./startService 
Step 3: run curl http://dh2026pc0x:8026/fooooooo to test the code
	x can be any int from 5 to 8
Step 4: to clean up, on each machine run
		ps aux | grep URLShortner | head -n 1 | awk '{print $2}'
	to find the pid of URLShortner, then kill -9 pid
