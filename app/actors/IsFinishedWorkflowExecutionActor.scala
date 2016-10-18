package actors

import akka.actor._
import javax.inject.Inject

import play.api.db._
import anorm._
import anorm.SqlParser._

object IsFinishedWorkflowExecutionActor {
  def props = Props[IsFinishedWorkflowExecutionActor]

  case class IsFinishedWorkflowExecution(workflowId: Long, workflowExecutionId: Long)
}

class IsFinishedWorkflowExecutionActor @Inject()(db: Database) extends Actor {

  import IsFinishedWorkflowExecutionActor._

  def getCurrentSteps(workflowId: Long, workflowExecutionId: Long): Option[Int] = {
    db.withConnection { implicit c =>
      SQL("select current_steps from workflow_execution where id = {workflowExecutionId} and workflow_id = {workflowId}")
        .on('workflowId -> workflowId, 'workflowExecutionId -> workflowExecutionId).as(scalar[Int].singleOpt)
    }
  }

  def receive = {
    case IsFinishedWorkflowExecution(workflowId, workflowExecutionId) =>
      sender() ! (getCurrentSteps(workflowId, workflowExecutionId) match {
        case Some(currentSteps) => Some(currentSteps == 0)
        case None => None
      })
  }
}
