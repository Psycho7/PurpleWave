package Micro.Actions.Combat.Spells

import ProxyBwapi.Races.{Protoss, Terran}
import ProxyBwapi.Techs.Tech
import ProxyBwapi.UnitClass.UnitClass
import ProxyBwapi.UnitInfo._

object Yamato extends TargetedSpell {
  
  override protected def casterClass    : UnitClass = Terran.Battlecruiser
  override protected def tech           : Tech      = Terran.Yamato
  override protected def aoe            : Boolean   = false
  override protected def castRangeTiles : Int       = 10
  override protected def thresholdValue : Double    = Protoss.Shuttle.subjectiveValue
  
  override protected def valueTarget(target: UnitInfo): Double = {
    if ( ! target.isEnemy) return 0.0
  
    val yamatoDamage    = 260.0
    val castFrames      = 72.0
    val antiAirBonus    = if (target.unitClass.attacksAir) 3.0 else 1.0
    val hitPoints       = target.totalHealth - target.matchups.dpfReceivingCurrently * castFrames
    val dealtDamage     = Math.min(yamatoDamage, hitPoints)
    val dealtValue      = dealtDamage / target.unitClass.subjectiveValue
    val output          = antiAirBonus * dealtValue
    output
  }
}
