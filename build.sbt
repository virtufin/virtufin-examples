name := "Virtufin Examples"

version := "1.0-Snapshots"

organization := "com.haenerconsulting"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
    "com.haenerconsulting" %% "virtufin-core" % "1.0-SNAPSHOT",
    "com.haenerconsulting" %% "virtufin-base" % "1.0-SNAPSHOT"
)

resolvers ++= Seq(
    "Virtufin snapshots" at "http://repo.haenerconsulting.com/content/repositories/snapshots",
    "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/"
)
