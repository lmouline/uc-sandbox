# Belief function scenario

A way to define the uncertainty of a value is to define a value and a belief function to represent the uncertainty: <value: v, beliefFunc: bf>.
Using current solutions, a designer has to manually represents both values.

## Without UC Abstraction solution

```
struct Sensor {
    att value: double
    att ucFunction: BeliefFunction
}
```

When an engineer wants to use this value, he has to manually consider this confidence:

```
function processing(s: Sensor, T: double, s2: Sensor) {
    // compared to a threshold
    // value > T
    if(s.ucFunction(s.value) > 0.8 && s.value > T) {
        ...
    }


    // value < T
    if(s.ucFunction(s.value) > 0.8 && s.value < T) {
        ...
    }

    // value == T
    if(s.ucFunction(s.value) > 0.8 && s.value == T) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s.ucFunction(s.value) > 0.8 && s2.ucFunction(s.value) > 0.8 && s.value > s2.value) {
        ...
    }

    //s < s2
    if (s.ucFunction(s.value) > 0.8 && s2.ucFunction(s.value) > 0.8 && s.value < s2.value) {
       ...
    }

    // s == s2
    // we consider that 2 values are equal if it exists an overlap between two ranges
    if(s.ucFunction(s.value) > 0.8 && s2.ucFunction(s.value) > 0.8 && s.value == s2.value) {
        ...
    }
}
```

## With our solution

**Generic** approach:
```
struct Sensor {
    att value: UncertainGaussFunc<double>
}
```

**Extension of property definition**:
```
struct Sensor {
    @UCRepresentation(name = "GaussianFunction")
    uatt value: double
}
```

The previous code will then looks like:
```
function processing(s: Sensor, T: double, s2: Sensor){
    // compared to a threshold
    // value > T
    if(s.value.ucFunction(s.value) > 80 && s.value > T) {
        ...
    }

    // value < T
    if(s.value.ucFunction(s.value) > 80 &&  s.value > T) {
        ...
    }

    // value == T
    if(s.value.ucFunction(s.value) > 80 && s.value == T) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s.value.ucFunction(s.value) > 80 && s2.value.ucFunction(s.value) > 80 && s.value > s2.value) {
        ...
    }

    //s < s2
    if (s.value.ucFunction(s.value) > 80 && s2.value.ucFunction(s.value) > 80 &&  s.value < s2.value) {
       ...
    }

    // s == s2
    if(s.value.ucFunction(s.value) > 80 && s2.value.ucFunction(s.value) > 80 && s.value == s2.value) {
        ...
    }
}
```

If the engineer wants to access to the measured value or the function, it could do so:

```
function processing(s: Sensor) {
    if(s.value.value > T) {
        ...
    }

     if(s.value.ucFunction(X) > T) {
        ...
    }
}