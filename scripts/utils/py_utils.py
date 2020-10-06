from typing import *

"""
Read hosts from hosts file. 
"""
def readHosts(path: str = "../proxy/hosts.txt") -> List[str]:
    with open(path, 'r') as hosts_file:
        hosts = [line.rstrip() for line in hosts_file]
    return hosts

"""
Assign hosts to their respective shard.

Return shard dictionary.
"""
def getShardsFromHosts(hosts: List[str]) -> Dict[int, str]:
    shards = {}
    num_shards = len(hosts) // 2
    for i in range(num_shards):
        shards[i] = []

    current_shard = 0
    for host in hosts:
        shards[current_shard].append(host)
        current_shard = (current_shard + 1) % num_shards

    return shards