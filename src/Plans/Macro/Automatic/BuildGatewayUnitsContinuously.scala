package Plans.Macro.Automatic

import Plans.Macro.Build.TrainUnit
import Startup.With
import bwapi.UnitType

class BuildGatewayUnitsContinuously extends AbstractBuildContinuously[TrainUnit] {
  
  override def _buildPlan:TrainUnit = {
    new TrainUnit(
      if (With.ourUnits.exists(_.getType == UnitType.Protoss_Templar_Archives)
        && With.game.self.gas >= UnitType.Protoss_Dark_Templar.gasPrice
        && With.ourUnits.filter(_.getType == UnitType.Protoss_Dark_Templar).size < 2) {
        UnitType.Protoss_Dark_Templar
      }
      else if (With.ourUnits.exists(_.getType == UnitType.Protoss_Cybernetics_Core)
        && With.game.self.gas >= UnitType.Protoss_Dragoon.gasPrice
        && (With.memory.knownEnemyUnits.exists(enemy => enemy.getType.isFlyer && enemy.getType.canAttack)
          || With.ourUnits.filter(_.getType == UnitType.Protoss_Dragoon).size * 2 <
             With.ourUnits.filter(_.getType == UnitType.Protoss_Zealot).size * 3)) {
        UnitType.Protoss_Dragoon
      }
      else {
        UnitType.Protoss_Zealot
      })
  }
  
  override def _additionalPlansRequired:Int = {
    Math.max(0, With.ourUnits.filter(_.getType == UnitType.Protoss_Gateway).size - _currentBuilds.size)
  }
}