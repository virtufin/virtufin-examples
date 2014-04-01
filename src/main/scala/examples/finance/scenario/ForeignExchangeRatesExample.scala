package examples.finance.scenario

import virtufin.finance.product.Currency._

import virtufin.finance.scenario.{ForeignExchangeRate,ForeignExchangeRates}

object ForeignExchangeRatesExample {
  def main(args: Array[String]) {
    val referenceCurrency = USD
    // price quoted referenceCurrency of a unit of some currency
    val data = List(EUR -> 1.375, GBP -> 1.664, AUD -> 0.9252)
    val quotes = ForeignExchangeRates(referenceCurrency, data:_*)
    // get cross
    val cross = ForeignExchangeRate(AUD, EUR)
    val value = quotes.get(cross)
    println(s"$cross: $value")
  }
}
