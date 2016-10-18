package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors._

class WorkflowModule extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[CreateWorkflowActor]("create-workflow-actor")
    bindActor[CreateWorkflowExecutionActor]("create-workflow-execution-actor")
    bindActor[DecrementWorkflowExecutionActor]("decrement-workflow-execution-actor")
    bindActor[IsFinishedWorkflowExecutionActor]("is-finished-workflow-execution-actor")
    bindActor[RemoveWorkflowExecutionsActor]("remove-workflow-executions-actor")
  }
}