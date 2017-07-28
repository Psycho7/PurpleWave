package Micro.Heuristics.Targeting

import Micro.Execution.ActionState
import ProxyBwapi.UnitInfo.UnitInfo

object TargetHeuristicDistance extends TargetHeuristic {
  
  override def evaluate(state: ActionState, candidate: UnitInfo): Double = {
    1.0 + state.unit.framesToGetInRange(candidate)
  }
}
