import os
from sklearn import tree
from sklearn.metrics import confusion_matrix
from PIL import Image

def train_tree(trainX, trainY):
    clf = tree.DecisionTreeClassifier(criterion = 'entropy', max_depth = 3)
    return clf.fit(trainX, trainY)

def predict(clf, testX):
    return clf.predict(testX)

def evaluate(yHat, testY):
 return confusion_matrix(yHat, testY)