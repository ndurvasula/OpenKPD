---
layout: default
title: Installation
nav_order: 2
---

# Installation
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## Getting started

OpenKPD requires only two dependencies to run: Java and CPLEX. Any 64-bit installation of [Java](https://www.java.com/en/download/manual.jsp) (JRE or JDK) will work with OpenKPD. OpenKPD also requires CPLEX Optimization Studio 12.6.3. CPLEX can be downloaded for free through the [IBM Academic Initiative](https://www.ibm.com/academic/home). Below we show how to do this.

### Installing CPLEX 12.6.3

Use the part number `CRW5CML` to install CPLEX 12.6.3 through the academic initiative program.

## Patient data files
OpenKPD requires two data files to function: `pairs.csv` and `alts.csv`. These files contain lists of synthetic patient data that was generated using real UNOS patient data. To protect patient privacy, we request that you email 
- [Naveen](https://ndurvasula.com) (ndurvasula [at] berkeley [dot] edu) and/or 
- [John](http://jpdickerson.com) (johnd [at] umd [dot] edu) and/or 
- [Aravind](http://www.cs.umd.edu/~srin/) (asriniv1 [at] umd [dot] edu) 

for these files


## Linking OpenKPD to CPLEX

In the same folder as `OpenKPD.jar`, locate the file `config.txt`. Besides facilitating customizations to the matching policy, this file is used to link CPLEX to OpenKPD. This can be done by modifying the key variable `CPLEX_PATH`. The variable should point to the directory of the CPLEX executable. For OSX users, this directory will look like `/Applications/CPLEX_Studio/cplex/bin/x86_64_OSX/`.