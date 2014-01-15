package examples.finance.simulation

import virtufin.finance.simulation._
import virtufin.finance.simulation.state.{CompositeMutableState, FixingValue}
import virtufin.finance.product.feature.{BilateralContract, Settling, SingleFixingDerivative}
import virtufin.finance.product._
import java.util.Calendar
import virtufin.util.Day
import virtufin.finance.Time
import scala.collection.SortedSet
import scala.concurrent.Await
import virtufin.simulation.{AgentIdentifier, Agent}
import virtufin.finance.scenario.{Quotes, Price, Fixing}
import virtufin.finance.simulation.trigger.SettlementEvent
import akka.util.Timeout
import scala.concurrent.duration._
import akka.actor.{Props, ActorSystem}
import akka.pattern._

object StatefulSimulatedFeatureExample {
  implicit val timeout = Timeout(10 seconds)

  def main(args: Array[String]) {


    trait BuyInstrumentFeature {
      def numberUnits: Double

      def instrument: Instrument

      def paymentCurrency: Currency

      def price = Price(instrument, paymentCurrency)
    }
    trait PaymentFeature extends StatefulSimulatedFeature[FixingValue[Double]] {
      self: StatefulCompositeSimulatable[FixingValue[Double]] with BuyInstrumentFeature with BilateralContract =>
      abstract override def trigger: Trigger = super.trigger

      private val t: PartialFunction[TriggerEvent, (Transaction, AgentIdentifier)] = {
        case e: SettlementEvent => {
          val v: Option[Double] = state.fixingValue
          if (v.isEmpty) throw new Exception("No value for "+state.fixing)
          val price=Cash(v.get * numberUnits, paymentCurrency)
          val position=Position(numberUnits,instrument)
          (Buy(price, position, sellerPortfolio, buyerPortfolio), AgentIdentifier("portfolioHierarchyAgent"))
        }
      }

      abstract override def transactionsGenerator: TransactionsGenerator = TransactionsGenerator(super.transactionsGenerator, TransactionsGenerator (t))
    }
    case class BuyInstrument(numberUnits: Double, instrument: Instrument, paymentCurrency: Currency, fixingDate: Time, settlementDate: Time, buyerPortfolio: PortfolioIdentifier, sellerPortfolio: PortfolioIdentifier) extends StatefulCompositeSimulatable[FixingValue[Double]] with BuyInstrumentFeature with PaymentFeature with SingleFixingDerivative[Double] with Settling[Payment] with BilateralContract {
      self=>
      def settlementDates: SortedSet[Time] = SortedSet(settlementDate)

      def fixing: Fixing[Double] = Fixing(fixingDate, Price(instrument, paymentCurrency))
      private val s= new CompositeMutableState with FixingValue[Double]{
        def fixing: Fixing[Double] = self.fixing
      }
      def state=s
    }

    val numberUnits = 100
    val instrument = new Instrument{override def toString="IBM"}
    val paymentCurrency=Currency.USD
    val fixingDate = Day(2014, Calendar.JUNE, 1)
    val settlingDate = Day(2014, Calendar.JUNE, 3)
    val sellerPortfolio:PortfolioIdentifier="seller"
    val buyerPortfolio:PortfolioIdentifier="buyer"
    val myTrade = BuyInstrument(numberUnits, instrument, paymentCurrency, fixingDate, settlingDate, sellerPortfolio, buyerPortfolio)
    val system=ActorSystem()
    val agent = system.actorOf(Props(myTrade.createAgent()))
    val quotes=Quotes(Price(instrument, paymentCurrency)-> 185.0)
    val simulationEvents=List(SimulationEvent(fixingDate,quotes, null), SimulationEvent(settlingDate,Quotes.empty,null))
    simulationEvents.foreach(e=>println(e.time+Await.result(agent ? Agent.MessagesGenerationRequest(e), timeout.duration).toString))
  }
}
