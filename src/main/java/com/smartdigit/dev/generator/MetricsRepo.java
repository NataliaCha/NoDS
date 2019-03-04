package com.smartdigit.dev.generator;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MetricsRepo {

  private MetricsRepo() {
  }

  public static final MetricRegistry metrics = new MetricRegistry();

  public static void initMetrics() {
//    metrics.register("gc", new GarbageCollectorMetricSet());
//    metrics.register("threads", new CachedThreadStatesGaugeSet(10, TimeUnit.SECONDS));
//    metrics.register("memory", new MemoryUsageGaugeSet());

    final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(5, TimeUnit.SECONDS);

//    graphiteReporter();
  }

  private static void graphiteReporter() {
    java.security.Security.setProperty("networkaddress.cache.ttl" , "60");

    final Graphite graphite =
        new Graphite(new InetSocketAddress("40df0bce.carbon.hostedgraphite.com", 2003));

    final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
        .prefixedWith("7b0a4b5f-aa25-48ff-8fdc-71d17906297a")
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(graphite);
    reporter.start(7, TimeUnit.SECONDS);
  }

}
