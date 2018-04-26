from uncertainties import ufloat


class Sensor:
    value: ufloat

    def __init__(self, value, range):
        self.value = ufloat(value, range)

    def __str__(self):
        strval = str(self.value.nominal_value)
        strrange = str(self.value.std_dev)
        return "Sensor(value:" + strval + ", precision:" + strrange + ")"


def reasoning1(s1: Sensor, threshold: float):
    if s1.value > threshold:
        print(str(s1) + " > " + str(threshold))

    elif threshold > s1.value:
        print(str(threshold) + " > " + str(s1))

    elif threshold == s1.value:
        print(str(threshold) + " == " + str(s1))

    else:
        print("Incompatible: " + str(s1) + " and " + str(threshold))


def reasoning2(s1: Sensor, s2: Sensor):
    if s1.value > s2.value:
        print(str(s1) + " > " + str(s2))

    elif s2.value > s1.value:
        print(str(s2) + " > " + str(s1))

    elif s2.value == s1.value:
        print(str(s2) + " == " + str(s1))

    else:
        print("Incompatible: " + str(s2) + " and " + str(s1))


# Test between DUC value and a threshold
reasoning1(Sensor(1, 0.2), 10)
reasoning1(Sensor(1, 0.2), 0)
reasoning1(Sensor(8, 10), 8.5)
reasoning1(Sensor(8, 10), 7.9)
reasoning1(Sensor(8, 10), 8)
print()

# Test between two DUCs with same precision
reasoning2(Sensor(1, 0.2), Sensor(5, 0.2))
reasoning2(Sensor(1, 0.2), Sensor(0, 0.2))
reasoning2(Sensor(1, 0.2), Sensor(1.1, 0.2))
reasoning2(Sensor(1, 0.2), Sensor(0.9, 0.2))
print()

# Test between two DUCs with different precisions
# Test 1: [     (  )  ]
reasoning2(Sensor(5, 2), Sensor(4, 0.2))
reasoning2(Sensor(5, 2), Sensor(6, 0.2))
reasoning2(Sensor(5, 2), Sensor(5, 0.2))
print()
# Test 2: [   ( ]   )
reasoning2(Sensor(5, 2), Sensor(4, 4))
reasoning2(Sensor(5, 2), Sensor(6, 3))
reasoning2(Sensor(5, 2), Sensor(5, 3))
