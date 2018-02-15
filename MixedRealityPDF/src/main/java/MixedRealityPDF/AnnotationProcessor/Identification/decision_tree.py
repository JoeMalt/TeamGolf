from sklearn import tree
from sklearn.metrics import confusion_matrix

def train_tree(trainX, trainY):
    clf = tree.DecisionTreeClassifier(criterion = 'entropy', max_depth = 3)
    return clf.fit(trainX, trainY)

def predict(testX):
    return clf.predict(testX)

def evaluate(yHat, testY):
    confusion_matrix(yHat, testY)