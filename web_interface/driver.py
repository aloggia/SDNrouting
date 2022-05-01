import requests
import sys
import subprocess

if __name__ == '__main__':
    # website_endpoint = str(sys.argv[1])
    website_endpoint = "topology1"
    file_name = "./" + website_endpoint + ".json"
    website = "http://www.goatgoose.com:2222/get_topology/" + website_endpoint
    r = requests.get(website)

    # TODO: Compile entire java program, this needs to include somehow compiling jar files along with java files
    #json_object = subprocess.check_output(['java', 'shortestPath', file_name])
    with open(file_name, 'w') as file:
        file.write(str(r.json()))
    file.close()

    with open(file_name, 'r') as file:
        print(file.read())

    file.close()

    # TODO: Need a way to get the created json from the java program and send it to the network
    # output = subprocess.check_output("java shortestPath " + file_name)
    #send_address = "http://www.goatgoose.com:2222/set_tables/" + website_endpoint
   # requests.post(website, json=json_object)
