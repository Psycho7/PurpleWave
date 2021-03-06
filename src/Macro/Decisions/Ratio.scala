package Macro.Decisions

import ProxyBwapi.UnitClass.UnitClass

trait Ratio {
  val unit      : UnitClass
  val otherUnit : UnitClass
  val ratio     : Double
  
  protected def quantity(context: DesireContext): Int
  
  def addedQuantity(context: DesireContext): Double = quantity(context) * ratio
}