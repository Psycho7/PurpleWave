package Micro.Behaviors
import Micro.Intentions.Intention
import Startup.With

object BehaviorWorker extends Behavior {
  
  def execute(intent: Intention) {
  
    if (intent.toBuild.isDefined) {
      return With.commander.build(intent, intent.toBuild.get, intent.destination.get)
    }
  
    if (intent.toGather.isDefined && intent.unit.enemiesInRange.isEmpty) {
      return With.commander.gather(intent, intent.toGather.get)
    }
    
    BehaviorDefault.execute(intent)
  }
}