package Information.Geography

import Information.Geography.Calculations.{ZoneBuilder, ZoneUpdater}
import Information.Geography.Types.{Base, Edge, Zone}
import Lifecycle.With
import Mathematics.Points.{SpecificPoints, Tile, TileRectangle}
import Performance.Caching.{Cache, CacheFrame, Limiter}
import ProxyBwapi.UnitInfo.UnitInfo

import scala.collection.mutable
import scala.collection.JavaConverters._

class Geography {
  
  lazy val mapArea        : TileRectangle           = TileRectangle(Tile(0, 0), Tile(With.mapWidth, With.mapHeight))
  lazy val allTiles       : Iterable[Tile]          = mapArea.tiles
  lazy val startLocations : Iterable[Tile]          = With.game.getStartLocations.asScala.map(new Tile(_))
  lazy val zones          : Iterable[Zone]          = ZoneBuilder.zones
  lazy val edges          : Iterable[Edge]          = ZoneBuilder.edges
  lazy val bases          : Iterable[Base]          = ZoneBuilder.bases
  def ourZones            : Iterable[Zone]          = ourZonesCache.get
  def ourBases            : Iterable[Base]          = ourBasesCache.get
  def enemyZones          : Iterable[Zone]          = enemyZonesCache.get
  def enemyBases          : Iterable[Base]          = enemyBasesCache.get
  def ourTownHalls        : Iterable[UnitInfo]      = ourTownHallsCache.get
  def ourHarvestingAreas  : Iterable[TileRectangle] = ourHarvestingAreasCache.get
  def ourMain             : Option[Base]            = ourMainCache.get
  def ourNatural          : Option[Base]            = ourNaturalCache.get
  
  private val ourZonesCache           = new CacheFrame(() => zones.filter(_.owner.isUs))
  private val ourBasesCache           = new CacheFrame(() => bases.filter(_.owner.isUs))
  private val enemyZonesCache         = new CacheFrame(() => zones.filter(_.owner.isEnemy))
  private val enemyBasesCache         = new CacheFrame(() => bases.filter(_.owner.isEnemy))
  private val ourTownHallsCache       = new CacheFrame(() => ourBases.flatMap(_.townHall))
  private val ourHarvestingAreasCache = new CacheFrame(() => ourBases.map(_.harvestingArea))
  private val ourMainCache            = new CacheFrame(() => bases.find(_.isOurMain))
  private val ourNaturalCache         = new CacheFrame(() => bases.find(_.isNaturalOf.exists(_.owner.isUs)))
  
  def zoneByTile(tile: Tile): Zone = zoneByTileCache(tile)
  private lazy val zoneByTileCache =
    new mutable.HashMap[Tile, Zone] {
      override def default(key: Tile): Zone = {
        val zone = zones
          .find(_.tiles.contains(key))
          .getOrElse(zones.minBy(_.centroid.tileDistanceSquared(key)))
        put(key, zone)
        zone
      }
    }
  
  def home: Tile = homeCache.get
  private val homeCache = new Cache(5, () =>
    ourBases
      .toVector
      .sortBy( ! _.isStartLocation)
      .headOption
      .map(_.townHallArea.startInclusive)
      .getOrElse(SpecificPoints.tileMiddle))
  
  def ourExposedChokes: Iterable[Edge] =
    With.geography.zones
      .filter(zone => zone.owner.isUs || zone.bases.exists(_.planningToTake))
      .flatten(_.edges)
      .filter(edge => edge.zones.exists( ! _.owner.isUs))
  
  def mostExposedChokes: Vector[Edge] =
    ourExposedChokes
      .toVector
      .sortBy(_.centerPixel.groundPixels(With.intelligence.mostBaselikeEnemyTile))
  
  def update() {
    zoneUpdateLimiter.act()
    bases.filter(base => With.game.isVisible(base.townHallArea.midpoint.bwapi)).foreach(base => base.lastScoutedFrame = With.frame)
  }
  
  private val zoneUpdateLimiter = new Limiter(2, () => ZoneUpdater.update())
}
