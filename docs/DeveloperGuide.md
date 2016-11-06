# Developer Guide 

* [Introduction](#introduction)
* [Setting Up](#setting-up)
* [Design](#design)
* [Implementation](#implementation)
* [Testing](#testing)
* [Dev Ops](#dev-ops)
* [Appendix A: User Stories](#appendix-a-user-stories)
* [Appendix B: Use Cases](#appendix-b-use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c-non-functional-requirements)
* [Appendix D: Glossary](#appendix-d-glossary)
* [Appendix E: Product Survey](#appendix-e-product-survey)


## Introduction

Welcome to the ForgetMeNot development team!

ForgetMeNot is a task manager application that keeps track of your tasks, events and
deadlines. It allows you to manage them efficiently with a keyboard-oriented
command line interface.

This development guide aims to quickly familiarise you with the ForgetMeNot
code base. It will give you an overview of the code architecture, as well as
its various components and how they all interact with each other. By the end of this document, you will
be ready to make your first awesome change to the code. The guide is organized from a top-down perspective to allow an overall idea to be grasped before moving on to the specifics. 


Ready to dive in? Let's get started!

## Setting up

#### Prerequisites

1. **JDK `1.8.0_60`**  or later<br>

    > Having any Java 8 version is not enough. <br>
    This app will not work with earlier versions of Java 8.
    
2. **Eclipse** IDE
3. **e(fx)clipse** plugin for Eclipse (Do the steps 2 onwards given in
   [this page](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious))
4. **Buildship Gradle Integration** plugin from the Eclipse Marketplace


#### Importing the project into Eclipse

0. Fork this repo, and clone the fork to your computer
1. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse** and **buildship** plugins as given 
   in the prerequisites above)
2. Click `File` > `Import`
3. Click `Gradle` > `Gradle Project` > `Next` > `Next`
4. Click `Browse`, then locate the project's directory
5. Click `Finish`

  > * If you are asked whether to 'keep' or 'overwrite' config files, choose to 'keep'.
  > * Depending on your connection speed and server load, it can even take up to 30 minutes for the set up to finish
      (This is because Gradle downloads library files from servers during the project set up process)
  > * If Eclipse auto-changed any settings files during the import process, you can discard those changes.

## Design

### Architecture

<img src="images/Architecture.png" width="600"><br>
The **_Architecture Diagram_** given above explains the high-level design of the App.
Given below is a quick overview of each component.

`Main` has only one class called [`MainApp`](../src/main/java/seedu/forgetmenot/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connect them up with each other.
* At shut down: Shuts down the components and invoke cleanup method where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.
Two of those classes play important roles at the architecture level.
* `EventsCentre` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of _Event Driven_ design)
* `LogsCenter` : Used by many classes to write log messages to the App's log file.

The rest of the App consists four components.
* [**`UI`**](#ui-component) : The UI of tha App.
* [**`Logic`**](#logic-component) : The command executor.
* [**`Model`**](#model-component) : Holds the data of the App in-memory.
* [**`Storage`**](#storage-component) : Reads data from, and writes data to, the hard disk.

Each of the four components
* Defines its _API_ in an `interface` with the same name as the Component.
* Exposes its functionality using a `{Component Name}Manager` class.

<!-- @@author A0147619W -->
For example, the `Logic` component (see the class diagram given below) defines it's API in the `Logic.java`
interface and exposes its functionality using the `LogicManager.java` class.<br>
<img src="images/LogicDiagram.png" width="800"><br>

The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `delete 1`. This same _Sequence Diagram_ will be used to illustrate `undo` in the following _Sequence Diagram_.

<img src="images\SDforDeleteTask.png" width="800"><br>

>Note how the method saveToHistory() is called when delete is being executed. A copy of the `TaskManager` will be stored
within `Model`. This is the same for task modifying commands such as `add`, `edit` and `clear`.

The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the command `undo`.

<img src="images\SDforUndo.png" width="800"><br>

When `undo` is entered by the user, the method loadFromHistory() will be executed to replace the current `TaskManager` to the most recent `TaskManager` saved in `Model` when saveToHistory() was previously executed. When the user exits ForgetMeNot, this stored history will be cleared.

>Note how the `Model` simply raises a `TaskManagerChangedEvent` when the Task Manager data are changed,
 instead of asking the `Storage` to save the updates to the hard disk.

The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\SDforDeleteTaskEventHandling.png" width="800">

> Note how the event is propagated through the `EventsCenter` to the `Storage` and `UI` without `Model` having
  to be coupled to either of them. This is an example of how this Event Driven approach helps us reduce direct 
  coupling between components.

The sections below give more details of each component.

### UI component

<img src="images/UiClassDiagram.png" width="800"><br>

**API** : [`Ui.java`](../src/main/java/seedu/forgetmenot/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `TaskListPanel`,
`StatusBarFooter`, `BrowserPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class
and they can be loaded using the `UiPartLoader`.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files
 that are in the `src/main/resources/view` folder.<br>
 For example, the layout of the [`MainWindow`](../src/main/java/seedu/forgetmenot/ui/MainWindow.java) is specified in
 [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml)

The `UI` component,
* Executes user commands using the `Logic` component.
* Binds itself to some data in the `Model` so that the UI can auto-update when data in the `Model` change.
* Responds to events raised from various parts of the App and updates the UI accordingly.

<!-- @@author A0147619W -->
### Logic component

<img src="images/LogicDiagram.png" width="800"><br>

**API** : [`Logic.java`](../src/main/java/seedu/forgetmenot/logic/Logic.java)

1. `Logic` uses the `Parser` class to parse the user command.
2. This results in a `Command` object which is executed by the `LogicManager`.
3. The command execution can affect the `Model` (e.g. adding a task) and/or raise events.
4. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")`
 API call.<br>
<img src="images/DeleteTaskSdForLogic.png" width="800"><br>

### Model component

<img src="images/ModelDiagram.png" width="800"><br>

**API** : [`Model.java`](../src/main/java/seedu/forgetmenot/model/Model.java)

The `Model`,
* stores a `UserPref` object that represents the user's preferences.
* stores ForgetMeNot's data.
* exposes a `UnmodifiableObservableList<ReadOnlyTask>` that can be 'observed' e.g. the UI can be bound to this list
  so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.
<!-- @@author A0139671X -->

### Storage component

<img src="images/StorageClassDiagram.png" width="800"><br>

**API** : [`Storage.java`](../src/main/java/seedu/forgetmenot/storage/Storage.java)

The `Storage` component,
* can save `UserPref` objects in json format and read it back.
* can save ForgeMeNot's data in xml format and read it back.
* can redirect the storage file path for the data in xml format and read it back.
<!-- @@author -->

### Common classes

Classes used by multiple components are in the `seedu.forgetmenot.commons` package.

They are further separated into sub-packages - namely `core`, `events`, `exceptions` and `util`.

* Core - This package consists of the essential classes that are required by multiple components.
* Events -This package consists of the different type of events that can occur; these are used mainly by EventManager and EventBus.
* Exceptions - This package consists of exceptions that may occur with the use of ForgetMeNot.
* Util - This package consists of additional utilities for the different components.

## Implementation

### Logging

We are using `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels
and logging destinations.

* The logging level can be controlled using the `logLevel` setting in the configuration file
  (See [Configuration](#configuration))
* The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level
* Currently log messages are output through: `Console` and to a `.log` file.

**Logging Levels**

* `SEVERE` : Critical problem detected which may possibly cause the termination of the application
* `WARNING` : Can continue, but with caution
* `INFO` : Information showing the noteworthy actions by the App
* `FINE` : Details that is not usually noteworthy but may be useful in debugging
  e.g. print the actual list instead of just its size

### Model implementation

The model componenet model the data of the application.

#### The `ModelManager` class

The `ModelManager` class implements the `Model` interface, and provides access to data in model while hiding the internal complexity of its various classes. All external components can only interact with the model data via this class.

#### The `TaskManager` class

The `TaskManager` class stores the list of `UniqueTaskList`. It is an internal class of the Model component. The external componenet can only access its data through the `ReadOnlyTaskManager` or `Model` interface.

#### The `ReadOnlyTaskManager` class

The `ReadOnlyTaskManager` interface provides a read-only view to the `TaskManager` object.

#### The `Config` class

The `Config` class stores the configuration settings.

### Storage implementation

The storage component uses [Jackson](https://github.com/FasterXML/jackson) to
serialize/deserialize model data to/from JSON files.

#### The Storage interfaces

The storage package defines two storage interfaces, `UserPrefsStorage` and
`TaskManagerStorage`. These interfaces contain methods for saving/loading `ReadOnlyTaskManager`.

The storage package also defines a facade `Storage` interface, which combines
together the aforementioned `UserPrefsStorage` and `TaskManagerStorage` interfaces
into a single interface.

### Configuration

Certain properties of the application can be controlled (e.g App name, logging level) through the configuration file 
(default: `config.json`):


## Testing

Tests can be found in the `./src/test/java` folder.

**In Eclipse**:
> If you are not using a recent Eclipse version (i.e. _Neon_ or later), enable assertions in JUnit tests
  as described [here](http://stackoverflow.com/questions/2522897/eclipse-junit-ea-vm-option).

* To run all tests, right-click on the `src/test/java` folder and choose
  `Run As` > `JUnit Test`
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.

**Using Gradle**:
* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.

We have two types of tests:

1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI. 
   These are in the `guitests` package.
  
2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.address.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units 
     (those code units are assumed to be working).<br>
      e.g. `seedu.address.storage.StorageManagerTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as 
      how the are connected together.<br>
      e.g. `seedu.address.logic.LogicManagerTest`

<!-- @@author A0139671X-->
**Measuring Coverage Locally using EclEmma**:
 * Install the [EclEmma Eclipse Plugin](http://www.eclemma.org/) in your computer and use that to
   find code that is not covered by the tests.
 * To measure coverage after installing plugin, right-clck on the `src/test/java` folder and choose 
   `Coverage As` > `JUnit Test`.
 * To see the color code for EclEmma coverage. Refer to [this](http://www.eclemma.org/userdoc/
   annotations.html).
<!-- @@author -->
  
**Headless GUI Testing**:
Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in the _headless_ mode. 
 In the headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the Computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.
  
## Dev Ops

### Build Automation

See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.

### Continuous Integration

We use [Travis CI](https://travis-ci.org/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) for more details.

### Making a Release

Here are the steps to create a new release.
 
 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file).
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Crete a new release using GitHub](https://help.github.com/articles/creating-releases/) 
    and upload the JAR file your created.
   
### Managing Dependencies

A project often depends on third-party libraries. For example, Task Manager depends on the
[Jackson library](http://wiki.fasterxml.com/JacksonHome) for XML parsing. Managing these _dependencies_
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which
is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size)<br>
b. Require developers to download those libraries manually (this creates extra work for developers)<br>
<!-- @@author A0147619W -->

## Appendix A: User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`


Priority | As a ... | I want to ... | So that I can...
-------- | :-------- | :--------- | :-----------
`* * *` | user | add floating tasks without date or time | I can keep track of tasks which need to be done whenever I have time.
`* * *` | user | add deadline tasks with only an end time | I can keep track of deadlines.
`* * *` | user | add event tasks with only a start time | I can keep track of events.
`* * *` | user | add event tasks with both start time and end time | I can keep track of events.
`* * *` | user | search for tasks | review the details of the task. 
`* * *` | user | delete a task | can get rid of tasks that I no longer care to track.  
`* * *` | user | edit the details of a specific task | reschedule the task if the deadline has changed.
`* * *` | new user | view the availability of all the possible commands | understand what features there are in the product.
`* * *` | user | have a few natural variations in my command inputs | key in my task more efficiently.
`* * *` | user | view all my tasks | I have an idea about the pending tasks.
`* * *` | user | mark a task as done | it will be removed from my list of things to do.
`* * *` | user | specify a specific folder as the data storage location | I can decide where to place my file for the task manager.
`* * *` | user | have a done list | see what has been done for the day to know how productive I've been.
`* * *` | user | clear my tasks | delete all the tasks in my task manager at once.
`* * *` | user | clear my done tasks | delete all my tasks that are done from the done list.
`* * *` | user | exit ForgetMeNot | close my task manager application whenever I want.
`* *` | user | add a recurring tasks | add the task once and not every time it occurs.
`* *` | user | undo a command | go back to the previous command if I have made a mistake.
`* *` | user | redo an undo | go back to the previous state if I have made an accidental undo.
`*` | user | auto-complete my commands | quickly type all my commands.
`*` | user | view my command history | look at all my previously typed commands and reuse them if I want to. 



## Appendix B: Use Cases

(For all the use cases below, the **System** is the `ForgetMeNot` and the **Actor** is the `user`, unless specified otherwise)

	
#### Use Case: Add task

**MSS** <br>
1. User types in a task to be added, with or without start and end times. <br>
2. ForgetMeNot adds the task in the list of tasks <br>
      Use case ends.

**Extensions**

	1a. User enters an incorrect command

> 1a1. ForgetMeNot shows an error message.
	
	1b. User enters invalid time
	
> 1b1. ForgetMeNot shows an appropriate error message.

#### Use Case: Clear Task

**MSS**

1. User types in clear command <br>
2. ForgetMeNot clears the list of task <br>
	Use case ends <br>
	
**Extensions**

	1a. User types in wrong command

> 1a1. ForgetMeNot shows error and help message


#### Use Case: Clear Done

**MSS**

1. User types in clear done command
2. ForgetMeNot clears the list of done task
	Use case ends
	
**Extensions**

	1a. User types in wrong command
	
> 1a1. ForgetMeNot shows error and help message

	2a. The done list is empty
	
> 2a1. ForgetMeNot shows error message
	
#### Use Case: Delete Task

**MSS**

1. User requests to list tasks
2. ForgetMeNot shows the list of tasks to the user
3. User requests to delete a particular task
4. ForgetMeNot deletes the task
      Use case ends.

**Extensions**

2a. The list is empty

> 2a1. Use case ends

3a. The task is not found

> 3a1. ForgetMeNot displays an error message
> 2a2. Use case resumes at step 2

<!-- @@author A0139671X -->

#### Use case: Edit a task

**MSS**

1. User requests to edit the various fields of a task.
2. ForgetMeNot shows to the user that the task has been edited.
	 Use case ends.

**Extensions**

	1a. Input command incorrect.
	
> 1a1. ForgetMeNot shows error message.

	1b. The task does not exist.
	
> 1b1. ForgetMeNot suggests user to check the input or add a new task.

	1c. The new edit details are invalid.
	
> 1c1. ForgetMeNot shows error message of the wrong details.
	
	2a. User changed his/her mind.
	
> 2a1. Command is removed.

<!-- @@author -->

#### Use case: Mark task as done

**MSS**

1. User request to mark a specific task as done.
2. System prompts for confirmation.
3. User confirms.
4. System shows user that the task is marked as done.
     Use case ends.
       
**Extensions**

    1a. Input command incorrect.
    
> 1a1. System shows help message.

	1b. Task entered does not exist
	
> 1b1. System prompts user to check input or add a new task
	
	2a. User changed his mind
	
> 2a1. Command is removed.

#### Use case: Show task

**MSS**

1. User request to show tasks.
2. System shows user the list of task.
      Use case ends
      
**Extensions**

	1a. Input command incorrect.
	
> 1a1. System shows help message.
	
    1b. No task inside the list.
    
> 1b1. System shows error message
> 1b2. Prompt user to add tasks

<!-- @@author A0139671X -->

#### Use Case: Undo a task

**MSS**

1. User undo a task
2. ForgetMeNot undo the most recent command executed
      Use case ends
      
**Extension**

	1a. No command to be undone
	
> 1a1. ForgetMeNot shows error message

	1b. User inputs an invalid input
	
> 1b1.ForgetMeNot shows error message

#### Use Case: Redo a task

**MSS**

1. User redoes a task
2. ForgetMeNot redoes the most recent undo command executed
	Use case ends

**Extension**
	
	1a. No command to be redone
	
> 1a1. ForgetMeNot shows error message
	
	1b. User inputs an invalid input
	
> 1b1. ForgetMeNot shows error message

<!-- @@author -->

#### Use case: Set storage of the tasks in ForgetMeNot to a different folder

**MSS**

1. User types in the setstorage command along with the name of the new folder to which he/she wants the location to be changed.
2. ForgetMeNot changes the location of the tasks to the new specified folder.
	Use case ends
	
**Extension**

	1a. User types in a file in an incorrect format.

> 1a1. ForgetMeNot shows an error message and requests the user to confirm to the set format.

	1b. User tries to access a folder where he/she does not have access to.
	
> 1b1. ForgetMeNot shows an appropriate error message.

#### Use Case: Exit ForgetMeNot

**MSS**

1. User types the exit command.
2. ForgetMeNot shuts down.


## Appendix C: Non Functional Requirements

1. Should be able to hold at least 100 tasks.
2. Should be able to display request under 0.5 seconds.
3. Should work on any mainstream OS as long as it has Java 8 or higher installed.
4. Should be able to operate without internet connection.
5. Should come with automated unit tests.
6. Should be able to use the product efficiently after using it for 15 minutes.
7. For a full list of constrains, see the handbook at http://www.comp.nus.edu.sg/~cs2103/AY1617S1/contents/handbook.html#handbook-project-constraints  


## Appendix D: Glossary

Mainstream OS: 
> Windows, Linux, Unix, OS-X

Day:
> From 0000 to 2359 of the current day


## Appendix E: Product Survey

<!-- @@author A0139211R -->
### Fantastical
#### Strengths

1. It has integration with all iOS products, i.e. mac, iphone, ipad etc. <br>
2. Includes all CRUD features. <br>
3. It has a reminder function. <br>
4. Has a list of all upcoming tasks for the week at the left hand side. <br>
5. Has natural language processing, can add events using Siri. <br>
6. Locations added when creating events are automatically shown in google/apple map when clicked. <br>
7. Automatically syncs with apple calendar, updates and syncs on the go. <br>
8. Can scroll according to tasks for by the first alphabet of the task, which makes it easier to find the tasks you want <br>

#### Weakness

1. Limited features for free users. <br>
2. Hard to categorise tasks. <br> 
3. Can be difficult to use for first time users <br>
4. No support for ipad. <br>
5. No alert when event is starting <br>
6. Default color scheme is black background with white words. May be quite distracting and hinders reading of words.
7. UI is not really user friendly with a whole lot of words
8. Little color code for users to categorize
9. Only available for apple products i.e. Mac, Iphone, Ipad etc.
10. Requires quite a bit of point and clicking
<!-- @@author A0139211R -->


### Google Cal
#### Strengths

1. It has CRUD features. <br>
2. It can link to external applications such as Gmail and Contacts. <br>
3. It has cross-platform features.<br>
3. It has a reminder function.<br>
4. It can support multiple accounts in one device.<br>
5. It can create Event, Reminder or Goal.<br>
6. All task created are automatically grouped and colour coded.<br>
7. Clean and simple UI.<br>
8. It can be used online or offline.<br>
9. It has different kind of viewing options such as Day, 3-day, Week and Month.<br>

#### Weakness

1. Does not have done function.<br>
2. Not keyboard friendly.<br>
3. Requires a google account.<br>
4. Does not support floating task.<br>
5. Steep learning curve.<br>
6. Does not support categories.<br>


<!-- @@author A0139671X-->
### Any.do
##### Strengths

1. It has support for events, deadlines, floating tasks.<br>
2. It has CRUD.<br>
3. It has a power search function for all tasks.<br>
4. It has a way to keep track of which items are done and yet to be done in reminders.<br>
5. It has categories, predefined ones and customizable ones.<br>
6. It allows priority settings for each tasks.<br>
7. It has an alarm feature for tasks.<br>
8. It has a very useful tasks descriptive features such as subtask, note, images, audio attachments, video attachments
9. It has the option to make tasks recurring.<br>
10. It has location reminder.<br>
11. It has the option for Any.do to walk the user through his/her tasks to make tasks organization better<br>
12. Portable as it available on mobile devices.<br>
13. It can be synced across all devices such as computers, phones, tablets.<br>
14. Easy to shift tasks between categories.<br>


##### Weaknesses

1. It has premium features that require payment.<br>
2. No auto clear of done tasks or the option to auto clear done tasks after a certain period.<br>
3. No levels of priority. Only priority or no priorty.<br>
4. Floating tasks are not always displayed.<br>
5. Only one color scheme in the basic version. <br>
6. Requires an account to start using.<br>
7. Not keyboard friendly. Requires substantial mouse usage.<br>
8. Frequently malfunctions and requres a restart.<br>
<!-- @@author -->

<!-- @@author A0147619W -->
### Wunderlist
##### Strengths

1. It has CRUD features for floating tasks. <br>
2. It has CRUD features for deadlines. <br>
3. It has a search function. <br>
4. It has different tabs for today, tomorrow and date-wise events. <br>
5. It allows to repeat reminders on a periodic basis. <br>
6. It has great syncing facilities. <br>
7. It allows users to group all related lists in one easily accessible folder. <br>
8. It has a comments section which hosts all our communication in one place, accessible for all. <br>
9. It is free to use on all devices. <br>
10. It has support for updates via push, email and in-app notifications. <br>
11. It allow sthe user to print his to-dos and lists with just one click. <br>

##### Weaknesses

1. It is not fully keyboard operated. Requires some amount of mouse usage. <br>
2. No auto clear of done tasks or the option to auto clear done tasks after a certain period.<br>
3. It does not support CRUD for events. <br>
4. Does not provide flexibility in command line format. <br>
5. It has limited undo options. <br>
6. It does not have the ability to block multiple slots. <br>
