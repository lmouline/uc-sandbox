# Confidence scenario

A way to define the uncertainty of a value is to define a value and a confidence percentage: <value: v, percentage: p>.
Using current solutions, a designer has to manually represents both values.

## Without UC Abstraction solution

```
struct Sensor {
    att value: double
    att confidence: double
}
```

When an engineer wants to use this value, he has to manually consider this confidence:

```
function processing(s: Sensor, T: double, s2: Sensor) {
    // compared to a threshold
    // value > T
    if(s.confidence > 0.8 && s.value > T) {
        ...
    }


    // value < T
    if(s.confidence > 0.8 && s.value < T) {
        ...
    }

    // value == T
    if(s.confidence > 0.8 && s.value == T) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s.confidence > 0.8 && s2.confidence > 0.8 && s.value > s2.value) {
        ...
    }

    //s < s2
    if (s.confidence > 0.8 && s2.confidence > 0.8 && s.value < s2.value) {
       ...
    }

    // s == s2
    // we consider that 2 values are equal if it exists an overlap between two ranges
    if(s.confidence > 0.8 && s2.confidence > 0.8 && s.value == s2.value) {
        ...
    }
}
```

## With our solution

**Generic** approach:
```
struct Sensor {
    att value: Unconfident<double>
}
```

**Extension of property definition**:
```
struct Sensor {
    @UCRepresentation(name = "ConfidencePercentage")
    uatt value: double
}
```

The previous code will then looks like:
```
function processing(s: Sensor, T: double, s2: Sensor) {
    // compared to a threshold
    // value > T
    if(s.value.confidence > 80 && s.value > T) {
        ...
    }

    // value < T
    if(s.value.confidence > 80 &&  s.value > T) {
        ...
    }

    // value == T
    if(s.value.confidence > 80 && s.value == T) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s.value.confidence > 80 && s2.value.confidence > 80 && s.value > s2.value) {
        ...
    }

    //s < s2
    if (s.value.confidence > 80 && s2.value.confidence > 80 &&  s.value < s2.value) {
       ...
    }

    // s == s2
    if(s.value.confidence > 80 && s2.value.confidence > 80 && s.value == s2.value) {
        ...
    }
}
```

If the engineer wants to access to the measured value or the precision, it could do so:

```
function processing(s: Sensor) {
    if(s.value.value > T) {
        ...
    }

     if(s.value.confidence > T) {
        ...
    }
}
```

