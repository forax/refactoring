/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.tremblay.core.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import pro.tremblay.core.Position;
import pro.tremblay.core.Preferences;
import pro.tremblay.core.PriceService;
import pro.tremblay.core.ReportingService;
import pro.tremblay.core.Security;
import pro.tremblay.core.SecurityPosition;
import pro.tremblay.core.Transaction;
import pro.tremblay.core.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 5, time = 2)
@Fork(2)
@State(Scope.Benchmark)
public class ReportingServiceBenchmark {
  private final Preferences preferences = Preferences.of(Preferences.LENGTH_OF_YEAR, 365);
  private final PriceService priceService = PriceService.createARandomPriceService();
  private final ReportingService service = new ReportingService(preferences, priceService);
  
  private Collection<Transaction> transactions;
  private Position position;

  @Setup
  public void setup() {
    var securities = Security.values();
    var securityPositions = Stream.of(securities)
        .map(sec -> new SecurityPosition().quantity(BigDecimal.valueOf(1_000)).security(sec))
        .collect(Collectors.toList());

    position = new Position().cash(BigDecimal.valueOf(1_000_000)).securityPositions(securityPositions);

    var now = LocalDate.now();
    var dayOfYear = now.getDayOfYear();

    var transactionTypes = TransactionType.values();

    var random = new Random(0);
    transactions = random.ints(100, 1, 100).mapToObj(value -> {
      var type = transactionTypes[random.nextInt(transactionTypes.length)];
      var date = now.minusDays(random.nextInt(dayOfYear));
      var cash = BigDecimal.valueOf(random.nextInt(1_000));
      var security = type.hasQuantity() ? securities[random.nextInt(securities.length)] : null;
      var quantity = type.hasQuantity() ? BigDecimal.valueOf(value) : BigDecimal.ZERO;
      return new Transaction(type, date, cash, security, quantity);
    }).collect(Collectors.toUnmodifiableList());
  }

  @Benchmark
  public BigDecimal calculate() { 
    return service.calculateReturnOnInvestmentYTD(position, transactions);
  }

  public static void main(String[] args) throws RunnerException {
    var opt = new OptionsBuilder().include(ReportingServiceBenchmark.class.getName()).build();
    new Runner(opt).run();
  }
}
