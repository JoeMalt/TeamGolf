from sklearn import tree
from sklearn.metrics import confusion_matrix
import pandas as pd
import os
import graphviz


def train_tree():
    data_file = load_data("C://Users/kocag/cam/Year2/group_project/TeamGolf/MixedRealityPDF/Data/trainingData.csv")

    clf = tree.DecisionTreeClassifier(criterion = 'gini', max_depth = 5)
    trainX = data_file.loc[:, data_file.columns != 'key']
    trainY = data_file.key
    return clf.fit(trainX, trainY)


def load_data(file_path):
    data_file = None
    if os.path.exists(file_path):
        data_file = pd.read_csv(file_path)
    return data_file


def predict(clf, testX):
    return clf.predict(testX)


def evaluate(yHat, testY):
    return confusion_matrix(yHat, testY)


if __name__ == '__main__':
    clf = train_tree()
    testX = load_data("C://Users/kocag/cam/Year2/group_project/TeamGolf/MixedRealityPDF/Data/testingData.csv")
    pred = predict(clf, testX)
    print(pred)