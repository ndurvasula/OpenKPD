---
layout: default
title: Simulators
nav_order: 3
---

# Simulators
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---
## Configuration

OpenKPD has a number of parameters that can be set through a configuration file. In the download release, this file is `config.txt`, as shown below.

![config](config_img.png)

The following parameters are relevant for both the Batch Simulator and Trajectory Simulator. By default, all constants are set to corresponding to data collected from the UNOS exchange:

- `PATH`: sets the directory where OpenKPD output files will be written. 
- `DAYS_PER_MATCH`: sets the policy match frequency, or the number of days patients wait until another round of matching begins.
- `EXPIRY`: sets the probability of patient expiry per round of matching. 
- `PATIENCE`: sets the per-round probability that an altruistic donor runs out of patience and leaves the exchnage prior to being matched.
- `RENEGE`: sets the probability that a bridge donor reneges in a chain
- `EXPECTED_PAIRS`: sets the expected number of pairs that arrives in the exchange each round.
- `EXPECTED_ALTRUISTS`: sets the expected number of altruists that arrives in the exchange each round.
- `CHAIN_CAP`: sets the maximum length of any chain in the integer program used to find matches.
- `CYCLE_CAP`: sets the maximum length of any cycle in the integer program used to find matches.
- `CPLEX_PATH`: links CPLEX to OpenKPD. See [Installation](https://openkpd.org/docs/installation/) for more information.

By default, OpenKPD will run the [UNOS matching policy](https://optn.transplant.hrsa.gov/media/3239/20191011_kidney_kpd_priority_points.pdf), and patients will arrive according to a distribution that was inferred using real UNOS data. The policy can be customized using the techniques described in the [Policy Customization](https://openkpd.org/docs/customization/) section of the documentation. The parameters `CUSTOM_WEIGHTS_PATH` and `CUSTOM_WEIGHTS_CLASSNAME` are used to set a custom policy.  



## Batch Simulation

OpenKPD comes with two packaged simulators. The first of which is the _batch simulator_, which can be used to generate a simulated match record. By match record, we refer to a running list that contains the features of every patient that has entered the exchange, along with their match outcome (i.e. was the patient matched, and if so how long did it take and what was the organ quality). 

![batch](batch.png)






## Trajectory Simulation