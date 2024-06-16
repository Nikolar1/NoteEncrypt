# NoteEncrypt

> **Warning:** Server is a proof of concept it does not encrypt notes it keeps

An android app for taking and keeping encrypted notes as well as exchanging these notes with the server.

## Getting Started

Use the provided apk to install the app on an android device. 
To be able to exchange notes with the server, the server must be started somewhere and its ip address should be inserted into the app.

The server is dockerized, so it can easily be run using docker.

### Starting the app
To run the server you will need docker and docker-compose. In the project root folder run the following command:

	docker-compose up --build

The app will start and be accessible at `http://localhost:8085`.


> **Note:** Make sure that docker engine is running before executing the command.