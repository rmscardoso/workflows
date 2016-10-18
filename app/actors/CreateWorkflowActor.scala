package actors

import akka.actor._
import javax.inject.Inject

import play.api.db._
import anorm._

object CreateWorkflowActor {
  def props = Props[CreateWorkflowActor]

  case class CreateWorkflow(numberOfSteps: Int)
}

class CreateWorkflowActor @Inject()(db: Database) extends Actor {

  import CreateWorkflowActor._

  def createWorkflow(numberOfSteps: Int): Option[Long] = {
    db.withConnection { implicit c =>
      SQL("insert into workflow(number_of_steps) values ({numberOfSteps})")
        .on('numberOfSteps -> numberOfSteps).executeInsert()
    }
  }

  def receive = {
    case CreateWorkflow(numberOfSteps: Int) =>
      sender() ! createWorkflow(numberOfSteps)
  }
}
