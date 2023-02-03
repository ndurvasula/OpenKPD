OpenKPD
==============

OpenKPD is an open-source platform for state-of-the-art kidney exchange simulation.

## What is kidney exchange?

Kidney failure is a life-threatening health issue that affects hundreds of thousands of people worldwide. In the US alone, the waitlist for a kidney transplant has over 100,000 patients. This list is growing: demand far outstrips supply.

A recent innovation, kidney exchange, allows patients to bring an (incompatible) donor to a large pool where they can swap donors with other patients. As of 2012–2013, roughly 10% of US kidney transplants occurred through a variety of kidney exchanges. Outside of the US, many countries (the UK, the Netherlands, Portugal, Israel, ...) are fielding exchanges.

## What does this package provide?

Detailed data pertaining to realistic kidney exchange networks can give crucial insight into how to improve these systems to be more efficient and equitable. However, much of this data is inaccessible to researchers due to its fundamentally sensitive nature. This package provides a means for generating realistic synthetic data so that researchers can have open access to key data without infringing upon patient privacy. 

This framework comes packaged with two simulators: the [batch simulator]() and the [trajectory simulator](). The former allows users to generate simulated running match records for a given amount of time. The latter takes as input the given state of a kioney exchange pool, and will simulate a particular patient for some number of trajectories until they exit the pool. See [Installation](https://openkpd.org/docs/installation/) to get started.


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

OpenKPD is distributed by a [GNU General Public License v2.0](https://github.com/ndurvasula/OpenKPD/blob/master/LICENSE).

### Contributing

Contributions are very welcome! Feel free to raise an issue through GitHub, or [get in touch](https://ndurvasula.com) if you'd like to contribute, or if you have any questions.


