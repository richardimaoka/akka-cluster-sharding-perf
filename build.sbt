name := "akka-cluster-sharding-perf"

version := "0.1"

scalaVersion := "2.12.8"



libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.5.20",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.20",
  "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.0",
  "com.typesafe.slick" %% "slick" % "3.3.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
  "mysql" % "mysql-connector-java" % "6.0.6"
)