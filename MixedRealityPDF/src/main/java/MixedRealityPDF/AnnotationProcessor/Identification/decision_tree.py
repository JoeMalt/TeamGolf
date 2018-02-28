import sys
sys.path.insert(0, '/home/aga/work/group_project/TeamGolf/MixedRealityPDF/venv/lib/python3.5/site-packages')
from sklearn import tree
from sklearn.metrics import confusion_matrix
import pandas as pd
import os
import graphviz
from pathlib import Path


# returns absolute path to MixedRealityPDF\
def get_project_dir():
    dirname = os.path.dirname
    relative_path = dirname(dirname(dirname(dirname(dirname(dirname(dirname(os.path.abspath(__file__))))))))
    return relative_path


def train_tree():
    relative_path = get_project_dir()
    file_path = os.path.join(relative_path, "Data", "trainingData.csv")
    data_file = load_data(file_path)

    clf = tree.DecisionTreeClassifier(criterion = 'gini', max_depth = 5)
    trainX = data_file.loc[:, data_file.columns != 'key']
    trainY = data_file.key
    return clf.fit(trainX, trainY)


# TODO(koc): finish writing this method to give us evaluation of effectiveness of tree
def test_tree():
    clf = train_tree()
    relative_path = get_project_dir()
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


def hello():
    relative_path = get_project_dir()
    file_path = os.path.join(str(relative_path), "Data", "test.txt")
    data_file = open(file_path, 'w')
    data_file.write("Hello")

clf = train_tree()
relative_path = get_project_dir()
file_path = os.path.join(relative_path, "Data", "testingData.csv")
testX = load_data(file_path)
pred = predict(clf, testX)
for p in pred:
    print(p)