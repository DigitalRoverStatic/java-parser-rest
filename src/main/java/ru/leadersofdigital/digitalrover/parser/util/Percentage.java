package ru.leadersofdigital.digitalrover.parser.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class for calculation progress percentage
 *
 * @author Stanislav Lebedkin
 */
@Slf4j
public class Percentage {
    private static final int DEFAULT_DISCRETIZATOIN = 5;

    private final AtomicLong percent;
    private AtomicLong counter;
    private final long size;
    private final int discretization;
    private final String message;

    /**
     * Percentage constructor
     * default discretization is {@value #DEFAULT_DISCRETIZATOIN}
     *
     * @param size    iterable collection size
     * @param message log message {@code {message} + " is: {} %"}
     */
    public Percentage(long size, String message) {
        this(size, message, DEFAULT_DISCRETIZATOIN);
    }

    /**
     * Percentage constructor
     *
     * @param size           iterable collection size
     * @param message        log message {@code {message} + " is: {} %"}
     * @param discretization dicretization step for percent {@code percent % discretization == 0}
     */
    public Percentage(long size, String message, int discretization) {
        this.size = size;
        this.message = message;
        this.discretization = discretization;
        counter = new AtomicLong(0);
        percent = new AtomicLong(0);
    }

    /**
     * Increment step
     */
    public void increment() throws InterruptedException {
        if (counter.incrementAndGet() * 100 / size > percent.get()) {
            long percent = this.percent.incrementAndGet();
            if (percent % discretization == 0 || percent == 100) {
                log.info(message + " is: {} %", percent);
                Thread.sleep(10000);
            }
        }
    }
}
