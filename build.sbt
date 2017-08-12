
scalaVersion in ThisBuild := "2.12.2"

lazy val root = project.in(file(".")).
  aggregate(slidegameJS, slidegameJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val slidegame = crossProject.in(file(".")).
  settings(
    name := "scalajs-slidegame",
    organization := "net.surguy",
    version := "0.1-SNAPSHOT"
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.9.4" % "test"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
      , "com.lihaoyi" %%% "scalatags" % "0.6.5"
      , "com.lihaoyi" %%% "utest" % "0.4.4" % "test"
    ),
    relativeSourceMaps := true,
    emitSourceMaps := true,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsDependencies += RuntimeDOM
  )

lazy val slidegameJVM = slidegame.jvm
lazy val slidegameJS = slidegame.js
  .enablePlugins(WorkbenchPlugin)

testFrameworks += new TestFramework("utest.runner.Framework")

import S3._
s3Settings
mappings in upload := Seq((new java.io.File("js/target/scala-2.12/scalajs-slidegame-opt.js"),"scalajs-slidegame-opt.js"),
  (new java.io.File("js/target/scala-2.12/classes/index-opt.html"),"resources/index-opt.html"))
host in upload := "scalajs-slidegame.s3.amazonaws.com"
