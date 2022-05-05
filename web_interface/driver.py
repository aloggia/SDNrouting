import json

import requests
import sys
import subprocess
import os

if __name__ == '__main__':
    connection_dest = str(sys.argv[1])
    website_endpoint = str(sys.argv[2])
    # I've only gotten the code to work when saving the json's as files, then reading to/from the files
    file_name = website_endpoint + ".json"
    get_website = "http://" + connection_dest + ":2222/get_topology/" + website_endpoint
    post_website = "http://" + connection_dest + ":2222/set_tables/" + website_endpoint
    # java compilation I've found requires the entire file path for the classpath, so we can store the
    # path to the pwd as a variable and append filenames to it
    working_directory = os.getcwd()
    # Dependancies for the java program
    # Might need to change the "\" to "/" if running on linux
    java_tuples = working_directory + "\javatuples-1.2.jar"
    java_json = working_directory + "\json-simple-1.1.jar"
    # store the classpath as a var for easy re-use
    class_path = working_directory + ";" + java_tuples + ";" + java_json
    # Write the recieved json to a file
    # Need to use json.dumps becuase otherwise the json is saved with single quotes not double quotes
    r = requests.get(get_website).json()
    with open(file_name, 'w') as file:
        file.write(json.dumps(r))
    file.close()

    # TO COMPILE: navigate to directory holding jar files, .java file, and topology we want routing tables for
    # run: javac -classpath ".\pwd;.\javatuples-1.2.jar;.\json-simple-1.1.jar" shortestPath.java
    # But with the complete path to the pwd
    subprocess.run(["javac", "-classpath", class_path, "shortestPath.java"])
    subprocess.run(['java', '-classpath', class_path, 'shortestPath', file_name])
    # Open the created file 'routingTable.json', read it into a json object, send it to the network, then
    # print the response from the server
    with open("routingTable.json", 'r') as send_file:
        json_to_send = json.dumps(json.load(send_file))
        site_results = requests.post(post_website, data=json_to_send)
        print(site_results.json())
