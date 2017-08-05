package Debugging.Visualizations.Views.Micro

import Debugging.Visualizations.Colors
import Debugging.Visualizations.Rendering.{DrawMap, DrawScreen}
import Debugging.Visualizations.Views.View
import Lifecycle.With
import Micro.Squads.Squad
import ProxyBwapi.UnitInfo.UnitInfo

object ShowSquads extends View {
  
  lazy val squadColors = Vector(
    Colors.NeonRed,
    Colors.NeonOrange,
    Colors.NeonYellow,
    Colors.NeonGreen,
    Colors.NeonBlue,
    Colors.NeonIndigo,
    Colors.NeonViolet,
    Colors.White)
  
  override def renderMap() {
    With.squads.all.filter(_.recruits.nonEmpty)foreach(renderSquadMap)
  }
  
  def renderSquadMap(squad: Squad) {
    val color = squadColors(squad.hashCode % squadColors.length)
    (squad.recruits ++ squad.enemies).foreach(unit =>
      DrawMap.labelBox(
        Vector(squad.client.toString, squad.goal.toString),
        unit.pixelCenter.add(0, unit.unitClass.height),
        drawBackground = true,
        color))
    
    DrawMap.circle(squad.centroid, squad.recruits.map(_.pixelDistanceSlow(squad.centroid)).max.toInt, color)
  }
  
  override def renderScreen() {
    
    val table =
      With.squads.squadsByPriority.map(squad =>
        Vector(
          squad.client.toString,
          squad.goal.toString,
          squad.centroid.toString,
          enumerateUnits(squad.recruits) + " --> " + enumerateUnits(squad.enemies)))
  
    DrawScreen.table(5, 7 * With.visualization.lineHeightSmall, table)
  }
  def enumerateUnits(units: Iterable[UnitInfo]): String = {
    
    val counts = units
      .map(_.unitClass)
      .groupBy(x => x)
    
    val output = counts
      .toSeq
      .sortBy(-_._2.size)
      .map(p => p._2.size + " " + p._1)
      .mkString(", ")
    
    output
  }
}
