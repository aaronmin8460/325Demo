# CIT 325 Team Project — QuizTrack (Client/Server Learning System)

QuizTrack is a Java client/server learning system that delivers quiz questions (MCQ / True-False / Short Answer), records responses, and synchronizes student progress through AWS. The system helps students identify weak topics, review performance over time, and prepare for exams through tracked assignments and analytics.

A key project goal is internationalization. QuizTrack is designed to support at least two locales and can optionally provide automatic translation for dynamic quiz content. The planned approach is a hybrid model:

- **Static UI localization** using Java `Locale` + `ResourceBundle`
- **Dynamic content translation** using AWS services for quiz prompts, assignment descriptions, and feedback
- **Optional AI-assisted translation/rewrite** for more natural educational explanations later

---

## Key Features

- Question delivery: multiple-choice, true/false, and short-answer
- Instant feedback: correctness shown for MCQ / T-F
- Short-answer support: can be manually graded first, with optional AI-assisted feedback later
- Tracking & analytics: stores each submission with timestamps and aggregates performance data
- Assignments: group questions into assignments with optional due dates
- Classlists: organize users into classes/sections for targeted assignments
- Proximity-gated servers: optional location-based access within a defined radius
- AWS sync: student/account data and attempt history stored remotely and synchronized
- Internationalization: supports at least two locales for UI text
- Automatic translation: planned support for dynamic question text, assignment instructions, and feedback

---

## Internationalization & Translation Strategy

QuizTrack will use a **hybrid i18n design** so the project satisfies the CIT 325 internationalization requirement without making the whole application dependent on an LLM.

### 1) Static UI Text

Fixed UI text will be localized using Java internationalization tools:

- `Locale`
- `ResourceBundle`
- `.properties` files such as:
  - `messages_en.properties`
  - `messages_es.properties`

This covers text such as:

- Login / Register
- Connect / Disconnect
- Submit / Cancel
- Labels, menus, headings
- Error messages
- Score and feedback headings

### 2) Dynamic Content Translation

Dynamic content may be translated at runtime, including:

- Question prompts
- Answer choices
- Assignment descriptions
- Instructor-written feedback
- Review hints or explanations

Planned implementation:

- Store the original content in a default language
- Translate on the server side
- Cache translated content to avoid repeated requests
- Fall back to the source language if translation is unavailable

### 3) LLM / Bedrock Usage (Optional)

If time permits, we may add an optional AI layer for **smart translation or rewriting**, for example:

- Translate and simplify an explanation
- Make a translated explanation sound more natural
- Preserve quiz formatting while rewriting awkward text

This is optional and not required for the core internationalization feature.

---

## CIT 325 Requirements Mapping

This project is designed to satisfy the CIT 325 team project requirements, including:

- 10+ new classes
- Inheritance relationships across 5+ classes
- Distributed element using sockets (TCP/UDP)
- At least 1 Java interface
- 2+ external interfaces:
  - AWS storage/service integration
  - Maps / geolocation integration
  - Optional translation service integration
- Time classes used
- GUI (Swing/AWT)
- Internationalization (2+ locales)

---

## Architecture (High-Level)

### Client/Server Model

- Client connects to server over sockets
- Server sends a question payload
- Client returns an answer payload
- Server validates answers for MCQ / T-F
- Server stores results and returns feedback
- Data is synchronized to AWS storage for persistence and multi-device access

### Translation Flow

- Client sends preferred locale during login/session setup
- Server checks whether translated content exists
- If translated content is cached, server returns localized content
- If not, server can generate/store a translated version
- Client displays either localized content or the original source text as fallback

This keeps translation logic centralized on the server and prevents duplicate work on every client.

---

## Planned Class Design (Draft)

> Names may change as implementation continues.

### Core Domain

- `User` — account/profile info (name, ID, class membership)
- `Question` — prompt + type + choices + correct answer (if applicable)
- `Answer` — user response + correctness + timestamps
- `Assignment` — collection of questions and optional due dates
- `ClassList` — group of users assigned to a course/section

### Distributed / Networking

- `Server` — listens for connections, dispatches questions, records answers
- `Client` — connects to server, displays question, sends back answer
- `Message` (or `Packet`) — common request/response payload structure

### Location / Time / GUI

- `LocationPolicy` — proximity rules (lat/long + radius)
- `SessionTime` — start/stop times and attempt timestamps using `java.time`
- `GUI` — screens for login, join server, answer question, view results, and assignments

### External Services

- `AwsStorageService` — save/load users, attempts, assignments
- `GeoService` — geolocation / distance calculations
- `TranslationService` — translate dynamic content for requested locales
- `LocalizationManager` — load `ResourceBundle` text for UI components
- `TranslationCache` — store previously translated prompts and feedback

### Interface (Example)

- `Storable` or `SerializablePayload` — ensures objects can be saved/transmitted consistently

---

## Inheritance Plan (Example)

We will implement inheritance to reduce duplication, for example:

- `Question` (base) → `MCQQuestion`, `TrueFalseQuestion`, `ShortAnswerQuestion`
- `Message` (base) → `QuestionMessage`, `AnswerMessage`, `AuthMessage`, `ResultMessage`
- `User` (base) → `Student`, `Instructor`

---

## Repository Structure (Suggested)

```text
/docs          # diagrams, spec, screenshots
/src
  /client      # client-side code
  /server      # server-side code
  /common      # shared models (Question, Answer, Message, etc.)
  /i18n        # properties files for supported locales
  /services    # AWS, geo, translation services
/tests
README.md
```

---

## Translation Scope (Planned)

### Translate with `ResourceBundle`

Use normal Java localization for:

- Buttons
- Labels
- Menus
- Dialog text
- Validation and error messages
- Navigation text

### Translate with Translation Service

Use automatic translation for:

- Quiz prompts
- Assignment instructions
- Explanatory feedback
- Instructor-authored notes
- Optional help/tutorial content

### Do Not Auto-Translate Blindly

These areas may require review or fallback behavior:

- Correct answers for short-answer grading
- Code snippets
- Formula-heavy content
- Proper nouns or course-specific terminology

---

## Team Work Split

To keep the project organized and make sure all CIT 325 requirements are covered, the work is divided by subsystem ownership rather than vague role titles. Each person owns one major area of the project and is responsible for implementation, testing, comments, and integration support for that area.

| Person      | Main Ownership                                   | Key Classes / Features                                                                                                                                                       | Why This Fits                                                             |
| ----------- | ------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| **Bobby**   | **Core Object Design Lead**                      | `User`, `Student`, `Instructor`, `Question`, `MCQQuestion`, `TrueFalseQuestion`, `ShortAnswerQuestion`, `Answer`, interface design, UML                                      | Covers the core class structure, inheritance, and object design           |
| **Drew**    | **Server + Networking Lead**                     | `Server`, socket handling, `Message`, `QuestionMessage`, `AnswerMessage`, `AuthMessage`, `ResultMessage`, protocol design                                                    | Covers the distributed/socket requirement                                 |
| **Neil**    | **GUI / Client Lead**                            | `LoginFrame`, `ConnectFrame`, `QuizFrame`, `ResultFrame`, `AssignmentFrame`, Swing/AWT event handling                                                                        | Covers the graphical interface and demo side                              |
| **Stephen** | **AWS + Data / Assignment Lead**                 | `AwsStorageService`, `Assignment`, `ClassList`, attempt history, syncing, progress tracking                                                                                  | Covers storage, assignments, classlists, and external service integration |
| **Aaron**   | **Internationalization + Translation + QA Lead** | `LocalizationManager`, `TranslationService`, `TranslationCache`, `messages_en.properties`, `messages_es.properties`, `GeoService`, `LocationPolicy`, testing, README cleanup | Covers i18n requirement, second external interface, and final polish      |

### Requirement Coverage by Owner

| Requirement                             | Assigned To |
| --------------------------------------- | ----------- |
| 10+ classes / object design             | Bobby       |
| Inheritance relationships               | Bobby       |
| Distributed element with sockets        | Drew        |
| GUI                                     | Neil        |
| External interface #1 (AWS)             | Stephen     |
| External interface #2 (Geo/Translation) | Aaron       |
| Internationalization (2 locales)        | Aaron       |
| Testing / integration / final cleanup   | Shared      |

### Shared Team Responsibilities

Even though each member has a main ownership area, all team members should also:

- contribute code through their own branch
- comment and document their own work
- test at least one other module
- report their own time accurately
- participate in the final presentation

---

## How to Run (Placeholder)

> We will update this section once the first runnable version exists.

### 1) Start Server

- Run `ServerMain` (binds to configured port)

### 2) Start Client

- Run `ClientMain`
- Connect to server IP + port
- Choose or inherit preferred locale
- Answer questions through the GUI

---

## Project Status

- Protocol design (message types + serialization)
- Basic server loop (accept → send question → receive answer)
- GUI v1 (connect + answer screen)
- AWS persistence
- Assignment + classlist support
- Location gating
- Internationalization with at least two locales
- Automatic translation for dynamic quiz content
- Optional AI-assisted translation/rewrite

---

## UML Class Box Contents (Draft)

> These are the planned class-box contents for the UML diagram.  
> Names may be adjusted slightly during implementation.

### Interface

#### `Storable`

**Methods**

- `+ save() : void`
- `+ load() : void`

---

### Core Domain

#### `User`

**Attributes**

- `- userId : int`
- `- name : String`
- `- email : String`
- `- classMembership : String`

**Methods**

- `+ login() : boolean`
- `+ logout() : void`

#### `Student extends User`

**Attributes**

- `- attempts : List<Answer>`

**Methods**

- `+ submitAnswer() : void`
- `+ viewProgress() : void`

#### `Instructor extends User`

**Attributes**

- `- courses : List<String>`

**Methods**

- `+ createQuestion() : void`
- `+ createAssignment() : void`
- `+ reviewResults() : void`

#### `Question` (abstract)

**Attributes**

- `- questionId : int`
- `- prompt : String`
- `- topic : String`
- `- points : int`

**Methods**

- `+ displayQuestion() : String`

#### `MCQQuestion extends Question`

**Attributes**

- `- choices : List<String>`
- `- correctChoice : String`

**Methods**

- `+ isCorrect(answer : String) : boolean`

#### `TrueFalseQuestion extends Question`

**Attributes**

- `- correctValue : boolean`

**Methods**

- `+ isCorrect(answer : String) : boolean`

#### `ShortAnswerQuestion extends Question`

**Attributes**

- `- expectedAnswer : String`

**Methods**

- `+ compareAnswer(answer : String) : boolean`

#### `Answer`

**Attributes**

- `- answerId : int`
- `- response : String`
- `- correct : boolean`
- `- timestamp : LocalDateTime`

**Methods**

- `+ checkCorrectness() : boolean`

#### `Assignment`

**Attributes**

- `- assignmentId : int`
- `- title : String`
- `- dueDate : LocalDateTime`
- `- questions : List<Question>`

**Methods**

- `+ addQuestion() : void`
- `+ removeQuestion() : void`

#### `ClassList`

**Attributes**

- `- classId : int`
- `- className : String`
- `- students : List<Student>`

**Methods**

- `+ addStudent() : void`
- `+ removeStudent() : void`

---

### Distributed / Networking

#### `Server`

**Attributes**

- `- port : int`
- `- activeClients : List<Client>`

**Methods**

- `+ startServer() : void`
- `+ sendQuestion() : void`
- `+ receiveAnswer() : void`

#### `Client`

**Attributes**

- `- socket : Socket`
- `- currentUser : User`
- `- locale : Locale`

**Methods**

- `+ connect() : void`
- `+ submitAnswer() : void`
- `+ receiveQuestion() : void`

#### `Message` (abstract)

**Attributes**

- `- messageId : int`
- `- timestamp : LocalDateTime`
- `- senderId : int`

**Methods**

- `+ serialize() : String`
- `+ deserialize() : Message`

#### `QuestionMessage extends Message`

**Attributes**

- `- question : Question`
- `- locale : String`

**Methods**

- `+ getQuestion() : Question`

#### `AnswerMessage extends Message`

**Attributes**

- `- answer : Answer`

**Methods**

- `+ getAnswer() : Answer`

#### `AuthMessage extends Message`

**Attributes**

- `- username : String`
- `- password : String`
- `- preferredLocale : String`

**Methods**

- `+ authenticate() : boolean`

#### `ResultMessage extends Message`

**Attributes**

- `- correct : boolean`
- `- feedback : String`

**Methods**

- `+ getFeedback() : String`

---

### Location / Time / GUI

#### `LocationPolicy`

**Attributes**

- `- latitude : double`
- `- longitude : double`
- `- allowedRadius : double`

**Methods**

- `+ isWithinRange() : boolean`

#### `SessionTime`

**Attributes**

- `- startTime : LocalDateTime`
- `- endTime : LocalDateTime`

**Methods**

- `+ getDuration() : Duration`

#### `GUI`

**Attributes**

- `- title : String`

**Methods**

- `+ launch() : void`
- `+ showLoginScreen() : void`
- `+ showQuestionScreen() : void`
- `+ showResultsScreen() : void`
- `+ refreshForLocale() : void`

---

### External Services

#### `AwsStorageService`

**Attributes**

- `- awsEndpoint : String`

**Methods**

- `+ uploadData() : void`
- `+ downloadData() : void`

#### `GeoService`

**Attributes**

- `- apiKey : String`

**Methods**

- `+ getCoordinates() : double[]`
- `+ calculateDistance() : double`

#### `TranslationService`

**Attributes**

- `- providerName : String`

**Methods**

- `+ translate(text : String, sourceLocale : String, targetLocale : String) : String`
- `+ isSupported(locale : String) : boolean`

#### `LocalizationManager`

**Attributes**

- `- currentLocale : Locale`

**Methods**

- `+ getText(key : String) : String`
- `+ setLocale(locale : Locale) : void`
- `+ loadBundle(bundleName : String) : void`

#### `TranslationCache`

**Attributes**

- `- translations : Map<String, String>`

**Methods**

- `+ put(key : String, value : String) : void`
- `+ get(key : String) : String`
- `+ contains(key : String) : boolean`

---

## Inheritance Summary

- `Student extends User`
- `Instructor extends User`
- `MCQQuestion extends Question`
- `TrueFalseQuestion extends Question`
- `ShortAnswerQuestion extends Question`
- `QuestionMessage extends Message`
- `AnswerMessage extends Message`
- `AuthMessage extends Message`
- `ResultMessage extends Message`

---

## Interface Implementation (Draft)

- `AwsStorageService implements Storable`
- `Assignment implements Storable`
- `Answer implements Storable`

---

## Notes

- Core quiz behavior should remain deterministic for MCQ and True/False questions.
- Translation should not change the logical meaning of a question or answer.
- The source-language version should remain available as fallback.
- AI-based translation is optional and should be treated as an enhancement, not the foundation of the system.
