class Package:
    def __init__(self, packageid):
        self.packageid = packageid

    def __init__(self, packageid, address, deadline, city, zipcode, weight, status):
        self.packageid = packageid
        self.address = address
        self.deadline = deadline
        self.city = city
        self.zipcode = zipcode
        self.weight = weight
        self.status = status
        self.deliveryTime = ""
        self.delivered = False
        self.time = 0.0

    def __str__(self):
        return str(self.packageid) + " " + self.address + ", " + self.city \
               + ", " + self.zipcode + ". " + self.deadline + " " + str(self.weight) + \
               " " + self.status + " " + str(self.deliveryTime)

    def __eq__(self, other):
        if isinstance(other, Package):
            return self.packageid == other.packageid


class Truck:

    def __init__(self, truckid: int, milestraveled: int, time: str, packageList):
        self.truckid = truckid
        self.milestraveled = milestraveled
        self.mph = 18
        self.currentlocation = "HUB"
        self.packages = packageList
        self.packagesArc = packageList
        self.addresses = []
        self.time = time
        self.startTime = time

        self.generateAddresses()

    def goto(self, location: str, miles: int):
        self.milestraveled += miles
        self.currentlocation = location
        self.addresses.remove(location)
        self.time = self.time + (miles / self.mph)
        for p in self.packages:
            if p.address == location:
                p.status = "Delivered"
                p.delivered = True
                p.deliveryTime = self.time
                self.packages.remove(p)


    def addpackage(self, package: Package):
        self.packages.append(package)
        self.packagesArc.append(package)

    def generateAddresses(self):
        for a in self.packages:
            self.addresses.append(a.address)

    def changePackageStatus(self, status):
        for p in self.packagesArc:
            p.status = status
