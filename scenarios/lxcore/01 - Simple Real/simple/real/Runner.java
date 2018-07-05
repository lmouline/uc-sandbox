package simple.real;

import java.util.Random;

public class Runner {

    public static final Random RANDOM = new Random();
    public static final int NB_ROOMS = 10;


    public static void main(String[] args) {
        Room[] rooms = new Room[NB_ROOMS];

        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = new Room();
            room.setTemperature(RANDOM.doubles() * 30, RANDOM.doubles())
        }

        Building building = new Building();
        for (int i = 0; i < rooms.length; i++) {
            building.addRoom(rooms[i]);
        }
    }
}
