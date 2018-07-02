Creos Scenario - SLE USe case
-----------------------------

Smart grid is an adaptive systems: topology and max allowed consumption are dynamically adapted according to consumption.
Consumption data are sent by smart meters every 15 min.
However, as shown by Hartmann et al., there is from 8 to 26% in errors.
Based on this consumption, consumption profile are computed per user.
This profile is mainly used to detect any abnormal consumption that could result from smart meters hacking, ressource theft, or other problems on the network.


add ref. about inaccuracy of sm



### Experimentation protocol

**Goal:**

To show that by considering uncertainty, we can avoid some miss interpretation.

**Made simulation:**

- 10 Customers
- 1 cable
- Compute cable overload
- Detection of overload

**Assumption**

- justify why gaussian
    - hidden assumption: most of the meters are accurate (gaussian also)
- power concumption follow a Gaussian distribution
- one guassian per hour
- gaussian folow a domain knowledge that says:
    - consumption pick around 7pm
    - consumption pick around 8am
    - small pick around noon
- Measured values are closed to the real one: accuracy of meters is 0.1%[1]

**Protocol**
- Generation of 10*24 random consumptions following gaussian distribution (cf. Assumption)
- Add random errors (~16.7%)
    - adds a random number to the generated one: x = randomGaussian() + (randomDouble() > 0.167)? 0 : (randomDouble()*100)

WITHOUT our Approach:
- compute cable load
- detect an overload: cable load > T

WITH our approach:
- evaluate uncertainty
    - computed a Gaussian centered on the mesured value (mean = received value) and small variance
    - used Bayes'theorem, combine this gaussian with domain knowledge
- if more than 75% of data have a confidence > 80%
    - compute cable overload, propagate uncertainty
    - detect an overload: cable load > T
- else
    - rise an alert: something might be wrong



[1]: http://www2.schneider-electric.com/documents/support/white-papers/998-4531_electric-utilities_High-accuracy_EN.pdf