package walker;

public class PerformanceUtils {

    private static final int NUMBER_CPU = Runtime.getRuntime().availableProcessors();
    private static final int UTILIZATION_CPU = 1;
    private static final int CONSTANT = 1;
    private static final int WAIT_TIME = 1;
    private static final int COMPUTE_TIME = 1;

    public int getDefaultNumThread() {
        return this.getNumberThread(NUMBER_CPU, UTILIZATION_CPU, WAIT_TIME, COMPUTE_TIME);
    }

    public int getNumberThread(int numberCPU, double utilizationCPU, int waitTime, int computeTime) {
        return (int) (numberCPU * utilizationCPU * (CONSTANT + waitTime/computeTime));
    }
}