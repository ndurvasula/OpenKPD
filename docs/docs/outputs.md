---
layout: default
title: Outputs
nav_order: 4
---

# Outputs
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

Below we outline the different patient features that are tracked by OpenKPD, and how they will be reported in output CSV files.

## State variables

The following logged variables pertain to the state of the pool at the time of exit. 

| State Variable | Description |
| :---: | :--- |
| `ID` | the patient-donor pair's numerical ID in the pool |
| `MR_NUM`| the age of the pool when the patient outcome was logged. If running a sample simulation, this is the number of iterations after starting the sample simulation -- not the number of iterations since the oriignal batch simulation |
|`NUM_PAIRS`| the number of patient-donor pairs in the pool at the time of entry |
| `NUM_ALTS`| the number of altruists in the pool at the time of entry |

## Patient outcomes

The following logged variables are key patient outcomes corresponding to the manner in which the patient exited the pool

| Outcome Variable | Description |
| :---: | :--- |
| `OUTCOME`| returns whether the patient was `MATCHED` or if the patient `EXPIRED` prior to a match outcome. In sample simulation, there is a further outcome `TIMEOUT` that occurs when the number of elapsed iterations surpasses the `TIMEOUT` variable set in the [configuration file](https://openkpd.org/docs/simulators#trajectory-simulation). |
| `ITERATIONS`| the number of iterations that the patient spent in the pool prior to exit |
| `LKDPI` | this variable measures the quality of a match outcome, and is computed using the formula for the [Living Kidney Donor Profile Index](https://pubmed.ncbi.nlm.nih.gov/26752290/). If there is no match outcome, this field is `null`. |

## Biological features

The following logged variables describe biological features pertaining to the patient, paired donor, and matched donor (should one exist). All features corresponding to the matched donor are `null` if the patient exits the pool without being matched.

### Categorical and Boolean features

| Categorical Variable | Description |
| :---: | :--- |
| `ABO_CAND/DON/MATCH` | blood type of patient/paired donor/matched donor |
| `AFRICAN_AMERICAN_DON/MATCH` | true iff paired donor/matched donor is African American |
| `HCU_DON/MATCH` | paired donor/matched donor history of cigarette use |
| `SEX_CAND/DON/MATCH` | sex of candidate/paired donor/matched donor -- a value of true denotes male |

#### Human Leukocyte Antigen (HLA) typing

These features correspond to the antigen types present within the patient. HLA mismatches increase the likelihood of graft rejection.

| Antigen Variable | Description |
| :---: | :--- |
| `C/D/MA1` and `C/D/MA2` | HLA-A antigens for patient/paired donor/matched donor |
| `C/D/MB1` and `C/D/MB2` | HLA-B antigenns for patient/paired donor/matched donor|
| `C/D/MDR1` and `C/D/MDR2` | HLA-DR antigenns for patient/paired donor/matched donor |

### Numerical features

| Numerical Variable | Description |
| :---: | :--- |
| `AGE_AT_ADD_CAND/DON/MATCH` | age of patient/paired donor/matched donor when added to pool |
| `BMI_DON/MATCH` | Body Mass Index of paired donor/matched donor |
| `BP_SYSTOLIC_DON/MATCH` | systolic blood pressure of paired donor/matched donor|
| `EGFR_DON/MATCH` | estimated glomerular filtration rate for paired donor/matched donor |
| `CPRA_AT_MATCH_RUN` | patient sensitivity (also known as Calculated Panel of Reactive Antibodies) -- ranges from 0 to 100 with 100 being most sensitive (least receptive to donor graft) |
| `WEIGHT_CAND/DON/MATCH`| weight of the patient/paired donor/matched donor in pounds|


