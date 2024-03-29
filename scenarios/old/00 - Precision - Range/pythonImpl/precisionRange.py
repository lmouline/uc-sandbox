from uncertainties import ufloat


class UReal:
    value: ufloat

    def __init__(self, value, range):
        self.value = ufloat(value, range)

    def __str__(self):
        strval = str(self.value.nominal_value)
        strrange = str(self.value.std_dev)
        return "UReal(value:" + strval + ", precision:" + strrange + ")"


def reasoning1(s1: UReal, threshold: float):
    if s1.value > threshold:
        print(str(s1) + " > " + str(threshold))

    elif threshold > s1.value:
        print(str(threshold) + " > " + str(s1))

    elif threshold == s1.value:
        print(str(threshold) + " == " + str(s1))

    else:
        print("Incompatible: " + str(s1) + " and " + str(threshold))



def reasoning2(s1: UReal, s2: UReal):
    if s1.value > s2.value:
        print(str(s1) + " > " + str(s2))

    elif s2.value > s1.value:
        print(str(s2) + " > " + str(s1))

    elif s2.value == s1.value:
        print(str(s2) + " == " + str(s1))

    else:
        print("Incompatible: " + str(s2) + " and " + str(s1))


def propagation1(s1: UReal, number: float):
    print("Addition: " + str(s1) + " + " + str(number) + " = " + str(s1.value + number))
    print("Subtraction: " + str(s1) + " - " + str(number) + " = " + str(s1.value - number))
    print("Division: " + str(s1) + " / " + str(number) + " = " + str(s1.value / number))
    print("Multiplication: " + str(s1) + " * " + str(number) + " = " + str(s1.value * number))


def propagation2(s1: UReal, s2: UReal):
    print("Addition: " + str(s1) + " + " + str(s2) + " = " + str(s1.value + s2.value))
    print("Subtraction: " + str(s1) + " - " + str(s2) + " = " + str(s1.value - s2.value))
    print("Division: " + str(s1) + " / " + str(s2) + " = " + str(s1.value / s2.value))
    print("Multiplication: " + str(s1) + " * " + str(s2) + " = " + str(s1.value * s2.value))


# Test between DUC value and a threshold
reasoning1(UReal(1, 0.2), 10)
reasoning1(UReal(1, 0.2), 0)
reasoning1(UReal(8, 10), 8.5)
reasoning1(UReal(8, 10), 7.9)
reasoning1(UReal(8, 10), 8)
print()

# Test between two DUCs with same precision
reasoning2(UReal(1, 0.2), UReal(5, 0.2))
reasoning2(UReal(1, 0.2), UReal(0, 0.2))
reasoning2(UReal(1, 0.2), UReal(1.1, 0.2))
reasoning2(UReal(1, 0.2), UReal(0.9, 0.2))
print()

# Test between two DUCs with different precisions
# Test 1: [     (  )  ]
reasoning2(UReal(5, 2), UReal(4, 0.2))
reasoning2(UReal(5, 2), UReal(6, 0.2))
reasoning2(UReal(5, 2), UReal(5, 0.2))
print()
# Test 2: [   ( ]   )
reasoning2(UReal(5, 2), UReal(4, 4))
reasoning2(UReal(5, 2), UReal(6, 3))
reasoning2(UReal(5, 2), UReal(5, 3))
print()

print()
# Test propagation
propagation1(UReal(5, 0.3), 4)
print()
propagation2(UReal(5, 0.3), UReal(2, 0.2))

