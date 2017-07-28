package Micro.Actions.Combat.Maneuvering

import Micro.Actions.Action
import Micro.Actions.Combat.Attacking.Potshot
import ProxyBwapi.UnitInfo.FriendlyUnitInfo

object Sneak extends Action {
  
  override protected def allowed(unit: FriendlyUnitInfo): Boolean = {
    unit.cloaked && ( ! unit.effectivelyCloaked || {
      
      val matchups = unit.matchups.ifAt(24)
      matchups.threats.nonEmpty &&
      matchups.enemies.exists(e =>
        e.unitClass.isDetector
        && e.aliveAndComplete
        && (e.unitClass.canMove || e.pixelDistanceFast(unit) < 11.0 * 32.0)
        && e.pixelDistanceFast(unit) < 13.0 * 32.0)
    })
  }
  
  override protected def perform(unit: FriendlyUnitInfo) {
    Potshot.delegate(unit)
    Retreat.delegate(unit)
  }
}
