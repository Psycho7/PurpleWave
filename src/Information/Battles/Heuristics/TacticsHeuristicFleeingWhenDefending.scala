package Information.Battles.Heuristics

import Information.Battles.BattleTypes.Battle
import Information.Battles.TacticsTypes.{Tactics, TacticsOptions}
import Lifecycle.With
import Mathematics.Heuristics.HeuristicMath

object TacticsHeuristicFleeingWhenDefending extends TacticsHeuristic {
  
  override def evaluate(context: Battle, candidate: TacticsOptions): Double = {
    
    if ( ! candidate.has(Tactics.Movement.Flee)) return HeuristicMath.default
    
    if (context.focus.zone.bases.exists(_.owner == With.self))
      3.0 / (Math.max(1.0, With.geography.bases.size))
    else
      HeuristicMath.default
  }
}