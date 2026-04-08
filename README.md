# QuizTrack

QuizTrack is a Java client/server quiz system for the CIT 325 team project. This repository now contains a clean MVP that preserves the original `client` / `server` / `common` package split and demonstrates a full socket-based quiz exchange with a Swing GUI.

## Current MVP

- `server.ServerMain` starts a TCP quiz server.
- `client.ClientMain` launches a Swing login window.
- The client connects to the server over sockets.
- The server sends one sample question per session.
- The client answers through the GUI and sends the response back.
- The server grades the answer and returns a result message.
- English and Spanish static UI text are provided through `ResourceBundle`.

The server rotates through sample multiple-choice, true/false, and short-answer questions on successive client connections so the supported question types are visible during demos.

## Package Layout

- `src/common`: shared models, messages, interfaces, and utility classes
- `src/client`: Swing UI, localization helper, and socket client
- `src/server`: server entry point, request handling, and quiz services
- `resources`: `messages_en.properties` and `messages_es.properties`

## Architecture Notes

- Inheritance:
  - `Question` -> `MCQQuestion`, `TrueFalseQuestion`, `ShortAnswerQuestion`
  - `Message` -> `AuthMessage`, `QuestionMessage`, `AnswerMessage`, `ResultMessage`
  - `User` -> `Student`, `Instructor`
- Interface usage:
  - `RequestHandler` implements `Runnable`
  - `Storable` remains the shared persistence-oriented interface for future storage work
- Separation of concerns:
  - GUI classes handle presentation only
  - `ClientConnection` handles socket I/O on the client
  - `RequestHandler` handles one server session
  - `QuizService` owns sample question selection and grading

## How To Run

Run these commands from the repository root.

### 1. Compile

```bash
javac -d out $(find src -name '*.java')
```

### 2. Start the server

```bash
java -cp out:resources server.ServerMain 8080
```

You should see:

```text
QuizTrack server listening on port 8080
```

### 3. Start the client in a second terminal

```bash
java -cp out:resources client.ClientMain
```

### 4. Use the login screen

- Host: `127.0.0.1`
- Port: `8080`
- Username: any non-empty value
- Password: optional placeholder for now
- Language: English or Spanish

Click `Connect`, answer the question, and the result window will display the grading feedback returned by the server.

## Verified Local Flow

The current MVP was verified by:

- compiling all Java sources successfully with `javac`
- starting `server.ServerMain`
- connecting over a local socket
- receiving a serialized `QuestionMessage`
- submitting an `AnswerMessage`
- receiving a successful `ResultMessage`

## Remaining Extension Points

- AWS sync:
  - `src/server/storage/AwsStorageService.java` is still a stub and should become the real persistence adapter later.
- Translation service:
  - `src/server/service/QuizService.java` includes a TODO where dynamic prompt/feedback translation can be inserted.
- Geolocation gating:
  - `src/server/network/RequestHandler.java` includes a TODO where `GeoService` and `LocationPolicy` checks can be applied before quiz delivery.

## Notes

- Short-answer grading currently uses a simple exact-match comparison so the MVP can stay runnable. This is the main place to extend if richer grading is needed later.
- Resource bundles are loaded from `resources/`, so keep that directory on the runtime classpath.
