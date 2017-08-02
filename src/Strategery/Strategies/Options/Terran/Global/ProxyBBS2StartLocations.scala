package Strategery.Strategies.Options.Terran.Global

import Planning.Plan
import Planning.Plans.Terran.GamePlans.ProxyBBS
import Strategery.Strategies.Strategy
import bwapi.Race

object ProxyBBS2StartLocations  extends Strategy {
  
  override def buildGameplan(): Option[Plan] = { Some(new ProxyBBS) }
  
  override def ourRaces: Iterable[Race] = Vector(Race.Terran)
  
  override def startLocationsMin = 2
  override def startLocationsMax = 2
}
