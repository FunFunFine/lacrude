val Http4sVersion = "0.21.23"
val CirceVersion = "0.13.0"
val MunitVersion = "0.7.20"
val LogbackVersion = "1.2.3"
val MunitCatsEffectVersion = "0.13.0"
val TofuVersion = "0.10.2"
val DerevoVersion = "0.12.5"

val EstaticoNewtypesVersion = "0.9.25"

val TapirVersion = "0.18.0-M11"

lazy val root = (project in file("."))
  .settings(
    organization := "io.funfunfine",
    name := "lacrude",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-Ymacro-annotations",
      "-Wconf:cat=unused-imports:info,any:warning-verbose"
    ),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-blaze-server"   % Http4sVersion,
      "org.http4s"    %% "http4s-blaze-client"   % Http4sVersion,
      "org.http4s"    %% "http4s-circe"          % Http4sVersion,
      "org.http4s"    %% "http4s-dsl"            % Http4sVersion,
      "io.circe"      %% "circe-generic"         % CirceVersion,
      "io.circe"      %% "circe-refined"         % CirceVersion,
      "io.circe"      %% "circe-golden"          % "0.3.0"                % Test,
      "org.scalameta" %% "munit"                 % MunitVersion           % Test,
      "org.scalameta" %% "munit-scalacheck"      % MunitVersion           % Test,
      "org.typelevel" %% "munit-cats-effect-2"   % MunitCatsEffectVersion % Test,
      "org.typelevel" %% "discipline-munit"      % "1.0.9"                % Test,
      "ch.qos.logback" % "logback-classic"       % LogbackVersion,
      "org.scalameta" %% "svm-subs"              % "20.2.0",
      "tf.tofu"       %% "derevo-cats"           % DerevoVersion,
      "tf.tofu"       %% "derevo-circe-magnolia" % DerevoVersion,
      "tf.tofu"       %% "derevo-scalacheck"     % DerevoVersion,
      "tf.tofu"       %% "tofu"                  % TofuVersion,
      "tf.tofu"       %% "tofu-logging"          % TofuVersion,
      "tf.tofu"       %% "tofu-zio-interop"      % TofuVersion,
      "io.estatico"   %% "newtype"               % "0.4.4",
      "eu.timepit"    %% "refined"               % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-cats"          % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-eval"          % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-jsonpath"      % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-pureconfig"    % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-scalacheck"    % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-scalaz"        % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-scodec"        % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-scopt"         % EstaticoNewtypesVersion,
      "eu.timepit"    %% "refined-shapeless"     % EstaticoNewtypesVersion,
      "org.typelevel" %% "spire"                 % "0.17.0",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoPackage := "io.funfunfine.lacrude.buildinfo",
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      git.branch,
      git.gitHeadCommit,
      git.gitHeadMessage,
      git.gitHeadCommitDate,
      git.gitCurrentBranch
    )
  )
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafixConfigSettings(IntegrationTest)),
    coverageFailOnMinimum := false,
    assembly / mainClass := Some("io.funfunfine.lacrude.Main"),
    licenseConfigurations := Set("compile", "provided", "test", "it")
  )

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := "4.4.18"
ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.5.0",
  "com.github.vovapolu"  %% "scaluzzi"         % "0.1.18",
  "com.eed3si9n.fix"     %% "scalafix-noinfer" % "0.1.0-M1"
)

addCommandAlias(
  "lint",
  "; it:scalafmtAll; test:scalafmtAll; scalafmtAll; it:scalafixAll; test:scalafixAll; scalafixAll;"
)
