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
    attributes = numpy.array(data[0,:])
    labels = numpy.array(data[1:,0])
    numColumns = len(attributes)
    i = 1
    while i < numColumns:
        removeRow = True
        holderRow = numpy.array(data[:,i])
        for j in range(len(holderRow)-1):
            if int(holderRow[j+1]) > 0:
                removeRow = False
        #print(attributes[i])
        #print(removeRow)
        #print(holderRow)
        if(removeRow):
            print(attributes[i])
            data  = numpy.delete(data, i, 1)
            attributes = numpy.delete(attributes, i)
            numColumns = numColumns - 1
        else:
            i = i + 1

    writeFile = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/simplifiedBattles.txt", 'w')

    writeFile.write(str(data[0][0]))
    for j in range(len(data[0]) - 1):
        writeFile.write("," + str(data[0][j+1]))

    for i in range(len(data) - 1):
        writeFile.write("\n" + str(data[i+1][0]))
        for j in range(len(data[0]) - 1):
            writeFile.write("," + str(data[i+1][j+1]))


def main():
    #removeIrrelevant()
    print("placeholder line")

if __name__ == "__main__":
    main()