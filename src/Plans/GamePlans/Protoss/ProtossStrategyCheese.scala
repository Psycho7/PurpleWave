package Plans.GamePlans.Protoss

import Plans.GamePlans.Protoss.Proxy.ProtossRushWithProxyZealots
import Plans.Generic.Compound.PlanCompleteAllInParallel
import Plans.Generic.Macro.PlanGatherMinerals

class ProtossStrategyCheese extends PlanCompleteAllInParallel {
  
  kids = List(
    new ProtossRushWithProxyZealots(),
    new PlanGatherMinerals()
  )
}