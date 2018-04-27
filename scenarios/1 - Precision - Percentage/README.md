# Percentage scenario

A way to define the precision of a value is to define a value and its range based on a percentage: <value: v, percentage: p>.
Using current solutions, a designer has to manually represents both values:

```
class Sensor {
    att value: double
    att ucPercentage: double
}
```

When an engineer wants to use this value, he has to manually consider this precision:

```
function reasoning(s: Sensor) {
    // compared to a threshold
    // value > T
    if(s.value * (1. + s.ucPercentage) > T) {
        ...
    }

    // value < T
    if(T > s.value - (1. - s.ucPercentage)) {
        ...
    }

    // value == T
    if(T > s.value - (1. - s.ucPercentage) && T < s.value + (1. + s.ucPercentage)) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s.value * (1. + s.ucPercentage) > s2.value * (1. + s2.ucPercentage)) {
        ...
    }

    //s < s2
    if (s.value * (1. - s.ucPercentage) > s2.value * (1. - s2.ucPercentage)) {
       ... 
    }

    // s == s2
    // we consider that 2 values are equal if it exists an overlap between two ranges
    if(s2.value * (1. - s2.ucPercentage) > s.value - * (1. - s.ucPercentage) && s.value * (1. + s.ucPercentage) > s2.value * (1. + s2.ucPercentage) ) {
        ...
    }
}
```

Using our solution:
```
class Sensor {
    att value: ImprecisePerc<double>
}
```

The previous code will then looks like:
```
function reasoning(s: Sensor) {
    // compared to a threshold
    // value > T
    if(s.value > T) {
        ...
    }

    // value < T
    if(T > s.value) {
        ...
    }

    // value == T
    if(s.value == T) {
        ...
    }

    var s2: Sensor
    // compared to another sensor value
    //s > s2
    if (s > s2) {
        ...
    }

    //s < s2
    if (s < s2) {
       ... 
    }

    // s == s2
    // we consider that 2 values are equal if it exists an overlap between two ranges
    if(s == s2) {
        ...
    }
}
```

If the engineer wants to access to the measured value or the precision, it could do so:

```
function reasoning(s: Sensor) {
    if(s.value.value > T) {
        ...
    }

     if(s.value.percentage > T) {
        ...
    }
}
```

