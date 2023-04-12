package walker;

public class PerformanceUtils {

    private static final int NUMBER_CPU = Runtime.getRuntime().availableProcessors();
    private static final int CONSTANT = 1;
    private static final int DOUBLE = 2;

    public static int getDefaultNumThread() {
        return NUMBER_CPU * DOUBLE;
    }

    public static int getNumberThread(int numberCPU, double utilizationCPU, double ratioWaitComputeTime) {
        return (int) (numberCPU * utilizationCPU * (CONSTANT + ratioWaitComputeTime));
    }

    public static int getNumberCpu() {
        return NUMBER_CPU;
    }
}