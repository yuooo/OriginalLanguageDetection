# -*- coding: utf-8 -*-
"""
Created on Sun May  8 12:19:08 2016

@author: jessicahoffmann
"""

import numpy as np
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import  confusion_matrix
import pandas as pd
import os
from sklearn.cross_validation import cross_val_score
from xgboost import XGBClassifier
import os
from sklearn.multiclass import OneVsRestClassifier

os.chdir("/Users/jessicahoffmann/IdeaProjects/OriginalLanguageDetection")

#%% ===========================================================================
#   === Loading Data ===
#   ===========================================================================
print "Load Data"
train = pd.read_csv("Data/csv/400/allFeat.csv")
test = pd.read_csv("Data/csv/400/AllFeat_test.csv")

y_train = train["@@class@@"]
x_train = train.drop(["@@class@@"],axis=1)

y_test = test["@@class@@"]
x_test = test.drop(["@@class@@"],axis=1)

class_label = ['American', 'French', 'German', 'Russian', 'Spanish']

print "Loaded Data"



#%% ===========================================================================
#   === helpers ===
#   ===========================================================================

def cross_score(name, model, X, target):
    scores = cross_val_score(model, X, target, cv=5, scoring='roc_auc')
    print name, "--- mean:", np.mean(scores), "sd:", np.std(scores)
    return model, scores
    
def preview(model, x, y, x_t, y_t):
    model.fit(x, y)
    print "Train score:", model.score(x, y)
    print "Test score:", model.score(x_t, y_t)
    y_hat = model.predict(x_t)
    print confusion_matrix(y_t, y_hat, labels=class_label)
    
def print_top(n, clf, class_labels, words):
    """Prints features with the highest coefficient values, per class"""
    for i, class_label in enumerate(class_labels):
        top10 = np.argsort(clf.coef_[i])[::-1][:n]
        print("%s: %s\n" % (class_label,
              " ".join(words[j] for j in top10)))
    


#%% ===========================================================================
#   === Feature Transform ===
#   ===========================================================================
x = np.asarray(x_train)
y = np.asarray(y_train)

x_t = np.asarray(x_test)
y_t = np.asarray(y_test)

l_train = list(x_train.columns.values)
l_test = list(x_test.columns.values)

ind_12 = [i for i,a in enumerate(l_train) if a in l_test]
ind_21 = [i for i,a in enumerate(l_test) if a in l_train]

words = [l_train[i] for i in ind_12]

x = x[:,ind_12]
x_t = x_t[:, ind_21]

##%% ===========================================================================
##   === Feature Extraction ===
##   ===========================================================================
#x = np.asarray(x_train)
#y = np.asarray(y_train)
#
#x_t = np.asarray(x_test)
#y_t = np.asarray(y_test)
#
#l_train = list(x_train.columns.values)
#l_test = list(x_test.columns.values)
#
#ind_12 = [i for i,a in enumerate(l_train) if a in l_test]
#ind_21 = [i for i,a in enumerate(l_test) if a in l_train]
#
#words = [l_train[i] for i in ind_12]
#
#x = x[:,ind_12]
#x_t = x_t[:, ind_21]


#%% ===========================================================================
#   === Train Model ===
#   ===========================================================================

lr = OneVsRestClassifier(LogisticRegression())
print "lr"
preview(lr, x, y, x_t, y_t)
print_top(10, lr, class_label, words)
print

#%%
rf = RandomForestClassifier()
print "rf"
preview(rf, x, y, x_t, y_t)
print

#%%
xgb = OneVsRestClassifier(XGBClassifier(max_depth=6, n_estimators=50, colsample_bytree =0.3))

print "xgb"
preview(xgb, x, y, x_t, y_t)
print

#%%
svm = OneVsRestClassifier(SVC())

print "svm"
preview(xgb, x, y, x_t, y_t)
print 