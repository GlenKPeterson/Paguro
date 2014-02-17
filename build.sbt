name := "fp4java7"

version := "0.2-SNAPSHOT"

//val licenseFile: File = file("LICENSE.txt")

//`package` ++= (licenseFile, "LICENSE.txt")

val someFileTask = taskKey[File]("LICENSE.txt")

someFileTask := {
  val src: File = baseDirectory.value / "LICENSE.txt"
  val out: File = target.value / "LICENSE.txt"
  IO.copyFile(src, out)
  out
}
// addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")

// unmanagedJars in Compile += new File("/home/gpeterso/tools/apache-tomcat-7.0.28/lib/servlet-api.jar")

// libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1"
                        
// libraryDependencies += "junit" % "junit" % "4.10"
