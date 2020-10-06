from typing import *


def readHosts(path: str = "../proxy/hosts.txt") -> List[str]:
    """
    Read hosts from hosts file.
    """

    with open(path, "r") as hosts_file:
        hosts = [line.rstrip() for line in hosts_file]
    return hosts


def getShardsFromHosts(hosts: List[str]) -> Dict[int, str]:
    """
    Assign hosts to their respective shard.

    Return shard dictionary.
    """

    shards = {}
    num_shards = len(hosts) // 2
    for i in range(num_shards):
        shards[i] = []

    current_shard = 0
    for host in hosts:
        shards[current_shard].append(host)
        current_shard = (current_shard + 1) % num_shards

    return shards