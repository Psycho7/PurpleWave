package Strategery.Strategies.Protoss.Other

import Planning.Plan
import Planning.Plans.Protoss.GamePlans.Specialty.IslandCarriers
import Planning.Plans.Protoss._
import Strategery.Strategies.Strategy
import bwapi.Race

object ProtossIslandCarriers extends Strategy {
  
  override def buildGameplan(): Option[Plan] = {  Some(new IslandCarriers) }
  
  override def ourRaces: Iterable[Race] = Vector(Race.Protoss)
  override def islandMaps: Boolean = true
  override def groundMaps: Boolean = false
}
