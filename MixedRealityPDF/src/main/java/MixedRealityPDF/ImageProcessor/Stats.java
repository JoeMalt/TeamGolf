package MixedRealityPDF.ImageProcessor;

public class Stats {

    public static double getMean(double[] data) {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.length;
    }

    public static double getVariance(double[] data) {
        double mean = getMean(data);
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/(data.length-1);
    }

    public static double getStdDev(double[] data) {
        return Math.sqrt(getVariance(data));
    }

//    public double median() {
//        Arrays.sort(data);
//
//        if (data.length % 2 == 0) {
//            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
//        }
//        return data[data.length / 2];
//    }

}
