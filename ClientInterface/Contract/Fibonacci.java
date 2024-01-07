package Contract;

import Contract.Task;
import java.io.Serializable;

/**
 * Represents a task for calculating the Fibonacci sequence up to a specified number using an iterative method.
 */
public class Fibonacci implements Task, Serializable {

    // StringBuilder to store the result of the Fibonacci sequence
    private StringBuilder result = new StringBuilder();
    
    // The number up to which the Fibonacci sequence is to be calculated
    private int n;

    /**
     * Constructs a Fibonacci task with the specified number.
     *
     * @param n The number up to which the Fibonacci sequence is to be calculated.
     */
    public Fibonacci(int n) {
        this.n = n;
    }

    /**
     * Executes the Fibonacci task, calculating the Fibonacci sequence up to the specified number using an iterative method.
     */
    @Override
    public void executeTask() {
        iterativeFibonacci();
    }

    /**
     * Gets the result of the Fibonacci task.
     *
     * @return The calculated Fibonacci sequence up to the specified number.
     */
    @Override
    public Object getResult() {
        return this.result;
    }

    /**
     * Calculates the Fibonacci sequence up to the specified number using an iterative method.
     */
    public void iterativeFibonacci() {
        long prev1 = 1;
        long prev2 = 0;
        result.append("Generating Fibonacci sequence up to number ").append(n).append(" : ");
        for (int i = 0; i < this.n; i++) {
            result.append(prev2).append(" ");
            long fib = prev1 + prev2;
            prev2 = prev1;
            prev1 = fib;
        }
    }
}
