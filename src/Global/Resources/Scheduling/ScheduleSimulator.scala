package Global.Resources.Scheduling

import Startup.With
import Types.Buildable.Buildable

import scala.collection.mutable

object ScheduleSimulator {

  def simulate:ScheduleSimulationResult = {
    val currentState = ScheduleSimulationStateBuilder.build
    var buildableQueue = With.scheduler.queue.toArray
    val eventsPlanned = new mutable.PriorityQueue[SimulationEvent]
    val buildablesImpossible = new mutable.ListBuffer[Buildable]
  
    var index = 0
    while (index < buildableQueue.size) {
      
      val nextBuildable = buildableQueue(index)
      val build = currentState.build(nextBuildable)
  
      if (build.buildable.isDefined) {
        eventsPlanned += currentState.startBuilding(nextBuildable)
        index += 1
      }
      else if (build.unmetPrerequisites.nonEmpty) {
        buildableQueue = insertAt(buildableQueue, build.unmetPrerequisites, index)
      }
      else {
        buildablesImpossible += nextBuildable
        index += 1
      }
    }
  
    new ScheduleSimulationResult(
      eventsPlanned,
      buildablesImpossible)
  }
  
  def insertAt(queue:Array[Buildable], insertions:Iterable[Buildable], index:Int):Array[Buildable] =
    queue.take(index) ++ insertions ++ queue.drop(index)
}
