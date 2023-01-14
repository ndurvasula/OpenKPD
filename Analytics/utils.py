import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
from sklearn.ensemble import RandomForestRegressor, RandomForestClassifier
from sklearn.model_selection import RandomizedSearchCV
import matplotlib.pyplot as plt


def pd_to_col(ser):
    return np.array([ser.to_numpy()]).T

def data_to_input(df):
    X = df.copy()
    Y = df.copy()
    Y = Y[["OUTCOME","ITERATIONS","LKDPI"]]
    Y["OUTCOME"] = Y["OUTCOME"].replace("MATCHED", 1)
    Y["OUTCOME"] = Y["OUTCOME"].replace("EXPIRED", 0)
    
    bt = ["REC_A", "REC_B", "DON_A", "DON_B"]
    
    X["REC_A"] = 1*((X["ABO_CAND"] == "A") | (X["ABO_CAND"] == "AB"))
    X["REC_B"] = 1*((X["ABO_CAND"] == "B") | (X["ABO_CAND"] == "AB"))
    
    X["DON_A"] = 1*((X["ABO_DON"] == "A") | (X["ABO_DON"] == "O"))
    X["DON_B"] = 1*((X["ABO_DON"] == "B") | (X["ABO_DON"] == "O"))
    
    pairs = pd.read_csv('pairs.csv')
    
    HLA = ["CA1","CA2","CB1","CB2","CDR1","CDR2","DA1","DA2","DB1","DB2","DDR1","DDR2"]
    HLA = [[i] for i in HLA]
    HLA2 = [["CA1","CA2"],["CB1","CB2"],["CDR1","CDR2"],["DA1","DA2"],["DB1","DB2"],["DDR1","DDR2"]]
    HLA3 = [["CA1","CA2","CB1","CB2","CDR1","CDR2"],["DA1","DA2","DB1","DB2","DDR1","DDR2"]]
    HLAs = HLA + HLA2 + HLA3

    hlan = [i[0]+ "f" for i in HLA]
    hlan2 = ["CA", "CB", "CDR", "DA", "DB", "DDR"]
    hlan3 = ["CHLA", "DHLA"]
    hlans = [hlan + hlan2 + hlan3][0]
    for i in range(len(HLAs)):
        counter = pairs.groupby(HLAs[i]).size().reset_index(name=hlans[i])
        X = X.merge(right=counter,how='left')
    
    
    continuous = ["AGE_AT_ADD_CAND", "CPRA_AT_MATCH_RUN","WEIGHT_CAND","WEIGHT_DON","NUM_PAIRS"]
    
    Xm = X.loc[X["OUTCOME"] == "MATCHED"][bt + hlans + continuous]
    Xo = X[bt + hlans + continuous]
    
    return Xo, Y["OUTCOME"], Xm, Y.loc[Y["OUTCOME"] == 1]["ITERATIONS"], Y.loc[Y["OUTCOME"] == 1]["LKDPI"]

def data_to_outcomes(df):
    Xo, Yo, Xm, Yi, Yl = data_to_input(df)
    
    try:
        Yo = Yo[(Yo[:] != 'TIMEOUT')]
    except:
        pass
    
    return Xo.head(1), Yo

def data_to_iterations(df):
    Xo, Yo, Xm, Yi, Yl = data_to_input(df)
    
    return Xm.head(1), Yi

def data_to_quality(df):
    Xo, Yo, Xm, Yi, Yl = data_to_input(df)
    
    return Xm.head(1), Yl

def data_to_features(df):
    X = df.copy()
    
    bt = ["REC_A", "REC_B", "DON_A", "DON_B"]
    
    X["REC_A"] = 1*((X["ABO_CAND"] == "A") | (X["ABO_CAND"] == "AB"))
    X["REC_B"] = 1*((X["ABO_CAND"] == "B") | (X["ABO_CAND"] == "AB"))
    
    X["DON_A"] = 1*((X["ABO_DON"] == "A") | (X["ABO_DON"] == "O"))
    X["DON_B"] = 1*((X["ABO_DON"] == "B") | (X["ABO_DON"] == "O"))
    
    pairs = pd.read_csv('pairs.csv')
    
    HLA = ["CA1","CA2","CB1","CB2","CDR1","CDR2","DA1","DA2","DB1","DB2","DDR1","DDR2"]
    HLA = [[i] for i in HLA]
    HLA2 = [["CA1","CA2"],["CB1","CB2"],["CDR1","CDR2"],["DA1","DA2"],["DB1","DB2"],["DDR1","DDR2"]]
    HLA3 = [["CA1","CA2","CB1","CB2","CDR1","CDR2"],["DA1","DA2","DB1","DB2","DDR1","DDR2"]]
    HLAs = HLA + HLA2 + HLA3

    hlan = [i[0]+ "f" for i in HLA]
    hlan2 = ["CA", "CB", "CDR", "DA", "DB", "DDR"]
    hlan3 = ["CHLA", "DHLA"]
    hlans = [hlan + hlan2 + hlan3][0]
    for i in range(len(HLAs)):
        counter = pairs.groupby(HLAs[i]).size().reset_index(name=hlans[i])
        X = X.merge(right=counter,how='left')
    
    
    continuous = ["AGE_AT_ADD_CAND", "CPRA_AT_MATCH_RUN","WEIGHT_CAND","WEIGHT_DON"]
    
    Xo = X[bt + hlans + continuous]
    
    return Xo
def iou(pl, ph, Y, p):
    tl = np.percentile(Y, (100-p)/2)
    th = np.percentile(Y, 100-(100-p)/2)
    
    ll = min(pl, tl)
    lh = max(pl, tl)
    
    hl = min(ph, th)
    hh = max(ph, th)
    
    if lh > hl:
        return 0
    return (hl - lh)/(hh - ll)