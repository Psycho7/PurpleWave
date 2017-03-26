package Micro.Heuristics.Movement

import Debugging.Visualization.Colors
import Micro.Heuristics.HeuristicMath
import Micro.Heuristics.TileHeuristics.TileHeuristic
import Micro.Intentions.Intention
import bwapi.{Color, TilePosition}

class WeightedMovementHeuristic(
  val heuristic : TileHeuristic,
  val weight    : Double,
  val color     : Color = Colors.DefaultGray) {
  
  def weigh(intent:Intention, candidate:TilePosition):Double = {
    
    val result =
      if (weight == 0)
        1.0
      else
        Math.pow(HeuristicMath.normalize(heuristic.evaluate(intent, candidate)), weight)
    
    return result
  }
  
}