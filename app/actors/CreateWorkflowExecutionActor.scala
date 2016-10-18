package actors

import akka.actor._
import javax.inject.Inject

import play.api.db._
import anorm._
import anorm.SqlParser._

object CreateWorkflowExecutionActor {
  def props = Props[CreateWorkflowExecutionActor]

  case class CreateWorkflowExecution(workflowId: Long)
}

class CreateWorkflowExecutionActor @Inject()(db: Database) extends Actor {

  import CreateWorkflowExecutionActor._

  def numberOfStepsFromWorkflowId(workflowId: Long): Option[Int] = {
    db.withConnection { implicit c =>
      SQL("select number_of_steps from workflow where id = {workflowId}")
        .on('workflowId -> workflowId).as(scalar[Int].singleOpt)
    }
  }

  def createWorkflowExecution(workflowId: Long, currentSteps: Int): Option[Long] = {
    db.withConnection { implicit c =>
      SQL("insert into workflow_execution(workflow_id, current_steps) values ({workflowId}, {currentSteps})")
        .on('workflowId -> workflowId, 'currentSteps -> currentSteps).executeInsert()
    }
  }

  def receive = {
    case CreateWorkflowExecution(workflowId) =>
      sender() ! (numberOfStepsFromWorkflowId(workflowId) match {
        case Some(numberOfSteps) => createWorkflowExecution(workflowId, numberOfSteps - 1)
        case None => None
      })
  }
}
