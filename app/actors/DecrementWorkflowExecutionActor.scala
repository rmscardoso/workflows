package actors

import akka.actor._
import javax.inject.Inject

import play.api.db._
import anorm._
import anorm.SqlParser._

object DecrementWorkflowExecutionActor {
  def props = Props[DecrementWorkflowExecutionActor]

  case class DecrementWorkflowExecution(workflowId: Long, workflowExecutionId: Long)
}

class DecrementWorkflowExecutionActor @Inject()(db: Database) extends Actor {

  import DecrementWorkflowExecutionActor._

  def getCurrentSteps(workflowId: Long, workflowExecutionId: Long): Option[Int] = {
    db.withConnection { implicit c =>
      SQL("select current_steps from workflow_execution where id = {workflowExecutionId} and workflow_id = {workflowId}")
        .on('workflowId -> workflowId, 'workflowExecutionId -> workflowExecutionId).as(scalar[Int].singleOpt)
    }
  }

  def decrementWorkflowExecution(workflowExecutionId: Long, currentSteps: Int): Int = {
    db.withConnection { implicit c =>
      SQL("update workflow_execution set current_steps = {currentSteps} where id = {workflowExecutionId}")
        .on('workflowExecutionId -> workflowExecutionId, 'currentSteps -> currentSteps).executeUpdate()
    }
  }

  def receive = {
    case DecrementWorkflowExecution(workflowId, workflowExecutionId) =>
      sender() ! (getCurrentSteps(workflowId, workflowExecutionId) match {
        case Some(currentSteps) => Some(if (currentSteps > 0) decrementWorkflowExecution(workflowExecutionId, currentSteps - 1) else 0)
        case None => None
      })
  }
}
