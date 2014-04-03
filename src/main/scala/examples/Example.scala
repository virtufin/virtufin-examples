package examples

import java.io.{FileOutputStream, OutputStreamWriter, BufferedWriter, File}

/**
 * All examples should extend this class.
 * This class handles the output in the examples.
 */
abstract class Example(outputDirectory:String = Example.outputDirectory) {
  private val directory=new File(outputDirectory)
  if(!directory.exists()){
    directory.mkdir()
  }else if(!directory.isDirectory()){
    throw new RuntimeException(s"$outputDirectory is not a directory")
  }
  private val file=new File(directory, getClass.getName.stripSuffix("$"))
  if(file.exists()){
    file.delete()
  }
  if(!file.createNewFile()){
    throw new RuntimeException(s"Could not create file $file")
  }
  private val writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
  /**
   * Prints message to stdout and appends
   * @param message
   * @return
   */
  def output(message: String) =  {
    writer.append(message+"\n")
    writer.flush()
    println(message)
  }
}
object Example{
  val defaultOutputDirectory = "output"
  var outputDirectory = defaultOutputDirectory
}
