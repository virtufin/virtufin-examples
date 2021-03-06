/*
 * Copyright (c) 2011-2015 Haener Consulting. All rights reserved.
 */

package examples

import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes

object Examples {
  def main(args: Array[String]) {
    runAllExamples()
  }

  def runAllExamples() {
    val masterExample = "examples.Examples"
    val l = getClass.getClassLoader
    val r = getClass.getName.replace(".", "/") + ".class"
    val x = l.getResource(r).getPath.replace(r, "")

    class ProcessFile extends SimpleFileVisitor[Path] {
      override def visitFile(aFile: Path, aAttrs: BasicFileAttributes): FileVisitResult = {
        val n = aFile.toString
        if (n.endsWith(".class")) {
          val cls = n.replace(x, "").replace("/", ".").replace(".class", "")
          if (!cls.contains(masterExample)) {
            val c = l.loadClass(cls)
            try {
              val m = c.getMethod("main", classOf[Array[String]])
              val mods = m.getModifiers
              if (java.lang.reflect.Modifier.isPublic(mods) && java.lang.reflect.Modifier.isStatic(mods)) {
                println("Running example "+c.getSimpleName)
                m.invoke(null, Array[String]())
              }
            }
            catch {
              case e: Throwable =>
            }
          }
        }
        FileVisitResult.CONTINUE
      }
      override def preVisitDirectory(aDir: Path, aAttrs: BasicFileAttributes): FileVisitResult = {
        FileVisitResult.CONTINUE
      }
    }
    val fileProcessor = new ProcessFile()
    Files.walkFileTree(Paths.get(x), fileProcessor)
  }
}
