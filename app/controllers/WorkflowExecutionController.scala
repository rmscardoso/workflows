package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._

import actors.CreateWorkflowExecutionActor.CreateWorkflowExecution
import actors.DecrementWorkflowExecutionActor.DecrementWorkflowExecution
import actors.IsFinishedWorkflowExecutionActor.IsFinishedWorkflowExecution
import actors.RemoveWorkflowExecutionsActor.RemoveWorkflowExecutions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.pattern.ask
import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout

@Singleton
class WorkflowExecutionController @Inject()(system: ActorSystem,
                                            @Named("create-workflow-execution-actor") createWorkflowExecutionActor: ActorRef,
                                            @Named("decrement-workflow-execution-actor") decrementWorkflowExecutionActor: ActorRef,
                                            @Named("is-finished-workflow-execution-actor") isFinishedWorkflowExecutionActor: ActorRef,
                                            @Named("remove-workflow-executions-actor") removeWorkflowExecutionsActor: ActorRef)
                                           (implicit ec: ExecutionContext) extends Controller {

  implicit val timeout: Timeout = 5.seconds

  system.scheduler.schedule(0.minute, 1.minute, removeWorkflowExecutionsActor, RemoveWorkflowExecutions)

  def addWorkflowExecution(workflowId: Long) = Action.async {
    (createWorkflowExecutionActor ? CreateWorkflowExecution(workflowId)).mapTo[Option[Long]].map(_ match {
      case Some(id) => Created(Json.obj("workflow_execution_id" -> id.toString))
      case None => NotFound
    })
  }

  def decrementWorkflowExecution(workflowId: Long, workflowExecutionId: Long) = Action.async {
    (decrementWorkflowExecutionActor ? DecrementWorkflowExecution(workflowId, workflowExecutionId)).mapTo[Option[Int]].map(_ match {
      case Some(n) => if (n == 1) NoContent else BadRequest
      case None => NotFound
    })
  }

  def isFinishedWorkflowExecution(workflowId: Long, workflowExecutionId: Long) = Action.async {
    (isFinishedWorkflowExecutionActor ? IsFinishedWorkflowExecution(workflowId, workflowExecutionId)).mapTo[Option[Boolean]].map(_ match {
      case Some(isFinished) => Ok(Json.obj("finished" -> isFinished))
      case None => NotFound
    })
  }
}