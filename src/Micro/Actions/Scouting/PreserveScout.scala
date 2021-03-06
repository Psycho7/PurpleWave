package Micro.Actions.Scouting

import Micro.Actions.Action
import Micro.Actions.Combat.Decisionmaking.Disengage
import ProxyBwapi.UnitInfo.FriendlyUnitInfo

object PreserveScout extends Action {
  
  override def allowed(unit: FriendlyUnitInfo): Boolean = {
    unit.matchups.framesOfSafetyDiffused < 24
  }
  
  override protected def perform(unit: FriendlyUnitInfo) {
    Disengage.delegate(unit)
  }
}
