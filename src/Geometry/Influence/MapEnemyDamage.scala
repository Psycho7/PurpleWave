package Geometry.Influence

import Startup.With
import bwapi.{Position, UnitType}

abstract class MapEnemyDamage extends MapDamage {
  override def getUnits:Iterable[(Position, UnitType)] = {
    With.memory.knownEnemyUnits.filter(_.isCompleted).map(unit => (unit.getPosition, unit.getType))
  }
}