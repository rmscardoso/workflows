# workflows
REST web service capable of handling thousands of concurrent workflow executions.

I have implemented with Play framework.


To run the workflows in a docker (You need to have the docker installed):

    sbt docker:publishLocal

    docker run -p 9000:9000 workflows:1.0