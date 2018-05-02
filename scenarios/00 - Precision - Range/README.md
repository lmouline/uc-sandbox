# Precision scenario

Sensors have always a precision measurement due to their hardware.
When a new value is measurement, the sensor can send it with the measurement precision: <value: v, precision: p>.
Using current solutions, a designer has to manually represents both values.

## Without UC Abstraction solution

```
struct Sensor {
    att value: double
    att precision: double
}
```

When an engineer wants to use this value, he has to manually consider this precision:

```
function processing(s: Sensor, T: double, s2: Sensor) {
    // Compare to a threshold
    // value > T
    if(s.value + s.precision > T) {
        ...
    }

    // value < T
    if(T > s.value - s.precision) {
        ...
    }

    // value == T
    if(T > s.value - s.precision && T < s.value + s.precision) {
        ...
    }

    // Compare to another sensor value
    //s > s2
    if (s.value + s.precision > s2.value + s2.precision) {
        ...
    }

    //s < s2
    if (s.value - s.precision > s2.value - s2.precision) {
       ... 
    }

    // s == s2
    // we consider that 2 values are equals if it exists an overlap between two ranges
    if(s2.value - s2.precision > s.value - s.precision && s.value + s.precision > s2.value + s2.precision ) {
        ...
    }
}
```

## With our solution

**Generic** approach:
```
struct Sensor {
    att value: Unprecise<double>
}
```

**Extension of property definition**:
```
struct Sensor {
    @UCRepresentation(name = "Precision")
    uatt value: double
}
```

The previous code will then look like:
```
function processing(s: Sensor, T: double, s2: Sensor) {
    // Compare to a threshold
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
    // Compare to another sensor value
    //s > s2
    if (s.value > s2.value) {
        ...
    }

    //s < s2
    if (s.value < s2.value) {
       ... 
    }

    // s == s2
    // we consider that 2 values are equals if it exists an overlap between two ranges
    if(s.value == s2.value) {
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

     if(s.value.precision > T) {
        ...
    }
}
```

