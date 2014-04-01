name := "Virtufin Examples"

version := "1.0-Snapshots"

organization := "com.haenerconsulting"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
    "com.haenerconsulting" % "virtufin-core" % "1.0-SNAPSHOT",
    "com.haenerconsulting" % "virtufin-base" % "1.0-SNAPSHOT"
)

resolvers ++= Seq(
    "Virtufin Snapshots" at "http://repo.haenerconsulting.com/content/repositories/snapshots"
)
