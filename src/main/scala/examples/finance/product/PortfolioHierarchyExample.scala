/*
 * Copyright (c) 2011-2015 Haener Consulting. All rights reserved.
 */

package examples.finance.product

import examples.Example
import virtufin.finance.product._
import virtufin.util.Path


object PortfolioHierarchyExample extends Example {
  // Enable operator notation for paths
  import Path._
  // Enable implicit conversions from String to PortfolioIdentifier
  import PortfolioIdentifier._
  val root:PortfolioIdentifier = "rootPortfolio"
  val id1:PortfolioIdentifier = "123"
  val id2:PortfolioIdentifier = "456"
  val id3:PortfolioIdentifier = "789"
  // Create path with __ and forward slash operator /
  val path1 = __ / root /"a" / "b" / "c" / id1
  // Path from array of identifiers
  val path2 = Path[PortfolioIdentifier](root, "a", "b", "c", id2)
  // Parse path string
  val path3 = Path[PortfolioIdentifier]("rootPortfolio/a/b/d/"+id3.name, '/')
  val position1:Position[Instrument] = Cash(100, Currency.USD)
  val position2:Position[Instrument] = Cash(200, Currency.EUR)
  val position3:Position[Instrument] = Cash(300, Currency.CHF)
  // Building the portfolio with PositionNodes and Paths
  val data = Seq(id1->position1, id2->position2, id3->position3)
  val positionNodes = data.map(x=>x._1->PositionNode(x._1,x._2))
  val builder = PortfolioHierarchyNodeBuilder(positionNodes) + path1 + path2 + path3
  val portfolio = builder.build()
  output(s"Built Portfolio:\n$portfolio")
  // Retrieving nodes by path
  output("\nRetrieving PortfolioHierarchyNodes by Path")
  val x1 = portfolio(path1)
  output(s"$path1 -> $x1")
  val x2 = portfolio(path2)
  output(s"$path2 -> $x2")
  val x3 = portfolio(path3)
  output(s"$path3 -> $x3")
  val path:Path[PortfolioIdentifier] = __ / root /"a" / "b" / "c"
  val x = portfolio(path)
  output(s"$path -> $x")
}
