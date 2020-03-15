import static com.github.forax.pro.Pro.*;
import static com.github.forax.pro.builder.Builders.*;

pro.loglevel("verbose")

resolver.
  checkForUpdate(true).
  dependencies(
    // JUnit 5
    "org.junit.jupiter.api=org.junit.jupiter:junit-jupiter-api:5.6.0",
    "org.junit.jupiter.params=org.junit.jupiter:junit-jupiter-params:5.6.0",
    "org.junit.platform.commons=org.junit.platform:junit-platform-commons:1.6.0",
    "org.apiguardian.api=org.apiguardian:apiguardian-api:1.1.0",
    "org.opentest4j=org.opentest4j:opentest4j:1.2.0",

    // Findbugs
    "com.google.code.findbugs=com.google.code.findbugs:jsr305:3.0.2",

    // JMH
    "org.openjdk.jmh=org.openjdk.jmh:jmh-core:1.23",
    "org.apache.commons.math3=org.apache.commons:commons-math3:3.3.2",
    "net.sf.jopt-simple=net.sf.jopt-simple:jopt-simple:4.6",
    "org.openjdk.jmh.generator=org.openjdk.jmh:jmh-generator-annprocess:1.23"
    );

compiler.
  sourceRelease(15).
  enablePreview(true).
  processorModuleTestPath(path("deps")) // enable JMH annotation processor
  
packager.
  modules("pro.tremblay.core@1.0")   

run(resolver, modulefixer, compiler, tester, packager /*, perfer */)

pro.arguments().forEach(plugin -> run(plugin))   // run command line defined plugins

/exit errorCode()
