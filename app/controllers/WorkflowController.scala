package controllers

import play.api.mvc._
import play.api.libs.json._
import javax.inject._

import actors.CreateWorkflowActor.CreateWorkflow

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.actor.ActorRef
import akka.util.Timeout

@Singleton
class WorkflowController @Inject()(@Named("create-workflow-actor") createWorkflowActor: ActorRef)
                                  (implicit ec: ExecutionContext) extends Controller {

  implicit val timeout: Timeout = 5.seconds

  def addWorkflow() = Action.async(BodyParsers.parse.json) { implicit request =>
    val numberOfStepsOption = (request.body \ "number_of_steps").validate[Int]

    numberOfStepsOption match {
      case n: JsSuccess[Int] => (createWorkflowActor ? CreateWorkflow(n.get)).mapTo[Option[Long]].map (_ match {
        case Some(id) => Created(Json.obj("workflow_id" -> id.toString))
        case None => BadRequest
      })
      case e: JsError => Future.successful(BadRequest(s"Errors: ${JsError.toJson(e).toString}"))
    }
  }
}