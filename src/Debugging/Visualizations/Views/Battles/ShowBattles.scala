package Debugging.Visualizations.Views.Battles

import Debugging.Visualizations.Colors
import Debugging.Visualizations.Rendering.DrawMap
import Debugging.Visualizations.Views.View
import Information.Battles.Estimations.Estimation
import Information.Battles.Types.Battle
import Lifecycle.With
import Mathematics.Points.Pixel
import Planning.Yolo

object ShowBattles extends View {
  
  private val tableHeader0  = Pixel(220, 320)
  private val tableHeader1  = tableHeader0.add(125, 0)
  private val tableStart0   = tableHeader0.add(0, 25)
  private val tableStart1   = tableHeader1.add(0, 25)
  private val army0         = Pixel(438, 18)
  private val army1         = Pixel(521, 18)
  private val army2         = Pixel(589, 18)
  private val yolo          = Pixel(310, 230)
  private val tacticsRanks  = Pixel(235, 18)
  
  override def renderScreen() {
    With.game.drawTextScreen(army0.bwapi, "Offense:")
    With.game.drawTextScreen(army1.bwapi, "+" + With.battles.global.estimationAbstractOffense.costToEnemy.toInt)
    With.game.drawTextScreen(army2.bwapi, "-" + With.battles.global.estimationAbstractOffense.costToUs.toInt)
    With.game.drawTextScreen(army0.add(0, 13).bwapi, "Frames since estimation: " + With.framesSince(With.battles.clustering.lastClusterCompletion))
    localBattle.foreach(battle => drawEstimationReport(battle.estimationSimulationAttack))
    if (Yolo.active && With.frame / 24 % 2 == 0) {
      With.game.drawTextScreen(yolo.bwapi, "YOLO")
    }
  }
  
  override def renderMap() {
    localBattle.foreach(battle => drawBattleMap(battle, battle.estimationSimulationAttack))
  }
  
  def localBattle: Option[Battle] = {
    val localBattles = With.battles.local
    if (localBattles.isEmpty)
      None
    else
      Some(localBattles.minBy(battle => battle.focus.pixelDistanceSquared(With.viewport.center)))
  }
  
  private def drawBattleMap(battle: Battle, estimation: Estimation) {
    val ourColor            = With.self.colorDark
    val enemyColor          = With.enemy.colorDark
    val neutralColor        = Colors.White
    val winnerStrengthColor = if (estimation.costToEnemy >= estimation.costToUs) ourColor else enemyColor
    DrawMap.circle  (battle.focus,          8,                      neutralColor)
    DrawMap.circle  (battle.us.vanguard,    8,                      ourColor)
    DrawMap.circle  (battle.enemy.vanguard, 8,                      enemyColor)
    DrawMap.line    (battle.focus,          battle.us.vanguard,     ourColor)
    DrawMap.line    (battle.focus,          battle.enemy.vanguard,  enemyColor)
    With.game.drawCircleMap(battle.focus.bwapi, (battle.us.units ++ battle.enemy.units).map(_.pixelDistanceFast(battle.focus)).max.toInt, neutralColor)
    DrawMap.labelBox(
      Vector(estimation.netValue.toInt.toString),
      battle.focus.add(24, 0),
      drawBackground = true,
      backgroundColor = winnerStrengthColor)
  }
  
  private def drawEstimationReport(estimation: Estimation) {
    With.game.setTextSize(bwapi.Text.Size.Enum.Large)
    With.game.drawTextScreen(tableHeader0.bwapi, With.self.name)
    With.game.drawTextScreen(tableHeader1.bwapi, With.enemy.name)
    With.game.drawTextScreen(tableStart0.bwapi, "+" + estimation.costToEnemy.toInt)
    With.game.drawTextScreen(tableStart1.bwapi, "-" + estimation.costToUs.toInt)
    With.game.setTextSize(bwapi.Text.Size.Enum.Small)
  }
}

