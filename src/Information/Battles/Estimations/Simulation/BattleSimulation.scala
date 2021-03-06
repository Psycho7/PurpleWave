package Information.Battles.Estimations.Simulation

import Information.Battles.Estimations.Estimation
import Information.Battles.Types.{Battle, Team}
import Lifecycle.With
import Mathematics.Points.Pixel
import ProxyBwapi.UnitInfo.UnitInfo

class BattleSimulation(val battle: Battle, val weAttack: Boolean, val hypothetical: Boolean = false) {
  
  private def simulacra(team: Team) = team.units.map(Simulacrum(this, _))
  
  val estimation  : Estimation          = new Estimation
  val focus       : Pixel               = battle.focus
  val unitsOurs   : Vector[Simulacrum]  = simulacra(battle.us)
  val unitsEnemy  : Vector[Simulacrum]  = simulacra(battle.enemy)
  val everyone    : Vector[Simulacrum]  = unitsOurs ++ unitsEnemy
  
  val simulacra: Map[UnitInfo, Simulacrum] = (unitsOurs.filter(_.canMove) ++ unitsEnemy).map(simulacrum => (simulacrum.unit, simulacrum)).toMap
  
  var updated = true
  
  def complete: Boolean =
    (   weAttack && estimation.frames > With.configuration.battleEstimationFrames) ||
    ( ! weAttack && estimation.frames > 24 * 5)   ||
    ! updated                                     ||
    unitsOurs.forall(_.dead)                      ||
    unitsEnemy.forall(_.dead)                     ||
    everyone.forall(e => e.dead || ! e.fighting)
  
  def run() {
    while ( ! complete) step()
    cleanup()
  }
  
  def step() {
    updated = false
    estimation.frames += 1
    everyone.foreach(_.step())
    everyone.foreach(_.updateDeath())
  }
  
  def cleanup() {
    estimation.costToUs         = unitsOurs   .map(_.valueReceived).sum
    estimation.costToEnemy      = unitsEnemy  .map(_.valueReceived).sum
    estimation.damageToUs       = unitsOurs   .map(_.damageReceived).sum
    estimation.damageToEnemy    = unitsEnemy  .map(_.damageReceived).sum
    estimation.deathsUs         = unitsOurs   .count(_.dead)
    estimation.deathsEnemy      = unitsEnemy  .count(_.dead)
    estimation.totalUnitsUs     = unitsOurs   .size
    estimation.totalUnitsEnemy  = unitsEnemy  .size
    estimation.reportCards      ++= everyone  .map(simulacrum => (simulacrum.unit, simulacrum.reportCard))
    estimation.simulation       = Some(this)
  }
}
