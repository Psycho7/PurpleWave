package Planning.Plans.Protoss.GamePlans.Specialty

import Lifecycle.With
import Macro.BuildRequests.RequestAnother
import Planning.Composition.UnitCounters.UnitCountExactly
import Planning.Composition.UnitMatchers.UnitMatchWorkers
import Planning.Composition.UnitPreferences.UnitPreferClose
import Planning.Plans.Army.AttackWithWorkers
import Planning.Plans.Compound.{And, _}
import Planning.Plans.Macro.Automatic.{Gather, TrainContinuously}
import Planning.Plans.Macro.BuildOrders.{Build, FollowBuildOrder}
import Planning.Plans.Macro.Milestones.{EnemyUnitsAtLeast, EnemyUnitsAtMost, UnitsAtLeast}
import Planning.Plans.Protoss.GamePlans.Standard.ProtossStandardGamePlan
import ProxyBwapi.Races.{Protoss, Zerg}

class WorkerRush extends Trigger {
  
  private class ExecuteRush extends Parallel(
    new If(
      new Check(() => With.self.supplyUsed == With.self.supplyTotal),
      new Build(RequestAnother(1, With.self.supplyClass))
    ),
    new TrainContinuously(With.self.workerClass),
    new FollowBuildOrder,
    new Trigger(
      new UnitsAtLeast(5, UnitMatchWorkers, complete = true),
      new Gather {
        workers.unitCounter.set(UnitCountExactly(1))
        workers.unitPreference.set(UnitPreferClose(With.geography.home.pixelCenter))
        //workers.interruptable.set(false) // Note that this means we can't build more workers until another Probe spawns. But that also means Probes aren't dying.
      }),
    new AttackWithWorkers)
 
  private class TimeToEndTheRush extends And(
    new EnemyUnitsAtMost(1, UnitMatchWorkers),
    new Or(
      new EnemyUnitsAtLeast(1, Protoss.PhotonCannon, complete = true),
      new EnemyUnitsAtLeast(1, Zerg.SunkenColony, complete = true)))
  
  
  trigger.set(new TimeToEndTheRush)
  before.set(new ExecuteRush)
  after.set(new ProtossStandardGamePlan)
}