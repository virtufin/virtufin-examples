package scala.examples.finance.simulation

import virtufin.finance.simulation._
import virtufin.finance.product.feature._
import virtufin.finance.product._
import virtufin.finance.{Time, Term}
import virtufin.simulation.{SimultaneousEvents, AgentIdentifier}
import virtufin.finance.simulation.trigger.{SettlementEvent, AccrualEnd}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.collection.SortedSet
import virtufin.util.{Day, Schedule}
import java.util.Calendar
import virtufin.finance.scenario.Quotes
import virtufin.simulation

object SimulatedFeatureExample {
  def main(args: Array[String]) {
    trait FixedRateAccrualPayment extends SimulatedFeature {
      self: CompositeSimulatable with Notional with Accruing with BilateralContract =>
      def fixedRate: Double

      def dayCountFraction: DayCountFraction

      private def transaction(a: AccrualPeriod) = (Payment(Cash(notional.amount * AccrualPeriod.dayCountFraction(a, dayCountFraction) * fixedRate, notional.asset), sellerPortfolio, buyerPortfolio) -> AgentIdentifier("myAgent"))

      private def f: PartialFunction[TriggerEvent, (Transaction, AgentIdentifier)] = {
        case AccrualEnd(time, accrualPeriod) => transaction(accrualPeriod)
        //case SettlementEvent(time) => {new Transaction(){}->AgentIdentifier("AAA")
      }

      private val t = TransactionsGenerator(super.transactionsGenerator, TransactionsGenerator(f))

      abstract override def transactionsGenerator: TransactionsGenerator = t

      abstract override def trigger: Trigger = super.trigger

    }

    trait FixedLegFeature extends CompositeSimulatable with Notional with Accruing with FixedRateAccrualPayment with Settling[Payment] with BilateralContract with Starting with Maturing

    final case class FixedLeg(notional: Cash, frequency: Term, tenor: Term, dayCountFraction: DayCountFraction, fixedRate: Double, startDate: Time, sellerPortfolio: PortfolioIdentifier, buyerPortfolio: PortfolioIdentifier)(implicit timeout: Timeout) extends FixedLegFeature {
      def maturityDate: Time = settlementDates.last

      def settlementDates: SortedSet[Time] = accrualDates.drop(1)

      /**
       * Start/End dates of accrual periods
       */
      def accrualDates: SortedSet[Time] = SortedSet(Schedule(frequency, tenor).generateTimes(startDate): _*)
    }

    implicit val timeout = Timeout(10 seconds)
    val notional = Cash(100, Currency.USD)
    val accrualFrequency = virtufin.util.Term.M1
    val tenor = virtufin.util.Term.Y1
    val dayCountFraction = DayCountFraction.ACT_ACT
    val fixedRate = 0.05
    val sellerPortfolio = PortfolioIdentifier("seller")
    val buyerPortfolio = PortfolioIdentifier("buyer")
    val startDate = Day(2014, Calendar.MAY, 1)
    val fixedLeg = FixedLeg(notional, accrualFrequency, tenor, dayCountFraction, fixedRate, startDate, sellerPortfolio, buyerPortfolio)

    val simulationDates = Schedule(virtufin.util.Term.D1, virtufin.util.Term.Y1).generateTimes(startDate)
    val simulationEvents = simulationDates.map(d => SimulationEvent(d, Quotes.empty, null))
    val trigger = fixedLeg.trigger
    val transactionsGenerator = fixedLeg.transactionsGenerator
    val transactions = simulationEvents.map(e => e.time -> transactionsGenerator.generateTransactions(trigger.project(e)))
    transactions.filter(x => x._2.size > 0).foreach(println)
  }
}
