package Planning.Plans.Macro.Automatic

import Macro.BuildRequests.RequestUnitAtLeast
import Planning.Plan
import ProxyBwapi.UnitClass.UnitClass
import Startup.With

class TrainContinuously(unitClass: UnitClass) extends Plan {
  
  override def onFrame() {
    if ( ! canBuild) return
    
    With.scheduler.request(this, List(
      new RequestUnitAtLeast(
        Math.min(
          maxDesirable,
          buildCapacity + With.units.ours.count(unit => unit.alive && unit.complete && unit.unitClass == unitClass)),
        unitClass)))
  }
  
  protected def canBuild:Boolean = {
    //TODO: Do we trust this?
    With.game.canMake(unitClass.baseType)
  }
  
  protected def buildCapacity:Int = {
    List(
      With.units.ours.count(_.unitClass == unitClass.whatBuilds._1)
      //if (unitClass.mineralPrice <= 0) Int.MaxValue else With.self.minerals / unitClass.mineralPrice,
      //if (unitClass.gasPrice     <= 0) Int.MaxValue else With.self.minerals / unitClass.gasPrice
    ).min
  }
  
  protected def maxDesirable:Int = {
    Int.MaxValue
  }
  
}