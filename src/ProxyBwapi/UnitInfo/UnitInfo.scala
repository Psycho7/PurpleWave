package ProxyBwapi.UnitInfo

import Lifecycle.With
import Mathematics.Pixels.{Pixel, Tile, TileRectangle}
import Performance.Caching.CacheFrame
import ProxyBwapi.Engine.Damage
import ProxyBwapi.Races.{Protoss, Terran, Zerg}
import ProxyBwapi.UnitClass.UnitClass
import bwapi._

abstract class UnitInfo (base:bwapi.Unit) extends UnitProxy(base) {
  
  def friendly  : Option[FriendlyUnitInfo]  = None
  def foreign   : Option[ForeignUnitInfo]   = None
  
  override def toString:String = unitClass.toString + " " + tileIncludingCenter.toString
  
  def is(unitClasses:UnitClass*):Boolean = unitClasses.exists(_ == unitClass)
  
  ////////////
  // Health //
  ////////////
  
  def mineralsLeft  : Int = if (unitClass.isMinerals) resourcesLeft else 0
  def gasLeft       : Int = if (unitClass.isGas)      resourcesLeft else 0
  
  def wounded:Boolean = totalHealth < Math.min(30, unitClass.maxTotalHealth/3)
  
  ///////////////
  // Economics //
  ///////////////
  
  def subjectiveValue: Int = unitClass.subjectiveValue + scarabs * Protoss.Scarab.subjectiveValue + interceptors * Protoss.Interceptor.subjectiveValue
  
  //////////////
  // Geometry //
  //////////////
  
  def x: Int = pixelCenter.x
  def y: Int = pixelCenter.y
  
  def tileIncludingCenter: Tile = pixelCenter.tileIncluding
  def tileArea: TileRectangle = unitClass.tileArea.add(tileTopLeft)
  
  def pixelRangeAir: Double = pixelRangeAirCache.get
  private val pixelRangeAirCache = new CacheFrame(() =>
    unitClass.airRange +
      (if (is(Terran.Marine)    && player.getUpgradeLevel(Terran.MarineRange)     > 0)  32.0 else 0.0) +
      (if (is(Terran.Goliath)   && player.getUpgradeLevel(Terran.GoliathAirRange) > 0)  96.0 else 0.0) +
      (if (is(Protoss.Dragoon)  && player.getUpgradeLevel(Protoss.DragoonRange)   > 0)  64.0 else 0.0) +
      (if (is(Zerg.Hydralisk)   && player.getUpgradeLevel(Zerg.HydraliskRange)    > 0)  32.0 else 0.0) )
  
  def pixelRangeGround: Double = pixelRangeGroundCache.get
  private val pixelRangeGroundCache = new CacheFrame(() =>
    unitClass.groundRange +
      (if (is(Terran.Marine)    && player.getUpgradeLevel(Terran.MarineRange)     > 0)  32.0 else 0.0) +
      (if (is(Protoss.Dragoon)  && player.getUpgradeLevel(Protoss.DragoonRange)   > 0)  64.0 else 0.0) +
      (if (is(Zerg.Hydralisk)   && player.getUpgradeLevel(Zerg.HydraliskRange)    > 0)  32.0 else 0.0))
  
  def pixelRangeMax:Double = Math.max(pixelRangeAir, pixelRangeGround)
  
  def canTraverse           (tile:        Tile)       : Boolean = flying || With.grids.walkable.get(tile)
  def pixelsFromEdgeSlow    (otherUnit:   UnitInfo)   : Double  = pixelDistanceSlow(otherUnit) - unitClass.radialHypotenuse - otherUnit.unitClass.radialHypotenuse
  def pixelsFromEdgeFast    (otherUnit:   UnitInfo)   : Double  = pixelDistanceFast(otherUnit) - unitClass.radialHypotenuse - otherUnit.unitClass.radialHypotenuse
  def pixelDistanceSlow     (otherPixel:  Pixel)      : Double  = pixelCenter.pixelDistanceSlow(otherPixel)
  def pixelDistanceSlow     (otherUnit:   UnitInfo)   : Double  = pixelDistanceSlow(otherUnit.pixelCenter)
  def pixelDistanceFast     (otherPixel:  Pixel)      : Double  = pixelCenter.pixelDistanceFast(otherPixel)
  def pixelDistanceFast     (otherUnit:   UnitInfo)   : Double  = pixelDistanceFast(otherUnit.pixelCenter)
  def pixelDistanceSquared  (otherUnit:   UnitInfo)   : Double  = pixelDistanceSquared(otherUnit.pixelCenter)
  def pixelDistanceSquared  (otherPixel:  Pixel)      : Double  = pixelCenter.pixelDistanceSquared(otherPixel)
  def travelPixels          (destination: Pixel)      : Double  = travelPixels(pixelCenter, destination)
  def travelPixels          (destination: Tile)       : Double  = travelPixels(tileIncludingCenter, destination)
  def travelPixels          (from: Pixel, to: Pixel)  : Double  = travelPixels(from.tileIncluding, to.tileIncluding)
  def travelPixels          (from: Tile,  to: Tile)   : Double  =
    if (flying)
      from.pixelCenter.pixelDistanceSlow(to.pixelCenter)
    else
      With.paths.groundPixels(from, to)
  
  def canMoveThisFrame:Boolean = unitClass.canMove && canDoAnythingThisFrame && ! burrowed
  
  def topSpeed:Double = topSpeedCache.get
  private val topSpeedCache = new CacheFrame(() =>
    stimBonus * (
    unitClass.topSpeed + (if (
      (is(Terran.Vulture)   && player.getUpgradeLevel(Terran.VultureSpeed)    > 0) ||
      (is(Protoss.Observer) && player.getUpgradeLevel(Protoss.ObserverSpeed)  > 0) ||
      (is(Protoss.Scout)    && player.getUpgradeLevel(Protoss.ScoutSpeed)     > 0) ||
      (is(Protoss.Shuttle)  && player.getUpgradeLevel(Protoss.ShuttleSpeed)   > 0) ||
      (is(Protoss.Zealot)   && player.getUpgradeLevel(Protoss.ZealotSpeed)    > 0) ||
      (is(Zerg.Overlord)    && player.getUpgradeLevel(Zerg.OverlordSpeed)     > 0) ||
      (is(Zerg.Zergling)    && player.getUpgradeLevel(Zerg.ZerglingSpeed)     > 0) ||
      (is(Zerg.Hydralisk)   && player.getUpgradeLevel(Zerg.HydraliskSpeed)    > 0) ||
      (is(Zerg.Ultralisk)   && player.getUpgradeLevel(Zerg.UltraliskSpeed)    > 0))
      unitClass.topSpeed/2.0 else 0.0)))
  
  def project(framesToLookAhead:Int):Pixel = pixelCenter.add((velocityX * framesToLookAhead).toInt, (velocityY * framesToLookAhead).toInt)
  
  def inTileRadius  (tiles:Int)  : Traversable[UnitInfo] = With.units.inTileRadius(tileIncludingCenter, tiles)
  def inPixelRadius (pixels:Int) : Traversable[UnitInfo] = With.units.inPixelRadius(pixelCenter, pixels)
  
  ////////////
  // Combat //
  ////////////
  
  def melee:Boolean = unitClass.maxAirGroundRange <= 32 * 2
  
  //TODO: Account for upgrades. Make sure to handle case where unit has no armor upgrades
  def armorHealth: Int = unitClass.armor // if (player.getUpgradeLevel(unitClass.armorUpgrade)
  def armorShield: Int = 0 //if(unitClass.maxShields > 0) player.getUpgradeLevel(Protoss.Shields) else 0
  
  def totalHealth: Int = hitPoints + shieldPoints + defensiveMatrixPoints
  def fractionalHealth:Double = totalHealth.toDouble / unitClass.maxTotalHealth
  
  def stimBonus:Int = if (stimmed) 2 else 1
  
  def airDps    : Double = stimBonus * unitClass.airDps
  def groundDps : Double = stimBonus * unitClass.groundDps
  
  def attacksAgainstGround: Int = {
    var output = unitClass.groundDamageFactorRaw * unitClass.maxGroundHitsRaw
    //if() is just to avoid slow is() calls
    if (output == 0) {
      if (is(Protoss.Reaver)) output = 1
      //TODO: Carrier = N attacks, but not Nx damage
      //TODO: Bunker = 4 attacks, but not 4x damage
    }
    output
  }
    
  def attacksAgainstAir: Int = unitClass.airDamageFactorRaw    * unitClass.maxAirHitsRaw
  
  def cooldownLeft                          : Int         = Math.max(airCooldownLeft, groundCooldownLeft)
  def cooldownLeftAgainst (enemy:UnitInfo)  : Int         =  if (enemy.flying) airCooldownLeft                else groundCooldownLeft
  def cooldownAgainst     (enemy:UnitInfo)  : Int         = (if (enemy.flying) unitClass.airDamageCooldown    else unitClass.groundDamageCooldown) / stimBonus
  def rangeAgainst        (enemy:UnitInfo)  : Double      =  if (enemy.flying) pixelRangeAir                  else pixelRangeGround
  def damageTypeAgainst   (enemy:UnitInfo)  : DamageType  =  if (enemy.flying) unitClass.airDamageTypeRaw     else unitClass.groundDamageTypeRaw
  def attacksAgainst      (enemy:UnitInfo)  : Int         =  if (enemy.flying) attacksAgainstAir              else attacksAgainstGround
  
  def damageScaleAgainst(enemy:UnitInfo): Double =
    if (enemy.flying && airDps > 0)
      if (enemy.shieldPoints > 5) 1.0
      else Damage.scaleBySize(unitClass.airDamageTypeRaw, enemy.unitClass.size)
    else if (groundDps > 0)
      if (enemy.shieldPoints > 5) 1.0
      else Damage.scaleBySize(unitClass.groundDamageTypeRaw, enemy.unitClass.size)
    else
      0.0
  
  def damageAgainst(enemy:UnitInfo, enemyShields:Int = 0) : Int = {
    val hits = attacksAgainst(enemy)
    val damageOnHit = if (enemy.flying) unitClass.effectiveAirDamage else unitClass.effectiveGroundDamage
    val damageScale = damageScaleAgainst(enemy)
    val damageToShields = if (enemy.shieldPoints > 0) Math.max(0, Math.min(enemy.shieldPoints, hits * (damageOnHit - enemy.armorShield))) else 0
    val damageToHealth  = Math.max(0, damageScale * (hits * (damageOnHit - enemy.armorHealth) - damageToShields))
    Math.max(1, damageToHealth.toInt + damageToShields)
  }
  
  def dpsAgainst(enemy:UnitInfo): Double = {
    val cooldownVs = cooldownAgainst(enemy)
    if (cooldownVs == 0) return 0.0
    damageAgainst(enemy) * 24.0 / cooldownVs
  }
  
  def canDoAnythingThisFrame:Boolean = canDoAnythingThisFrameCache.get
  private val canDoAnythingThisFrameCache = new CacheFrame(() =>
    alive &&
    complete &&
    ! stasised &&
    ! maelstrommed &&
    ! lockedDown)
  
  def canBeAttackedThisFrame:Boolean = canBeAttackedThisFrameCache.get
  private val canBeAttackedThisFrameCache = new CacheFrame(() =>
      alive &&
      totalHealth > 0 &&
      visible &&
      ! invincible &&
      ! stasised)
  
  def canAttackThisSecond:Boolean = canAttackThisSecondCache.get
  private val canAttackThisSecondCache = new CacheFrame(() =>
    canDoAnythingThisFrame &&
    (
      unitClass.canAttack ||
      (
        ( ! is(Protoss.Carrier) || interceptors > 0) &&
        ( ! is(Protoss.Reaver)  || scarabs > 0) &&
        ( ! is(Zerg.Lurker)     || burrowed)
      )
    ))
  
  def canAttackThisSecond(enemy:UnitInfo):Boolean =
    canAttackThisSecond &&
    enemy.canBeAttackedThisFrame &&
    ! enemy.effectivelyCloaked &&
    (if (enemy.flying) unitClass.attacksAir else unitClass.attacksGround)
  
  def canAttackThisFrame:Boolean = canAttackThisSecond && cooldownLeft < With.latency.framesRemaining
  
  def requiredAttackDelay: Int = {
    // The question:
    // If we order this unit to attack, how many frames after issuing an order (and waiting on latency) before it can attack again?
    //
    // This is also important for preventing the Goon Stop bug. See BehaviorDragoon for details.
    //
    if      (is(Protoss.Dragoon)) 8
    else if (is(Protoss.Carrier)) 48
    else                          4
  }
  
  def pixelImpactTravel   (framesAhead  : Int)  : Double = if (canMoveThisFrame) topSpeed * framesAhead  else 0.0
  def pixelImpactAir      (framesAhead  : Int)  : Double = pixelImpactTravel(framesAhead) + pixelRangeAir
  def pixelImpactGround   (framesAhead  : Int)  : Double = pixelImpactTravel(framesAhead) + pixelRangeGround
  def pixelImpactMax      (framesAhead  : Int)  : Double = Math.max(pixelImpactAir(framesAhead), pixelImpactGround(framesAhead))
  def pixelImpactAgainst  (framesAhead  : Int, enemy:UnitInfo): Double = if (enemy.flying) pixelImpactAir(framesAhead) else pixelImpactGround(framesAhead)
  
  def inRangeToAttackSlow(enemy:UnitInfo):Boolean = pixelsFromEdgeSlow(enemy) <= rangeAgainst(enemy)
  def inRangeToAttackFast(enemy:UnitInfo):Boolean = pixelsFromEdgeFast(enemy) <= rangeAgainst(enemy)
  
  ////////////////
  // Visibility //
  ////////////////
  
  def effectivelyCloaked:Boolean =
    (burrowed || cloaked) && (
      if (isFriendly) ! With.grids.enemyDetection.get(tileIncludingCenter)
      else            ! detected
    )
  
  /////////////
  // Players //
  /////////////
  
  def isOurs     : Boolean = player.isUs
  def isNeutral  : Boolean = player.isNeutral
  def isFriendly : Boolean = player.isAlly || isOurs
  def isEnemy    : Boolean = player.isEnemy
  def isEnemyOf(otherUnit:UnitInfo): Boolean = (isFriendly && otherUnit.isEnemy) || (isEnemy && otherUnit.isFriendly)
}
