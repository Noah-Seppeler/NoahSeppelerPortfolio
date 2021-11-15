from Package import Package

def HashInsert(table: list, item: Package):
    # search the table and make sure the item isn't already in there
    if (HashSearch(table, item) == None):
        # make a key for the item
        key = HashItem(item)
        # append item into table counting for overlap on the keys
        table[key].append(item)

def HashSearch(table: list, item: Package):
    # check to see if the item is found at the keys location
    if(item in table[HashItem(item)]):
        # if it is found go through the list and return the matching item
        for i in table[HashItem(item)]:
            if(i == item):
                return i
    else:
        return None

# using a simple hash function to hash the data and return the result
def HashItem(item: Package):
    return item.packageid % 10;

