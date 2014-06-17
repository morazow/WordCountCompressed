// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Assemble a single jar with all dependencies.
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.1")

// Eclipse plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")
