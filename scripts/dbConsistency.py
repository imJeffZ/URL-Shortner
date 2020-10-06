from typing import *
import subprocess
from utils.py_utils import *


"""
Get the number of lines in host's /virtual/database.txt file.

Return number of lines or None if file cannot be opened.
"""
def getNumEntries(host: str) -> int:
    try:
        return int(subprocess.check_output(["ssh", host, "wc", "-l", "/virtual/database.txt", 
                                    "|", "awk", "'{print $1}'"], stderr=subprocess.STDOUT).decode("utf-8").strip())
    except subprocess.CalledProcessError as e:
        return None

""" 
Copy database found from host with max values to hosts which have less in the same shard.

Return None
"""
def copyDatabase(fromHost: str, toHost: str) -> None:
    subprocess.check_output(["ssh", fromHost, "scp", "/virtual/database.txt", 
                                    f"{toHost}:/virtual/database.txt"], stderr=subprocess.STDOUT)

def runDbConsistency() -> int:
    # read hosts file
    # group hosts to shards (list of lists)
    # for each shard:
    #   check for consistency across nodes
    #   if not consistent:
    #       get host with most entries
    #       for all other hosts: scp database

    hosts = readHosts()
    shards = getShardsFromHosts(hosts)
    numInconsistencies = 0

    for shard in shards.values():
        numEntries_by_host = {} # {hostname: numEntries}
        for host in shard:
            numEntries = getNumEntries(host)
            if numEntries == None: continue # host is down
            numEntries_by_host[host] = numEntries
            
        maxEntries = max(numEntries_by_host.values())
        maxEntriesHost = max(numEntries_by_host, key=numEntries_by_host.get)

        for host in numEntries_by_host.keys():
            if host != maxEntriesHost and numEntries_by_host[host] < maxEntries:
                print(f"Found DB inconsistency in Shard {shard}.\nCopying from {maxEntriesHost} to {host}")
                copyDatabase(maxEntriesHost, host)
                numInconsistencies += 1
    
    return numInconsistencies



if __name__=='__main__':
    print("\nRunning DB consistency check 📊")
    numInconsistencies = runDbConsistency()
    print(f"DB check finished, found and fixed {numInconsistencies} inconsistencies. 👌")