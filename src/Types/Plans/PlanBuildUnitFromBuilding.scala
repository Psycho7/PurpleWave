package Types.Plans

import Startup.With
import Types.Tactics.Tactic
import bwapi.UnitType

class PlanBuildUnitFromBuilding(
  val builder:UnitType,
  val product:UnitType)
    extends Plan {
  
  var _builder:Option[bwapi.Unit] = None
  var _product:Option[bwapi.Unit] = None
  
  override def execute(): Iterable[Tactic] = {
    With.recruiter.
  }
  
}
