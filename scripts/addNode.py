import argparse, subprocess, os
import utils.py_utils as utils

DEFAULT_PROXY_PORT=8030
DEFAULT_NODE_PORT=8026
ALT_PROXY_PORT=8031
ALT_NODE_PORT=8026
ALT_DB_NAME="new_db.db"

parser = argparse.ArgumentParser(description='Add a worker node that runs URLShortner.')
parser.add_argument('hostName', metavar='hostName', type=str,
                   help='The host name (or IP address) of the node.')
args = parser.parse_args()
new_host = args.hostName

if __name__ == "__main__":
    # get current shards
    # get 1 representative from each shard
    # add node to host.txt
    # start URLShortner on new node
    print(f"Adding worker node {new_host}")
    hosts = utils.readHosts()
    shards = utils.getShardsFromHosts(hosts)
    representative_nodes = [shard[0] for shard in shards.values()]
    print(f"Chose representative (master) nodes f{*representative_nodes}")

    utils.addHostToFile(new_host)

    print(f"Starting URLShortner on {new_host}")
    out = subprocess.check_output(["ssh", new_host, f"cd {os.getcwd()}", "&&", "./startNode"]) # start the default URL Shortner
    print(out)

    print(f"Starting alternate (data redistribution) proxy")
    out = subprocess.check_output(["cd", "..", "&&", "java", "proxy/Proxy", "-d", ALT_PROXY_PORT, ALT_NODE_PORT]) # start an alternate proxy used for data redistribution
    print(out)


    # start alternate URL Shortner on each host for data redistribution
    for host in hosts:
        print(f"Starting alternate (data redistribution) URLShortner on node {host}")
        out = subprocess.check_output(["ssh", host, "cd", os.getcwd(), "&&", "./startNode", "-d", ALT_PROXY_PORT, "newdb.db"])
        print(out)



'''
# python program takes in node(node that we are trying to add.)
    looks at host.txt file
    save representative(master) nodes 
    add node to host.txt
    start URLShortner on new node
    run redistribute proxy (reads from new host file)
    all master nodes send data to redistribute proxydis

# python program takes in node(node that we are trying to add.)
    looks at host.txt file
    save representative(master) nodes 
    add node to host.txt
    start URLShortner on new node
    run redistribute proxy (reads from new host file)
    all master nodes send data to redistribute proxy which is connected to URLShortnerResdribute (write method) new test.db
'''