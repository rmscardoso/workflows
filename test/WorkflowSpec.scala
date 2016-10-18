import akka.stream.Materializer
import controllers.{WorkflowController, WorkflowExecutionController}
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers._
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.test.FakeRequest


class WorkflowSpec extends PlaySpec with OneAppPerSuite with ScalaFutures {

  implicit lazy val materializer: Materializer = app.materializer

  def workflowController = app.injector.instanceOf(classOf[WorkflowController])

  def workflowExecutionController = app.injector.instanceOf(classOf[WorkflowExecutionController])

  "Workflow" should {

    "add workflow with a number_of_steps and return a json with workflow_id" in {
      val action = workflowController.addWorkflow()

      val request = FakeRequest(POST, "/workflows").withJsonBody(Json.parse("""{ "number_of_steps": 3 }"""))

      val result = call(action, request)

      status(result) mustEqual CREATED

      contentAsString(result) must include ("workflow_id")
    }

    "add workflow with an invalid request" in {
      val action = workflowController.addWorkflow()

      val request = FakeRequest(POST, "/workflows").withJsonBody(Json.parse("""{ "numberOfSteps": 3 }"""))

      val result = call(action, request)

      status(result) mustEqual BAD_REQUEST
    }

    "should add workflow execution with a workflow_id = 1 and return a json with workflow_execution_id" in {
      val action = workflowExecutionController.addWorkflowExecution(1)

      val request = FakeRequest(POST, "/workflows/1/executions")

      val result = call(action, request)

      status(result) mustEqual CREATED

      contentAsString(result) must include ("workflow_execution_id")
    }

    "add workflow execution with a workflow_id invalid" in {
      val action = workflowExecutionController.addWorkflowExecution(2)

      val request = FakeRequest(POST, "/workflows/2/executions")

      val result = call(action, request)

      status(result) mustEqual NOT_FOUND
    }

    "decrement workflow execution by 1" in {
      val action = workflowExecutionController.decrementWorkflowExecution(1, 1)

      val request = FakeRequest(PUT, "/workflows/1/executions/1")

      val result = call(action, request)

      status(result) mustEqual NO_CONTENT
    }

    "decrement workflow execution with a workflow_execution_id invalid" in {
      val action = workflowExecutionController.decrementWorkflowExecution(1, 2)

      val request = FakeRequest(PUT, "/workflows/1/executions/2")

      val result = call(action, request)

      status(result) mustEqual NOT_FOUND
    }

    "is finished the workflow execution = false" in {
      val action = workflowExecutionController.isFinishedWorkflowExecution(1, 1)

      val request = FakeRequest(GET, "/workflows/1/executions/1")

      val result = call(action, request)

      status(result) mustEqual OK

      contentAsString(result) must include ("finished")
      contentAsString(result) must include ("false")
    }

    "decrement workflow execution by 1 again" in {
      val action = workflowExecutionController.decrementWorkflowExecution(1, 1)

      val request = FakeRequest(PUT, "/workflows/1/executions/1")

      val result = call(action, request)

      status(result) mustEqual NO_CONTENT
    }

    "decrement workflow execution by 1 until current steps = 0" in {
      val action = workflowExecutionController.decrementWorkflowExecution(1, 1)

      val request = FakeRequest(PUT, "/workflows/1/executions/1")

      val result = call(action, request)

      status(result) mustEqual BAD_REQUEST
    }

    "is finished the workflow execution = true" in {
      val action = workflowExecutionController.isFinishedWorkflowExecution(1, 1)

      val request = FakeRequest(GET, "/workflows/1/executions/1")

      val result = call(action, request)

      status(result) mustEqual OK

      contentAsString(result) must include ("finished")
      contentAsString(result) must include ("true")
    }

    "is finished the workflow execution with an invalid workflow execution id" in {
      val action = workflowExecutionController.isFinishedWorkflowExecution(1, 2)

      val request = FakeRequest(GET, "/workflows/1/executions/2")

      val result = call(action, request)

      status(result) mustEqual NOT_FOUND
    }

  }
}
