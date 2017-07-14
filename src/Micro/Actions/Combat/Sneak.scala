package Micro.Actions.Combat

import Micro.Actions.Action
import Micro.Execution.ActionState

object Sneak extends Action {
  
  override protected def allowed(state: ActionState): Boolean = {
    state.unit.cloaked &&
    state.threats.nonEmpty &&
    state.enemies.exists(e => e.unitClass.isDetector)
  }
  
  override protected def perform(state: ActionState) {
    Retreat.delegate(state)
  }
}