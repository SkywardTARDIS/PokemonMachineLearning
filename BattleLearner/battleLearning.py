import math
import numpy

def removeIrrelevant():
    #removes all columns for which the value is 0 for every single battle, writes to new file
    data = []
    file = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/showdownBattles.txt", 'r')
    line = file.readline()
    data.append([x for x in line.split(",")])
    for row in file:
        data.append([int(float(x)) for x in row.split(",")])
    data = numpy.array(data)
    attributes = numpy.array(data[0,1:])
    labels = numpy.array(data[1:,0])
    data = numpy.delete(data, 0 , 1)
    data = numpy.delete(data, 0, 0)
    numColumns = len(attributes)
    i = 0
    while i < numColumns:
        removeRow = True
        for j in range(len(labels) - 2):
            if data[j][i] != data [j+1][i]:
                removeRow = False
        if(removeRow):
            #print(attributes[i])
            data  = numpy.delete(data, i, 1)
            attributes = numpy.delete(attributes, i)
            numColumns = numColumns - 1
        else:
            i = i + 1
    data = numpy.concatenate((labels, data), 0)
    data = numpy.concatenate((attributes, data), 1)
    #writeFile = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/simplifiedBattles.txt", 'w')
    numpy.savetxt("./ShowdownScraper/src/main/java/cisc181/labs/finalData/simplifiedBattles.txt", data, delimiter=',')


def main():
    removeIrrelevant()

if __name__ == "__main__":
    main()