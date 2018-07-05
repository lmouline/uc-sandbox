package simple.bool;


import java.util.Random;

public class Runner {
    public static final int NB_EMPLOYEES = 10;
    public static final int NB_OFFICES = 5;
    public static final int NB_EMPLOYEE_PER_OFFICE = NB_EMPLOYEES / NB_OFFICES;
    public static final Random RANDOM = new Random();

    public static void main(String[] args) {
        Employee[] employees = new Employee[NB_EMPLOYEES];

        for (int i = 0; i < NB_EMPLOYEES; i++) {
            employees[i] = new Employee();
            employees[i].setEmail(nextString(RANDOM, 10));
        }

        Office[] offices = new Office[NB_OFFICES];
        for (int i = 0; i < NB_OFFICES; i++) {
            offices[i] = new Office();
            for (int j = 0; j < NB_EMPLOYEE_PER_OFFICE; j++) {
                offices[i].addEmployee(employees[i * NB_EMPLOYEE_PER_OFFICE + j])
            }

        }

        Building building = new Building();
        for (int i = 0; i < offices.length; i++) {
            building.addRoom(offices[i]);
        }
        building.setHeatingSystemOn(false, 0.8);

    }




    public static String nextString(Random random, int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            int asciicode = random.nextInt(34);
            if(asciicode < 9) {
                asciicode = 48 + asciicode;
            } else {
                asciicode = 97 + (asciicode-9);
            }
            chars[i] = (char) asciicode;
        }
    }


}