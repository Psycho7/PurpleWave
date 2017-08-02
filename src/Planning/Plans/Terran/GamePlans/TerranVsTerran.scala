package Planning.Plans.Terran.GamePlans

import Macro.BuildRequests.{RequestAtLeast, RequestTech, RequestUpgrade}
import Planning.Composition.UnitMatchers.{UnitMatchSiegeTank, UnitMatchWarriors}
import Planning.Plans.Army._
import Planning.Plans.Compound._
import Planning.Plans.Macro.Automatic.{Gather, RequireSufficientSupply, TrainContinuously, TrainWorkersContinuously}
import Planning.Plans.Macro.BuildOrders.{Build, FirstEightMinutes, FollowBuildOrder}
import Planning.Plans.Macro.Expanding.{BuildRefineries, RemoveMineralBlocksAt, RequireMiningBases}
import Planning.Plans.Macro.Milestones.{OnGasBases, OnMiningBases, UnitsAtLeast}
import Planning.Plans.Macro.Upgrades.UpgradeContinuously
import Planning.Plans.Scouting.ScoutAt
import ProxyBwapi.Races.Terran

class TerranVsTerran extends Parallel {
  
  children.set(Vector(
    new RequireMiningBases(1),
    new FirstEightMinutes(
      //1 Fact FE
      new Build(
        RequestAtLeast(1, Terran.CommandCenter),
        RequestAtLeast(9, Terran.SCV),
        RequestAtLeast(1, Terran.SupplyDepot),
        RequestAtLeast(12, Terran.SCV),
        RequestAtLeast(1, Terran.Barracks),
        RequestAtLeast(1, Terran.Refinery),
        RequestAtLeast(15, Terran.SCV),
        RequestAtLeast(2, Terran.SupplyDepot),
        RequestAtLeast(16, Terran.SCV),
        RequestAtLeast(1, Terran.Factory),
        RequestAtLeast(20, Terran.SCV))),
    new If(
      new UnitsAtLeast(1, UnitMatchSiegeTank),
      new RequireMiningBases(2)),
    new If(
      new UnitsAtLeast(12, UnitMatchWarriors),
      new RequireMiningBases(3)),
    new If(
      new UnitsAtLeast(30, UnitMatchWarriors),
      new RequireMiningBases(4)),
    new RequireSufficientSupply,
    new TrainWorkersContinuously,
    new BuildRefineries,
    new TrainContinuously(Terran.Comsat),
    new TrainContinuously(Terran.SiegeTankUnsieged),
    new TrainContinuously(Terran.Valkyrie, 1),
    new TrainContinuously(Terran.Wraith),
    new Build(
      RequestAtLeast(1, Terran.Barracks),
      RequestAtLeast(1, Terran.Factory),
      RequestAtLeast(1, Terran.MachineShop),
      RequestTech(Terran.SiegeMode)),
    new OnMiningBases(2, new Build(
      RequestAtLeast(3, Terran.Factory),
      RequestUpgrade(Terran.VultureSpeed),
      RequestAtLeast(1, Terran.Academy),
      RequestAtLeast(5, Terran.Factory))),
    new OnMiningBases(3, new Build(
      RequestAtLeast(8, Terran.Factory))),
    new OnGasBases(1, new Build(
      RequestAtLeast(1, Terran.MachineShop))),
    new OnGasBases(2, new Build(
      RequestAtLeast(2, Terran.MachineShop),
      RequestAtLeast(1, Terran.Armory))),
    new OnGasBases(3, new Build(
      RequestAtLeast(4, Terran.MachineShop))),
    new OnGasBases(1, new Build(
      RequestAtLeast(1, Terran.Starport))),
    new OnGasBases(3, new Build(
      RequestAtLeast(2, Terran.Starport))),
    new UpgradeContinuously(Terran.MechDamage),
    new TrainContinuously(Terran.Vulture),
    new TrainContinuously(Terran.Marine),
    new RequireMiningBases(3),
    new ScoutAt(14),
    new ControlMap,
    new ConsiderAttacking,
    new DefendChokes,
    new FollowBuildOrder,
    new Scan,
    new RemoveMineralBlocksAt(40),
    new Gather
  ))
}