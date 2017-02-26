package Plans.Army

import Types.Intents.Intent
import Plans.Allocation.{LockUnits, LockUnitsNobody}
import Plans.Plan
import Startup.With
import Utilities.Property

class PressureEnemyBaseFulfiller extends Plan {
  
  val fighters = new Property[LockUnits](LockUnitsNobody)
  
  override def getChildren: Iterable[Plan] = { List(fighters.get) }
  override def onFrame() {
    
    if (With.intelligence.mostBaselikeEnemyBuilding.isEmpty) {
      With.logger.warn("Trying to destroy economy without knowing where to go")
      return
    }
  
    fighters.get.onFrame()
    if ( ! fighters.get.isComplete) {
      return
    }
    
    val targetPosition = With.intelligence.mostBaselikeEnemyBuilding.get.position
    fighters.get.units.foreach(unit => With.commander.intend(unit, new Intent(Some(targetPosition))))
  }
}
