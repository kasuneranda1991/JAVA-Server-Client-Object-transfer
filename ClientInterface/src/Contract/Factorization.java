package Contract;

import Contract.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Factorization class implements the Task interface and represents a task
 * for finding the prime factors of a given number using trial division.
 */
public class Factorization implements Task, Serializable {

    // Variables to store the number to be factorized and the result
    private int number;
    private StringBuilder result = new StringBuilder();

    /**
     * Constructor to initialize the Factorization object with a given number.
     *
     * @param number The number to be factorized.
     */
    public Factorization(int number) {
        this.number = number;
    }

    /**
     * execute the factorization task. It appends a message to the result
     * StringBuilder indicating the prime factors of the specified number.
     */
    @Override
    public void executeTask() {
        // Create a new Factorization object with the same number
        Factorization fact = new Factorization(this.number);
        
        // Append a message to the result StringBuilder
        this.result.append("Prime factors of ").append(number).append(" are: ");
        
        // Call the factorize get the list of prime factors
        List<Long> factors = fact.factorize(number);
        
        // Append each factor to the result StringBuilder
        for (Long factor : factors) {
            this.result.append(factor).append(" ");
        }
    }

    /**
     * retrieve the result after the task execution.
     *
     * @return The StringBuilder containing the result message.
     */
    @Override
    public Object getResult() {
        return this.result;
    }

    /**
     * Function to factorize a number using trial division.
     *
     * @param n The number to be factorized.
     * @return A list of Long representing the prime factors of the input number.
     */
    public List<Long> factorize(long n) {
        // List to store the prime factors
        List<Long> factors = new ArrayList<>();

        // Handle divisibility by 2 separately
        while (n % 2 == 0) {
            n /= 2;
        }
        factors.add(2L);

        // Iterate from 3 to the square root of n, checking for divisibility
        for (long i = 3; i <= Math.sqrt(n); i += 2) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }

        // If n is a prime greater than 2, add it as a factor
        if (n > 2) {
            factors.add(n);
        }

        // Return the list of prime factors
        return factors;
    }
}
