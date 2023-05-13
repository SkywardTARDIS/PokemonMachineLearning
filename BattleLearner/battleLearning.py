import math
import numpy
from sklearn import tree

#Function scrapped and rewritten in Java, because it was significantly faster
'''
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
'''



def main():
    #removeIrrelevant()
    data = []
    testDate = []
    attributes = []
    file = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/trainingData.txt", 'r')
    line = file.readline()
    for x in line.split(","):
     attributes.append(x.removesuffix("\n"))
    for row in file:
        data.append([int(float(x)) for x in row.split(",")])
    data = numpy.array(data)
    attributes = numpy.array(attributes)
    #removing labels from the data matrix
    labels = numpy.array(data[:,0])
    data = numpy.delete(data, 0, 1)
    clf = tree.DecisionTreeClassifier()
    clf = clf.fit(data, labels)
    print("Done")
    tree.plot_tree(clf)

    file = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/validationData.txt", 'r')
    line = file.readline()
    for row in file:
        testData.append([int(float(x)) for x in row.split(",")])
    testData = numpy.array(testData)
    testLables = numpy.array(testData[:,0])
    testData = numpy.delete(testData, 0, 1)
    predictions = clf.predict(testData)
    print(predictions)
    print(testLables)

    counts = 0
    for i in range(len(predictions)):
       if predictions[i] == testLables[i]:
          counts = counts + 1
    print(counts)
    

if __name__ == "__main__":
    main()