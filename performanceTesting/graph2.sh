#!/bin/bash

# random    proxy   numThread   numReads    numWrites
./runDistributedTests random dh2020pc05 8 10000 0
./runDistributedTests random dh2020pc05 8 9000 1000
./runDistributedTests random dh2020pc05 8 8000 2000
./runDistributedTests random dh2020pc05 8 7000 3000
./runDistributedTests random dh2020pc05 8 6000 4000
./runDistributedTests random dh2020pc05 8 5000 5000
./runDistributedTests random dh2020pc05 8 4000 6000
./runDistributedTests random dh2020pc05 8 3000 7000
./runDistributedTests random dh2020pc05 8 2000 8000
./runDistributedTests random dh2020pc05 8 1000 9000
./runDistributedTests random dh2020pc05 8 0 10000