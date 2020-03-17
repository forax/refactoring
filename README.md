# Refactoring fun

This is a fork of [https://github.com/henri-tremblay/refactoring](https://github.com/henri-tremblay/refactoring).

We want to refactor this code until we are happy with it. 
Everything turns around `ReportingService` which calculate an annualized return of investment for a security and cash
position since the beginning of the year.

Have fun and check how far you can go.
Don't hesitate to play with everything, test and production code.
The only important thing is that you should keep the current behavior.

## Usage

* Build: `java pro_wrapper.java`

Note: given it uses the latest early access JDK, spotbugs, jacoco and pitest do not work :(

## Benchmark

To run:
```
./pro/bin/java
    --enable-preview
    --module-path deps:target/test/artifact/
    -m pro.tremblay.core/pro.tremblay.core.benchmark.ReportingServiceBenchmark
```

If you want to run it against multiple commits, you can do `java RunBenchmarkSuite.java commit1, commit2, ...`.

## Maintenance

* Upgrade pro version: `rm -fr ./pro` then run `java pro_wrapper.java`

