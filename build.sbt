import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.github.vangj.gdr",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.10.6",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  pomExtra := (
    <url>https://github.com/vangj/gradient-descent-regression</url>
      <scm>
        <url>git@github.com:vangj/gradient-descent-regression</url>
        <connection>scm:git:gi@github.com:vangj/gradient-descent-regression.git</connection>
        <developerConnection>scm:git:git@github.com:vangj/gradient-descent-regression.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <email>vangjee@gmail.com</email>
          <name>Jee Vang, Ph.D.</name>
          <url>https://github.com/vangj</url>
          <id>vangj</id>
          <organization>Jee Vang</organization>
          <organizationUrl>https://github.com/vangj</organizationUrl>
        </developer>
      </developers>)
)

lazy val commonDeps = Seq(
  junit,
  junitInterface,
  specs,
  spark,
  sparktesting,
  scalatest,
  csvparser,
  argot,
  args4j,
  csvparser
)

lazy val data = (project in file("."))
  .settings(commonSettings: _*)
  //	  .settings(pgpPassphrase := scala.util.Properties.envOrNone("gpgpassphrase").map(_.toCharArray))
  .settings(name := "gradient-descent-regression")
  .settings(libraryDependencies ++= commonDeps)
  .settings(parallelExecution in Test := false)
  .settings(javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"))
