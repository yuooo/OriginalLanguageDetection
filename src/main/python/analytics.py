# -*- coding: utf-8 -*-
"""
Created on Sun May  8 12:19:08 2016

@author: jessicahoffmann
"""

import numpy as np
from sklearn.linear_model import LogisticRegression
import time
from sklearn.svm import SVC
from sklearn.preprocessing import OneHotEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import roc_auc_score, confusion_matrix
import pandas as pd
import os
from sklearn.feature_selection import chi2, f_classif, RFECV
import pandas as pd
from sklearn.cross_validation import cross_val_score
from sklearn.grid_search import GridSearchCV
from sklearn.preprocessing import StandardScaler
from xgboost import XGBClassifier
import os
from sklearn.multiclass import OneVsRestClassifier

os.chdir("/Users/jessicahoffmann/IdeaProjects/OriginalLanguageDetection")

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
    print confusion_matrix(y_t, y_hat, labels=['American','French', 'Russian'])
    
def print_top(n, clf, class_labels, words):
    """Prints features with the highest coefficient values, per class"""
    for i, class_label in enumerate(class_labels):
        top10 = np.argsort(clf.coef_[i])[::-1][:n]
        print("%s: %s" % (class_label,
              " ".join(words[j] for j in top10)))
    

#%% ===========================================================================
#   === Loading Data ===
#   ===========================================================================
print "Load Data"
train = pd.read_csv("Data/csv/500/unigram.csv")
test = pd.read_csv("Data/csv/500/unigram_test.csv")

y_train = train["@@class@@"]
x_train = train.drop(["@@class@@"],axis=1)

y_test = test["@@class@@"]
x_test = test.drop(["@@class@@"],axis=1)

class_label = ['American','French', 'Russian']

print "Loaded Data"


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

#%% ===========================================================================
#   === Train Model ===
#   ===========================================================================

lr = OneVsRestClassifier(LogisticRegression())
preview(lr, x, y, x_t, y_t)
print_top(30, lr, class_label, words)

#%%
rf = RandomForestClassifier()

preview(rf, x, y, x_t, y_t)

#%%
xgb = OneVsRestClassifier(XGBClassifier())

preview(xgb, x, y, x_t, y_t)