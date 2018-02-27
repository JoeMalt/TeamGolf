from sklearn import tree
from sklearn.metrics import confusion_matrix
import pandas as pd
import os
import graphviz
from pathlib import Path


def train_tree():
    # returns absolute path to MixedRealityPDF\
    relative_path = Path().resolve().parents[5]
    file_path = os.path.join(str(relative_path), "Data", "trainingData.csv")
    data_file = load_data(file_path)

    clf = tree.DecisionTreeClassifier(criterion = 'gini', max_depth = 5)
    trainX = data_file.loc[:, data_file.columns != 'key']
    trainY = data_file.key
    return clf.fit(trainX, trainY)


# TODO(koc): finish writing this method to give us evaluation of effectiveness of tree
def test_tree():
    clf = train_tree()
    relative_path = Path().resolve().parents[5]
    file_path = os.path.join(str(relative_path), "Data", "testingData.csv")
    testX = load_data(file_path)
    pred = predict(clf, testX)


def load_data(file_path):
    data_file = None
    if os.path.exists(file_path):
        data_file = pd.read_csv(file_path)
    return data_file


def predict(clf, testX):
    return clf.predict(testX)


if __name__ == '__main__':
    clf = train_tree()
    relative_path = Path().resolve().parents[5]
    file_path = os.path.join(str(relative_path), "Data", "testingData.csv")
    testX = load_data(file_path)
    pred = predict(clf, testX)
    # test should output highlight x3, text x3 and underline x3
    print(pred)