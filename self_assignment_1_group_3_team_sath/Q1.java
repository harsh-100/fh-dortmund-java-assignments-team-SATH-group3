public class Q1 {
    public static void main(String[] args){

        int sunDiameter = 865_000;
        double sunRadius = sunDiameter/2.0;
        int earthDiameter = 7_600;
        double earthRadius = earthDiameter/2.0;
        double earthVolume = 0.0;
        double sunVolume = 0.0;
        double sunToEarthRatio = 0.0;
        earthVolume = (4.0/3.0)*(Math.PI*Math.pow(earthRadius, 3));
        sunVolume = (4.0/3.0)*(Math.PI*Math.pow(sunRadius, 3));
        sunToEarthRatio = sunVolume / earthVolume;
        String earthVolumeStr = String.format(" %.3e cubic miles", earthVolume);
        String sunVolumeStr = String.format(" %.3e cubic miles", sunVolume);
        String sunToEarthRatioStr = String.format(" %.3e", sunToEarthRatio);
        System.out.println("The volume of the Earth is" + earthVolumeStr + ",\n" +
                "the volume of the sun is" + sunVolumeStr + ",\n" +
                "and the ratio of the volume of the Sun to the volume of the Earth is"
                + sunToEarthRatioStr + ".");

    }
}