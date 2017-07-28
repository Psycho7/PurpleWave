package Micro.Heuristics.Targeting

import Mathematics.Heuristics.HeuristicMathMultiplicative
import ProxyBwapi.Races.{Protoss, Terran}
import ProxyBwapi.UnitInfo.{FriendlyUnitInfo, UnitInfo}

object TargetHeuristicDetectors extends TargetHeuristic{
  
  override def evaluate(unit: FriendlyUnitInfo, candidate: UnitInfo): Double = {
    
    if ( ! unit.cloaked) return HeuristicMathMultiplicative.default
    
    val detects = (
      candidate.unitClass.isDetector
      ||  (candidate.constructing && candidate.target.exists(_.unitClass.isDetector))
      ||  candidate.is(Terran.Comsat)
      ||  candidate.is(Terran.EngineeringBay)
      ||  candidate.is(Protoss.Forge)
      ||  candidate.is(Protoss.Observatory)
      ||  candidate.is(Protoss.RoboticsFacility))
    
    HeuristicMathMultiplicative.fromBoolean(detects)
  }
}
