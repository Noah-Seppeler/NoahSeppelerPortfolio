"""
Name: Noah Seppeler
Student ID: #005614340

"""

from Package import Package, Truck
import Hash


def runSimulation(tts: str):
    print("Simulation run")

    # load truck 1
    truck1Packages = [deliveriesTable[1][0], deliveriesTable[3][1],
                       deliveriesTable[4][1], deliveriesTable[5][1],
                       deliveriesTable[6][1], deliveriesTable[0][1],
                       deliveriesTable[3][2], deliveriesTable[4][2],
                       deliveriesTable[6][2], deliveriesTable[7][2],
                       deliveriesTable[9][2], deliveriesTable[0][2],
                       deliveriesTable[1][3], deliveriesTable[4][3],
                       deliveriesTable[7][3], deliveriesTable[0][3]]

    truck1 = Truck(1, 0, 8, truck1Packages)

    minDis = 999.9
    nextLocation = ""
    # go through all the algorithm and don't stop until all packages are delivered
    while len(truck1.packages) > 0:

        # if the time is above the previous set time to stop the simulation then stop the simulation
        if truck1.time > float(tts):
            break

        # go through all distances
        for i, d in enumerate(distanceTable[places[truck1.currentlocation]]):
            if minDis > d and ivplaces[i] in truck1.addresses:
                # find the location that is the least distance away
                # keep track of the distance and the location
                minDis = d
                nextLocation = ivplaces[i]

        # go to the location that is the least distance away
        truck1.goto(nextLocation, minDis)

        minDis = 999.9
        nextLocation = ""

    # load truck 2

    truck2Packages = [deliveriesTable[3][0], deliveriesTable[6][0],
                       deliveriesTable[8][1],
                       deliveriesTable[5][2],
                       deliveriesTable[8][2], deliveriesTable[2][3],
                       deliveriesTable[3][3], deliveriesTable[5][3],
                       deliveriesTable[6][3], deliveriesTable[8][3],
                       deliveriesTable[9][3]]

    truck2 = Truck(2, 0, 9.1, truck2Packages)


    minDis = 999.9
    nextLocation = ""
    # go through algorithm to check when truck 2 will be done
    while len(truck2.packages) > 0:

        if truck2.time > float(tts):
            break

        for i, d in enumerate(distanceTable[places[truck2.currentlocation]]):
            if minDis > d and ivplaces[i] in truck2.addresses:
                minDis = d
                nextLocation = ivplaces[i]

        truck2.goto(nextLocation, minDis)

        minDis = 999.9
        nextLocation = ""

    truck3 = Truck(3, 0, max(min(float(truck1.time), float(truck2.time)), 10.5), [])

    for op in deliveriesTable:
        for tp in op:
            if tp.delivered == False:
                truck3.addpackage(tp)

    truck3.generateAddresses()

    minDis = 999.9
    nextLocation = ""
    # go through algorithm to check when truck 3 will be done
    while len(truck3.packages) > 0:

        if truck3.time > float(tts):
            break

        for i, d in enumerate(distanceTable[places[truck3.currentlocation]]):
            if minDis > d and ivplaces[i] in truck3.addresses:
                minDis = d
                nextLocation = ivplaces[i]

        truck3.goto(nextLocation, minDis)

        minDis = 999.9
        nextLocation = ""

    if truck1.startTime != truck1.time:
        truck1.changePackageStatus("En Route")

    if truck2.startTime != truck2.time:
        truck2.changePackageStatus("En Route")

    if truck3.startTime != truck3.time:
        truck3.changePackageStatus("En Route")

    # print out all deliveries and statuses of said deliveries
    print(float(tts))
    for op in deliveriesTable:
        for tp in op:
            print(str(tp))

    print(truck1.time, truck2.time, truck3.time, truck1.milestraveled, truck2.milestraveled, truck3.milestraveled)


# read in the data for the distances and put it into a table
distanceTable = [[0 for x in range(27)] for y in range(27)]

f = open("distances.txt", "r")

inputs = f.read().split("\n")

for i, rows in enumerate(inputs):
    d = rows.split()
    for j, dis in enumerate(d):
        distanceTable[i][j] = float(dis)

# read in the data for the places and put it into a dictionary
places = {}
ivplaces = {}

f = open("places.txt", "r")

inputs = f.read().split("\n")

for i, p in enumerate(inputs):
    places[p] = int(i)
    ivplaces[int(i)] = p

# make a table of tables that cover all of the hashes for the hash map
deliveriesTable = [[], [], [], [], [], [], [], [], [], []]

# read in the deliveries file
f = open("deliveries.txt", "r")

# split entries up into more readable formats, and put them into the hash map
inputs = f.read().split("\n")

for pac in inputs:
    x = pac.split("\t")
    for number in range(len(pac)):
        p = Package(int(x[0]), x[1], x[5], x[2], x[4], x[6], "Hub")
        Hash.HashInsert(deliveriesTable, p)
        number += 8

# package count used for new package ids
packageCount = 40

# menu for what the user wants to do
while True:
    s = input("What would you like to do \n"
              "1. Insert Package\n"
              "2. Search Package\n"
              "3. Run Simulation\n"
              "4. Exit\n")
    if s == "1":
        # collect new package information
        packageAddress = input("Please enter package Address: \n")
        packageCity = input("Please enter package City: \n")
        packageZip = input("Please enter package Zip: \n")
        packageDeadline = input("Please enter package Deadline: \n")
        packageWeight = input("Please enter package Weight: \n")
        # make new package
        package = Package(packageCount + 1, packageAddress, packageDeadline,
                          packageCity, packageZip, packageWeight, "Hub")

        # insert package into hash table
        Hash.HashInsert(deliveriesTable, package)
        packageCount += 1
    elif s == "2":
        packageId = input("Please enter package id: \n")
        print(Hash.HashSearch(deliveriesTable, Package(int(packageId), "", "", "", "", "", "")))
    elif s == "3":
        timeToStop = input("Please enter time to stop simulation:\n")
        runSimulation(timeToStop)
    elif s == "4":
        break
