package actors

import akka.actor._
import javax.inject.Inject

import play.api.db._
import anorm._

object RemoveWorkflowExecutionsActor {
  def props = Props[RemoveWorkflowExecutionsActor]

  case class RemoveWorkflowExecutions()
}

class RemoveWorkflowExecutionsActor @Inject()(db: Database) extends Actor {

  import RemoveWorkflowExecutionsActor._

  def removeWorkflowExecutions(): Int = {
    db.withConnection { implicit c =>
      SQL("delete from workflow_execution where current_steps = 0 or creation_date <= timestampadd(minute, -1, now());").executeUpdate()
    }
  }

  def receive = {
    case RemoveWorkflowExecutions => removeWorkflowExecutions()
  }
}
