public class TestSensorAccuracy {

    /**
     * Source: http://www.eenewseurope.com/sites/default/files/images/01-edit-photos-uploads/2011/2011-06-june/2011-06-24-mlx90614.jpg
     */

    public static void main(String[] args) {
        System.out.println(accuracy(60,130));
    }

    private static double accuracy(double ambientTemperature, double objectTemperature) {
        double[][] accuracyMatrix = {{4,4,4,4,4},
                                     {4,4,3,3,3},
                                     {4,3,2,2,2},
                                     {3,2,1,1,2},
                                     {2,1,0.5,1,2},
                                     {3,1,1,2,3},
                                     {3,3,2,3,4}};

        int ligneIdx, colonneIdx;
        if(ambientTemperature < -40) {
            throw new RuntimeException("Unknown");
        } else if(ambientTemperature < -20) {
            colonneIdx = 0;
        } else if(ambientTemperature < 0) {
            colonneIdx = 1;
        } else if(ambientTemperature < 50) {
            colonneIdx = 2;
        } else if(ambientTemperature < 100) {
            colonneIdx = 3;
        } else if(ambientTemperature < 125) {
            colonneIdx = 4;
        } else {
            throw new RuntimeException("Unknown");
        }

        if(objectTemperature < -70) {
            throw new RuntimeException("Unknown");
        } else if(objectTemperature < -40) {
            ligneIdx = 6;
        } else if(objectTemperature < 0) {
            ligneIdx = 5;
        } else if(objectTemperature < 60) {
            ligneIdx = 4;
        } else if(objectTemperature < 120) {
            ligneIdx = 3;
        } else if(objectTemperature < 180) {
            ligneIdx = 2;
        } else if(objectTemperature < 240) {
            ligneIdx = 1;
        } else if(objectTemperature < 380) {
            ligneIdx = 0;
        } else {
            throw new RuntimeException("Unknown");
        }


        return accuracyMatrix[ligneIdx][colonneIdx];
    }
}
