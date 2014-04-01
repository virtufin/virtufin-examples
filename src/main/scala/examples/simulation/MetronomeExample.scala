package examples.simulation

import java.util.Calendar
import play.api.libs.iteratee.Iteratee
import scala.concurrent.{Await, ExecutionContext}
import virtufin.util._
import virtufin.simulation._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

object MetronomeExample {
  def main(args: Array[String]) {
    // Metronome emitting 0, 1, 2,...
    // emitting events as fast as possible
    val delayInt = Duration.Zero
    val nMax = 10000
    def metronomeInt = Metronome(delayInt, n => n > nMax)
    // Metronome emitting from start date monthly dates for 1 years
    // events emitted every second
    val delayDay = 1 second
    val startDate = Day(2014, Calendar.NOVEMBER, 1)
    import FiniteSchedule._
    val schedule = Schedule(Term.M1) until Term.Y1
    val dates = schedule.generateTimes(startDate)
    def metronomeDay = Metronome(dates, delayDay)

    // Iteratees:
    // adds all values emitted by metronomeInt
    val adder = Iteratee.fold[Int, Int](0)((l: Int, x: Int) => l + x)
    // prints days
    val printerDay = Iteratee.foreach[Day](d => println(s"${new java.util.Date()} -> $d")).map(_=>println("Done"))
    // stores all values emitted by the metronomeDay
    val collectorDays = Iteratee.fold(List[Day]())((l: List[Day], x: Day) => l.::(x))

    // Run the Iteratees
    val resultAdd=Await.result(metronomeInt |>>> adder, 20 seconds)
    println("Sum is "+resultAdd)
    Await.result(metronomeDay |>>> printerDay, 20 seconds)
    val resultCollectDays = Await.result(metronomeDay |>>> collectorDays, 20 seconds)
    println("Days collected: "+resultCollectDays.reverse)
  }
}
