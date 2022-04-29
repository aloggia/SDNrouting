import requests
import json
import sys

if __name__ == '__main__':
    # TODO: get command line arg for network to route
    # TODO: request the correct website ip and port
    r = requests.get(website)
    r.json()
    # TODO: compile java program
    # TODO: pass in r.json to java program
    print("Hello world!")
