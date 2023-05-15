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
    trees = []
    for i in range(7):
        data = []
        testData = []
        attributes = []
        file = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/baggedSets/bagData" + str(i+1) + ".txt", 'r')
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
        trees.append(tree.DecisionTreeClassifier())
        trees[i] = trees[i].fit(data, labels)
        print("Done " + str(i+1))
        #tree.plot_tree(clf)

    tp = []
    tn = []
    fp = []
    fn = []
    file = open("./ShowdownScraper/src/main/java/cisc181/labs/finalData/validationData.txt", 'r')
    line = file.readline()
    for row in file:
        testData.append([int(float(x)) for x in row.split(",")])
    testData = numpy.array(testData)
    testLables = numpy.array(testData[:,0])
    testData = numpy.delete(testData, 0, 1)
    predictions = []
    for i in range(8):
        tp.append(0)
        tn.append(0)
        fp.append(0)
        fn.append(0)
        if i < 7:
            predictions.append(trees[i].predict(testData))
    votes = []
    counts = 0
    for i in range(len(predictions[0])):
        counts = 0
        for j in range(len(predictions)):
            if predictions[j][i] == 1:
                if testLables[i] == 1:
                    tp[j] = tp[j] + 1
                else:
                    fp[j] = fp[j] + 1
            else:
                if testLables[i] == 1:
                    fn[j] = fn[j] + 1
                else:
                    tn[j] = tn[j] + 1
            counts = counts + predictions[j][i]
        if counts < 7/2:
            votes.append(0)
        else:
            votes.append(1)

    counts = 0
    for i in range(len(votes)):
        if votes[i] == testLables[i]:
            counts = counts + 1
        if votes[i] == 1:
            if testLables[i] == 1:
                tp[7] = tp[7] + 1
            else:
                fp[7] = fp[7] + 1
        else:
            if testLables[i] == 1:
                fn[7] = fn[7] + 1
            else:
                tn[7] = tn[7] + 1
    
    for i in range(len(tp)-1):
        recall = tp[i]/(tp[i] + fn[i])
        precision = tp[i]/(tp[i] + fp[i])
        print("For tree from bag " + str(i+1))
        print("Recall: " + str(precision))
        print("Precision: " + str(recall))
        print("Accuracy: " + str((tp[i] + tn[i]) / len(testLables)))
        print("F1: " + str(2 * (precision * recall) / (precision + recall)))
        print("\n")

    recall = tp[7]/(tp[7] + fn[7])
    precision = tp[7]/(tp[7] + fp[7])
    print("Ensemble success rates: ")
    print("Recall: " + str(precision))
    print("Precision: " + str(recall))
    print("Accuracy: " + str((tp[7] + tn[7]) / len(testLables)))
    print("F1: " + str(2 * (precision * recall) / (precision + recall)))

if __name__ == "__main__":
    main()