import AssemblyKeys._

name := "Word Count Compressed"

organization := "com.morazow"

version := "1.0"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
                  "conjars.org" at "http://conjars.org/repo")

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.twitter" %% "scalding-core" % "0.10.0",
  "org.apache.hadoop" % "hadoop-core" % "1.2.1"
)

assemblySettings

jarName in assembly := s"wcc-${version.value}.jar"

test in assembly := {}

excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  val exludeSet = Set(
    "asm-3.1.jar",
    "minlog-1.2.jar", // conflicts with kryo
    "stax-api-1.0.1.jar",
    "jsp-2.1-6.1.14.jar",
    "jsp-api-2.1-6.1.14.jar",
    "commons-beanutils-1.7.0.jar",
    "commons-beanutils-core-1.8.0.jar", // clashes with each other and with commons-collections
    "hadoop-core-1.2.1.jar" // provided in hadoop env
  )
  cp filter { jar => exludeSet(jar.data.getName) }
}


