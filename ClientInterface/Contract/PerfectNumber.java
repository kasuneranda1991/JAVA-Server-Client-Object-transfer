package Contract;

import Contract.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task for finding and displaying perfect numbers up to a specified limit along with their divisors.
 */
public class PerfectNumber implements Task, Serializable {

    // The upper limit to find perfect numbers
    private int limit;
    
    // StringBuilder to store the result of perfect numbers and their divisors
    private StringBuilder result = new StringBuilder();

    /**
     * Constructs a PerfectNumber task with the specified limit.
     *
     * @param limit The upper limit to find perfect numbers.
     */
    public PerfectNumber(int limit) {
        this.limit = limit;
    }

    /**
     * Executes the perfect number task, finding and displaying perfect numbers up to the specified limit and their divisors.
     */
    @Override
    public void executeTask() {
        this.findPerfectNumbers();
    }

    /**
     * Gets the result of the perfect number task.
     *
     * @return The perfect numbers up to the specified limit and their divisors.
     */
    @Override
    public Object getResult() {
        return this.result;
    }

    /**
     * Finds and displays perfect numbers up to the specified limit and their divisors.
     */
    public void findPerfectNumbers() {
        this.result.append("Perfect numbers up to ")
                .append(this.limit)
                .append(" and their divisors:\n");
        for (int i = 2; i <= this.limit; i++) {
            int sumOfDivisors = 0;
            List<Integer> divisors = findDivisors(i);

            for (int divisor : divisors) {
                sumOfDivisors += divisor;
            }

            if (sumOfDivisors == i) {
                this.result.append(i).append(" (Divisors:");
                for (int divisor : divisors) {
                    this.result.append(" ").append(divisor);
                }
                this.result.append(")\n");
            }
        }
    }

    /**
     * Finds divisors of a given number.
     *
     * @param number The number for which divisors are to be found.
     * @return A list of divisors of the specified number.
     */
    public static List<Integer> findDivisors(int number) {
        List<Integer> divisors = new ArrayList<>();
        for (int i = 1; i <= number / 2; i++) {
            if (number % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }
}
