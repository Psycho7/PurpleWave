package Global.Information.Combat

import Types.UnitInfo.UnitInfo
import bwapi.Position

import scala.collection.mutable

class CombatGroup(
  val vanguard:Position,
  val units:mutable.HashSet[UnitInfo]) {
  
}