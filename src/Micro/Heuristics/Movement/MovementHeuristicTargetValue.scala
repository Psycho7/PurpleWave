package Micro.Heuristics.Movement

import Mathematics.Heuristics.HeuristicMathMultiplicative
import Mathematics.Pixels.Pixel
import Micro.Task.ExecutionState

object MovementHeuristicTargetValue extends MovementHeuristic {
  
  override def evaluate(state: ExecutionState, candidate: Pixel): Double = {
    
    if ( ! state.canAttack)     return HeuristicMathMultiplicative.default
    if ( state.targets.isEmpty) return HeuristicMathMultiplicative.default
    
    state.targetValues
      .filter(pair =>
        pair._1.pixelDistanceFast(candidate) < state.unit.pixelRangeAgainstFromCenter(pair._1))
      .map(_._2)
      .max
  }
}