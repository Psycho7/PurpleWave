package Information.Geography.Calculations

import Information.Geography.Pathfinding.GroundPathFinder
import Information.Geography.Types.Zone
import Lifecycle.With
import ProxyBwapi.Players.Players
import ProxyBwapi.Races.Terran

object ZoneUpdater {
  
  def update() {
    With.geography.zones.foreach(updateZone)
  
    // TODO: We only want to do this once! The current check is a hack
    if (With.geography.ourNatural.isEmpty) {
      With.geography.bases
        .filter(_.isStartLocation)
        .foreach(startLocationBase =>
          With.geography.bases
            .filter(otherBase => otherBase != startLocationBase && otherBase.gas.nonEmpty)
            .toVector
            .sortBy(_.zone.distancePixels(startLocationBase.zone))
            .headOption
            .foreach(_.isNaturalOf = Some(startLocationBase)))
    }
  
    val plannedBases = With.groundskeeper.proposalPlacements
      .flatMap(placement => placement._2.tile)
      .flatMap(tile => if (tile.zone.bases.isEmpty) None else Some(tile.zone.bases.minBy(_.heart.tileDistanceFast(tile))))
      .filter(_.owner.isNeutral)
      .toSet
  
    With.geography.zones.foreach(zone =>  { zone.owner = With.neutral; zone.contested = false })
    val playerBorders = Players.all
      .filterNot(_.isNeutral)
      .map(player => (player, BorderFinder.claimedZones(player)))
      .toMap
    
    playerBorders.foreach(pair => pair._2.foreach(zone => {
      if ( ! zone.owner.isNeutral || zone.contested) {
        zone.owner = With.neutral
        zone.contested = true
      }
      else {
        zone.owner = pair._1
      }
    }))
  }
  
  def updateZone(zone: Zone) {
  
    zone.units = With.units.all.filter(_.zone == zone)
    
    zone.bases.foreach(BaseUpdater.updateBase)
  
    val exitBuildings = zone.exit.map(exit =>
      With.units
        .inTileRadius(exit.centerPixel.tileIncluding, 10)
        .filter(u => u.unitClass.isBuilding && ! u.flying))
      .getOrElse(List.empty)
  
    lazy val canaryTileInside   = zone.tiles.find(With.grids.walkable.get)
    lazy val canaryTileOutside  = zone.exit.map(_.otherSideof(zone)).flatMap(_.tiles.find(With.grids.walkable.get))
    zone.walledIn =
      exitBuildings.count(_.is(Terran.SupplyDepot)) >= 1 &&
        exitBuildings.count(_.is(Terran.Barracks))  >= 1 &&
        canaryTileInside.exists(tileInside =>
          canaryTileOutside.exists(tileOutside =>
            ! GroundPathFinder.manhattanGroundDistanceThroughObstacles(
              tileInside,
              tileOutside,
              obstacles = Set.empty,
              maximumDistance = 100).pathExists))
  }
  
  
}
