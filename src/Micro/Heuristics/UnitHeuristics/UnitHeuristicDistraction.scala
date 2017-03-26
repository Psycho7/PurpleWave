package Micro.Heuristics.UnitHeuristics

import Micro.Heuristics.HeuristicMath
import Micro.Intentions.Intention
import ProxyBwapi.UnitInfo.UnitInfo

object UnitHeuristicDistraction extends UnitHeuristic{
  
  override def evaluate(intent: Intention, candidate: UnitInfo): Double = {
  
    if (intent.destination.isEmpty) return 1.0
  
    HeuristicMath.unboolify(candidate.travelPixels(intent.destination.get) > intent.unit.travelPixels(intent.destination.get))
      
  }
  
}