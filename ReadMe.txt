4:27 PM 6/12/2024

Explaination of this repository.

All of the code in this repository was written by Dusty Bedford

(650) 867-8506

Call me, I am looking for work.


Purpose: These are example projects, in a variety of genres.

/Python/ScreenSaver

	This is a screen saver application, written in Python.

This project demonstrates polymorphism where behavior a standard interface
Animates objects in different ways. In a fish tank simulation. This particular example has a wide variety of different objects, each with their own behavior.  All driven by the same animation engine.

While the project is a toy. It demonstrate my attention to detail when coding. And advanced Python coding skills. You will notice that there a definite Z-order with the movement and display of the object in a consistent simulated 3d environment.


/Java+Docker+Newman

This project will allow you to sychronize any set of test threads. Use this for example to get all virtual users to click on a button at the same time, using the code/tool of your choice for example Postman, Selenium, C#, Kotlin, Cypress... you name it. Everything is supported for sychronized testing (stress/longevity/concurrency...) with this tool.

Because all synchronization happens via a server which uses HTTP message with a JSON payload (see the exported Postman collections in the /newman directory).  

Same code base can be used to synchonize PostMan tests (when run with Newman). Postman doesn't natively support this kind of synchronization, with this tool you will not need to switch to using JMeter.

See the postman exports in the newman directory.

The client (virtual user) classes can be employed in any java coding framework.

There is also a TestNG unit test in the codebase, that will verify all basic tests of the servers and clients. Also you can run the newman scripts (first run init, then run 2 of the virtual user scripts). To create tests with dynamic or different number of virtual users (other than 2), edit the postman collections and enviroment files.

Since the project uses JSON Encoded messages to do the synchronization you can take a look at the exported postman scripts to learn how to use this protocol to synchronize any project, irregardless of language (C#, Kotlin... you name it. If you can send an HTTP request, with a JSON payload, you can synchronize the actions of simulated virtual users).

You can even sychronize curl statements.

The stats project will create a spring boot server. This server will be incorporated into a Docker image by the build system (you must have docker installed to build the application and have the build put it into the image this way).

If you build the image, the stats server and the JSONServer will be up and running, whenever you start an instance of the image.

The resulting image will be accessible if you map these ports:

Docker port 8010 - The synchronization server
Docker port 9090 - Spring Boot application to view the status of groups of virtual users.

When you first start the image, using the run command you will need to map the ports

For example

docker run -d -p8100:8010 -p9095:9090 strat

(note 8100 & 9095 would be your preferred ports, 8010 and 9090 are the ports that are used inside the running instance).

There is no limit on the number of virtual users you can run with or the number of synchronized groups, other than your VM limitations.

The unit tests that are part of the build, verify multithreaded virtual users and verifies correct order of operation). It verifies waiting local in the client and forced waiting at the server (for example Postman scripts need this feature, for convenient synchronization). Also group creation and deletion is also tested.

You can view the status of the groups and virtual users at this URL:

http://localhost:9095/

Assuming you started the docker container with the docker run command shown above.

If you are running everything outside of a docker instance, or you are in the docker and have access to a browser you can use:

http://localhost:9090

