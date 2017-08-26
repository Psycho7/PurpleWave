package Micro.Actions.Combat.Decisionmaking

import Micro.Actions.Action
import Planning.Yolo
import ProxyBwapi.UnitInfo.FriendlyUnitInfo

object FightOrFlight extends Action {
  
  override def allowed(unit: FriendlyUnitInfo): Boolean = {
    unit.canMove
  }
  
  override def perform(unit: FriendlyUnitInfo) {
  
    unit.agent.desireTeam        = 0.0
    unit.agent.desireIndividual  = 0.0
    unit.agent.desireTotal       = 0.0
  
    if (unit.effectivelyCloaked)            { unit.agent.shouldEngage = true;                                   return }
    if (Yolo.active)                        { unit.agent.shouldEngage = true;                                   return }
    if ( ! unit.agent.canFight)             { unit.agent.shouldEngage = false;                                  return }
    if (unit.underStorm)                    { unit.agent.shouldEngage = false;                                  return }
    if (unit.underDisruptionWeb)            { unit.agent.shouldEngage = false;                                  return }
    if (unit.underDarkSwarm)                { unit.agent.shouldEngage = unit.unitClass.unaffectedByDarkSwarm;   return }
    if (unit.flying
      && unit.matchups.threats.forall(_.topSpeed < unit.topSpeed)
      && unit.matchups.ifAt(24).threatsInRange.isEmpty) {
      unit.agent.shouldEngage = true
      return
    }
    
    unit.agent.desireTeam        = unit.battle.map(_.desire).getOrElse(0.0)
    unit.agent.desireIndividual  = unit.battle.flatMap(_.estimationSimulationAttack.reportCards.get(unit).map(_.netValuePerFrame)).getOrElse(0.0)
    unit.agent.desireTotal       = unit.agent.desireTeam + unit.agent.desireIndividual // Vanity metric, for now
    
    // Hysteresis
    val individualCaution           = 0.2
    val individualHysteresis        = 0.2
    val individualThreshold         = individualCaution + (if (unit.agent.shouldEngage) -individualHysteresis else individualHysteresis)
    val motivatedByDoom             = unit.matchups.doomedDiffused && unit.battle.exists(_.estimationSimulationRetreat.reportCards.get(unit).exists(_.dead))
    val motivatedIndividually       = unit.agent.desireIndividual > individualThreshold
    val motivatedCollectively       = unit.agent.desireTeam       > 0.0
    unit.agent.shouldEngage         = motivatedByDoom || motivatedIndividually || motivatedCollectively
  }
}
