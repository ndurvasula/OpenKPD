---
layout: default
title: Home
nav_order: 1
description: "OpenKPD is an open-source platform for state-of-the-art kidney exchange simulation."
permalink: /
---

# State-of-the-art kidney exchange simulation
{: .fs-9 }

OpenKPD provides a platform for generating realistic kidney exchange data under a customizable matching policy.  
{: .fs-6 .fw-300 }

[Download OpenKPD](/OpenKPD.zip){: .btn .btn-primary .fs-5 .mb-4 .mb-md-0 .mr-2 }
[View it on GitHub](https://github.com/ndurvasula/OpenKPD){: .btn .fs-5 .mb-4 .mb-md-0 }

---

## What is kidney exchange?

Kidney failure is a life-threatening health issue that affects hundreds of thousands of people worldwide. In the US alone, the waitlist for a kidney transplant has over 100,000 patients. This list is growing: demand far outstrips supply.

A recent innovation, kidney exchange, allows patients to bring an (incompatible) donor to a large pool where they can swap donors with other patients. As of 2012–2013, roughly 10% of US kidney transplants occurred through a variety of kidney exchanges. Outside of the US, many countries (the UK, the Netherlands, Portugal, Israel, ...) are fielding exchanges.

## What does this code contain?

This code 

## Getting started

OpenKPD requires only two dependencies to run: Java and CPLEX. Any 64-bit installation of [Java](https://www.java.com/en/download/manual.jsp) (JRE or JDK) will work with OpenKPD. OpenKPD also requires CPLEX Optimization Studio 12.6.3. CPLEX can be downloaded for free through the [IBM Academic Initiative](https://academic.ibm.com/a2mt/email-auth).


## About OpenKPD

OpenKPD was developed as a fork of [JohnDickerson/KidneyExchange](https://github.com/JohnDickerson/KidneyExchange) by [Naveen Durvasula](https://ndurvasula.com). If you use OpenKPD in your work, please cite us using the following citation:

```
@inproceedings{ijcai2022p701,
  title     = {Forecasting Patient Outcomes in Kidney Exchange},
  author    = {Durvasula, Naveen and Srinivasan, Aravind and Dickerson, John},
  booktitle = {Proceedings of the Thirty-First International Joint Conference on
               Artificial Intelligence, {IJCAI-22}},
  publisher = {International Joint Conferences on Artificial Intelligence Organization},
  editor    = {Lud De Raedt},
  pages     = {5052--5058},
  year      = {2022},
  month     = {7},
  note      = {AI for Good},
  doi       = {10.24963/ijcai.2022/701},
  url       = {https://doi.org/10.24963/ijcai.2022/701},
}
```

### License

OpenKPD is distributed by an [GNU General Public License v2.0](https://github.com/ndurvasula/OpenKPD/blob/master/LICENSE).

### Contributing

When contributing to this repository, please first discuss the change you wish to make via issue,
email, or any other method with the owners of this repository before making a change.


