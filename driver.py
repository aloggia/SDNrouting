import requests
import sys
import subprocess

if __name__ == '__main__':
    # TODO: get command line arg for network to route
    # TODO: request the correct website ip and port
    # website_endpoint = str(sys.argv[1])
    website_endpoint = "topology1"
    file_name = "./" + website_endpoint + ".json"
    website = "http://www.goatgoose.com:2222/get_topology/" + website_endpoint
    r = requests.get(website)

    with open(file_name, 'w') as file:
        file.write(str(r.json()))
    file.close()

    with open(file_name, 'r') as file:
        print(file.read())

    file.close()

    # TODO: Need a way to get the created json from the java program and send it to the network
    output = subprocess.check_output("java shortestPath " + file_name)
