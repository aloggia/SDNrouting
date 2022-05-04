import json

import requests
import sys
import subprocess
import os

if __name__ == '__main__':
    connection_dest = str(sys.argv[1])
    website_endpoint = str(sys.argv[2])
    # website_endpoint = "topology2"
    file_name = website_endpoint + ".json"
    get_website = "http://" + connection_dest + ":2222/get_topology/" + website_endpoint
    post_website = "http://" + connection_dest + ":2222/set_tables/" + website_endpoint
    working_directory = os.getcwd()
    java_tuples = working_directory + "\javatuples-1.2.jar"
    java_json = working_directory + "\json-simple-1.1.jar"
    class_path = working_directory + ";" + java_tuples + ";" + java_json
    # print(working_directory)
    # print(class_path)
    r = requests.get(get_website).json()
    with open(file_name, 'w') as file:
        file.write(json.dumps(r))
    file.close()

    # TO COMPILE: navigate to directory holding jar files, .java file, and topology we want routing tables for
    # run: javac -classpath ".\pwd;.\javatuples-1.2.jar;.\json-simple-1.1.jar" shortestPath.java
    # But with the complete path to the pwd
    subprocess.run(["javac", "-classpath", class_path, "shortestPath.java"])
    subprocess.run(['java', '-classpath', class_path, 'shortestPath', file_name])

    with open("routingTable.json", 'r') as send_file:
        json_to_send = json.dumps(json.load(send_file))
        site_results = requests.post(post_website, data=json_to_send)
        print(site_results.json())
