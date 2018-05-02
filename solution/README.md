DUC Language
------------

Our goal in this project is not to define a novel language that manage data uncertainty.
We would like to provide the maximal information to implement them in our language.
For example, in Java it will result in a new framework in which new data types or defined whereas in a DSL it could result in a novel datatype.
However, to implement a PoC, we needed a language that we could extend as required.
This is the main purpose of the DUC language.

## Base language

TBD.


```
struct AStruct {
    att aVar: double

    rel b: BStruct
}

struct BStruct{}

function complexFunc(x: int, y: int): double {
    return (x + y) * 0.2
}

var a: AStruct = AStruct()
a.aVar = 5.2 + complexFunc(3,5)

print a
```

## General idea

Data uncertainty can be seen as a meta-data.
However, this meta-data will influence the reasoning as well as the process that engineers will perform on it.
Indeed, the question behind uncertainty is: how much the data is closed to the reality?

This question cannot be answered only by looking at the data it self, we need other information:

- consistency relationship with other data
- timestamp of the data
- data source
- previous data
- is the data a result of a computation?
- domain knowledge
    - boundaries
    - profile

Uncertainty can be represented using different solutions:

1. precision value: value v +/- x
2. precision using percentage: value v ~ x%
3. confidence percentage
4. gaussian function
5. belief function
6. ... **[Related Work NEEDED HERE]**


This uncertainty can be applied either on a single data or a set of values.
On relationships, it could be applied on all the elements in the relations or on each elements.
On values, it could be applied on one data or a set of data.

Currently, there is two considered solutions: using generic techniques or by defining a extended concept of what is a property.

## Generic type

The global idea here is to define an uncertainty type for each representation with a generic type: `Uncertain<T>`.
Idea similar to the work made by Microsoft Research: "[Uncertain\<T>: A First-Order Type for Uncertain Data](https://dl.acm.org/citation.cfm?id=2541958)".

Based on the current list of uncertain representation, we can derived it in different uncertain types:

1. Unprecise<T>
2. UnprecisePerc<T>
3. Unconfident<T>
4. UncertainGaussFunc<T>
5. UncertainBeliefFunc<T>
6. ???

All these types should "specialized" the `Uncertain<T>` . This types should encapsule the elements required to answer the predifined question:

- a relation / a set of relations to store the consistency relationship(s) (e.g., a List<?>)
- an attribute to store the timestamp (e.g., a long)
- an attribute to store the source
- a relation to a structure that stores the past values
- a set of relations to store the computation sources
- a set of relations to store the domain knowledge

To represent uncertainty on a set of values, T can be equal to a collection type.
Example: `Unconfident<List<double>`.

To represent uncertainty on all elements of a relationship, T is equal to the relationship type.
Example: `Unconfident<AClass>`.

To represent uncertainty on each elements of a relationship, we need to add a new annotation: `@Uncertainty(value = "onEach")`.
The default value is "onAll", and it is equivalent to not put the annotation.

Example (based on GCM language):
```

struct AStruct {
    att myVar: UncertainGaussFunc<T>
    att myVar2: Unconfident<double[]>

    rel b: ConfidencePerc<BStruct>

    @Uncertainty(value = "onEach")
    rel b: ConfidencePerc<BStruct>
}

struct BStruct {}

```



## Extension of property definition

The global idea here is to define a new kind of property: uncertainty property.
Current definition of properties is mainly implemented by its value and its type.
An uncertainty property is also defined by a set of metadata:

- either an atribute or a relation (i.e., a reference or a collection of references)
- a relation / a set of relations to store the consistency relationship(s) (e.g., a List<?>)
- an attribute to store the timestamp (e.g., a long)
- an attribute to store the source
- a relation to a structure that stores the past values
- a set of relations to store the computation sources
- a set of relations to store the domain knowledge

Then, based on the current list of uncertain representation, we can either defined a list of new keyword or using annoted properties.

Here, we choose to combined both: defined a new kind of properties (`uatt` and `urel`) and to add some annotation to precise the chosen uncertainty representation.


Example (based on GCM):
```

struct AStruct {
    @UCRepresentation(name = "GaussianFunction")
    uatt myVar: double

    @UCRepresentation(name = "ConfidentPerc")
    uatt myVar2: double[]

    @UCRepresentation(name = "ConfidencePerc")
    urel b: BStruct

    @UCRepresentation(name = "ConfidencePerc", param = "onEach" )
    urel b: BStruct
}

struct BStruct {}

```


