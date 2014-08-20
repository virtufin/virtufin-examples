package examples.finance.product

import java.util.Calendar

import examples.Example
import virtufin.finance.product._
import virtufin.finance.product.feature.{Maturing, Settling, Optional, Notional}
import virtufin.util.Day

object FeatureExample extends Example {
  val assetPosition = Position(100.0,Stock("IBM"))
  val cashPosition = Cash(189361.20, Currency.USD)
  val settlementDate = Day(2020, Calendar.JUNE, 1)
  val buyerPortfolio = PortfolioIdentifier("nostro")
  val sellerPortfolio = PortfolioIdentifier("GS")
  val trade = PhysicallySettledForwardContract(assetPosition, cashPosition, settlementDate, buyerPortfolio, sellerPortfolio)
  val featureId1 = Settling.featureIdentifier
  val featureInfoExtractor1 = Settling.featureInfoExtractor
  val featureId2 = Maturing.featureIdentifier
  val featureInfoExtractor2 = Maturing.featureInfoExtractor
  val featureId3 = Optional.featureIdentifier
  val featureInfoExtractor3 = Optional.featureInfoExtractor
  val featureInfoExtractor4 = FeatureInfoExtractor.compose(featureInfoExtractor1, featureInfoExtractor2, featureInfoExtractor3)
  val info1 = featureInfoExtractor4.getInfo(trade, featureId1)
  output(s"${featureId1} -> $info1")
  val info2 = featureInfoExtractor4.getInfo(trade, featureId2)
  output(s"${featureId2} -> $info2")
  val info3 = featureInfoExtractor4.getInfo(trade, featureId3)
  output(s"${featureId3} -> $info3")
  val featureIds = FeatureInfoExtractor.defaultFeatureIdentifiers
  val featureInfoExtractor = FeatureInfoExtractor.defaultFeatureInfoExtractor
  featureIds.foreach(i=>println(i+"->"+featureInfoExtractor.getInfo(trade,i)))
  val infos = featureInfoExtractor.getInfos(trade, featureIds:_*)
  infos.foreach(i=>output(s"${i._1} -> ${i._2}"))
}