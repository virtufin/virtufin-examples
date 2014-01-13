package scala.examples.finance.simulation

import virtufin.finance.simulation._
import virtufin.finance.product.feature.{Notional, Accruing, Settling, Maturing}
import virtufin.finance.product.{Transaction, Currency, Cash, Payment}
import virtufin.finance.Term
import virtufin.util.Term
import virtufin.finance.Term
import virtufin.simulation.AgentIdentifier

object SimulatedFeatureExample {
  trait FixedRateAccrualPayment extends SimulatedFeature{
    a:Accruing =>
    def fixedRate:Double
    def transactionsGenerator: TransactionsGenerator = new TransactionsGenerator {
      def generateTransactions(triggerEvents: TriggerEvents): Iterable[(Transaction, AgentIdentifier)] = e match{
          ev:AccrualEvent => List()
      }

    }

    def trigger: Trigger =super.trigger

  }
  trait FixedLegFeature extends SimulatedFeature with Notional with Accruing with Settling[Payment] with Maturing

  final case class FixedLeg(notional: Cash, accrualFrequency: Term, tenor: Term) extends CompositeSimulatable with FixedLegFeature

  val notional = Cash(100, Currency.USD)
  val accrualFrequency = Term.M1
  val tenor = Term.Y1
  val fixedLeg=FixedLeg(notional, accrualFrequency, )

}
