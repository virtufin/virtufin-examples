package examples.finance.product

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import examples.Example
import virtufin.finance.simulation._
import virtufin.finance.simulation.feature.{CompositeSimulatedFeature, StatelessCompositeSimulatedFeature}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object SimulatedFeatureExample extends Example {

  implicit val timeout = new Timeout(1 second)

  trait Echo extends SimulatedFeature {
    self: CompositeSimulatedFeature =>
    abstract override def createMessageProcessor(agent: AgentType): MessageProcessor = MessageProcessor(super.createMessageProcessor(agent),
      new MessageProcessor {
        def processMessage(msg: Any): Iterable[Any] = msg match {
          case _ => List[Any](msg)
        }
      })
  }

  trait HelloWorld extends SimulatedFeature {
    self: CompositeSimulatedFeature =>
    abstract override def createMessageProcessor(agent: AgentType): MessageProcessor = MessageProcessor(super.createMessageProcessor(agent),
      new MessageProcessor {
        def processMessage(msg: Any): Iterable[Any] = msg match {
          case "Hello" => List[Any]("World")
          case x: Any => List[Any](s"Don't understand $x")
        }
      })
  }

  case object GetName

  trait GetNameForMyAgent extends SimulatedFeature {
    self: MyAgent =>
    abstract override def createMessageProcessor(agent: AgentType): MessageProcessor = MessageProcessor(super.createMessageProcessor(agent), new MessageProcessor {
      def processMessage(msg: Any): Iterable[Any] = msg match {
        case GetName => List[Any](self.name)
        case _ => List.empty[Any]

      }
    })
  }

  /**
   * Agent for which the [[SimulatedFeature.createMessageProcessor( )]]s methods from [[Echo]], [[HelloWorld]], [[GetNameForMyAgent]] are stacked
   * @param name
   */
  class MyAgent(val name: String) extends StatelessCompositeSimulatedFeature with GetNameForMyAgent with Echo with HelloWorld

  val name = "myTestAgent"
  val myAgent = new MyAgent(name)
  val props = Props(myAgent.createAgent())
  val system = ActorSystem()

  val agent = system.actorOf(props)
  val msg1 = "Hello"
  val r1 = agent ? msg1
  r1.onComplete(r => output(s"$msg1 -> $r"))
  val msg2 = GetName
  val r2 = agent ? msg2
  r2.onComplete(r => {output(s"$msg2 -> $r");system.shutdown(); System.exit(0)})
}
