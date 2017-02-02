# TASK DRIVER - Command-line Task Framework

The Task Driver provides a framework that simplifies the process of building high quality command line based
applications. Highlights include:

* Parsing of command line arguments.
* Generation of help output.
* Providing descriptive error messages.
* Generation of Unix launcher scripts.
* Generation of launch4j executables for Windows.
* Providing for verbose and debug output.

## Table of Contents

- [TaskDriverDemo](#taskdriverdemo)
- [Implementing](#implementing)
- [Controlling Output](#controlling-output)
- [Release Notes](#release-notes)
- [Building](#building)
- [Author](#author)
- [License](#license)

## TaskDriverDemo

An example program **TaskDriverDemo** is included as a template for building new programs. Running the program gives the
following output.

```sh
C:\temp\task-driver>task-driver.exe
usage: task-driver [OPTIONS] [int-param|no-param|str-param]
   -d,--debug              turn on debug messages
   -h,--help               print this message
   -o,--optional <arg>     Optional Option
   -r,--required <arg>     Required Option
   -v,--verbose            Verbose mode. (show request/response message)

TASK DRIVER - Command-line Task Framework (v1.0.0)

You must choose one of the following tasks:
   int-param [PARAM-INT].............. Task with integer param.
   no-param........................... Task with no params.
   str-param [PARAM-STR].............. Task with string param.

This program is an example implimentation of CmdlineAdapter.
Contact Chad Juliano<chad.jualiano@oracle.com> for feedback or assistance.

Terminating: Help option requested
```

In the above example there are 3 tasks defined (e.g. int-param, no-param) and 5 options defined that apply to all the
tasks. Running the program with some arguments demonstrates how the parameters are retrieved.

```sh
C:\temp\task-driver>task-driver.exe -r test1 -o test2 int-param 555
Required Option Value: <test1>
Optional Option Value: <test2>
Task INT_PARAM was called with: <555>
Task Complete: <int-param>
```

Running with an invalid argument demonstrates the descriptive error messages.

```sh
D:\Data\cjuliano\ATL_Git\task-driver\build\install\task-driver>task-driver.exe -r test1 -o test2 int-param a
Required Option Value: <test1>
Optional Option Value: <test2>
Terminating: Could not convert PARAM-INT to integer: a
```

The base class will automatically add a debug option. In the below example we execute the same command as above
with the **-d** option.

```sh
D:\Data\cjuliano\ATL_Git\task-driver\build\install\task-driver>task-driver.exe -d -r test1 -o test2 int-param a
Level DEBUG enabled for: <org.taskdriver>
ARGS: (-d,-o,-r,a,int-param,test1,test2)
OPTION: optional = <test2>
OPTION: required = <test1>
OPTION: task = <INT_PARAM>
* Starting task: <int-param>
Required Option Value: <test1>
Optional Option Value: <test2>
ARG: PARAM-INT = <a>
Terminating: Could not convert PARAM-INT to integer: a
org.apache.commons.cli.ParseException: Could not convert PARAM-INT to integer: a
        at org.taskdriver.TaskDriver$CmdlineTaskArgs.takeArgInt(TaskDriver.java:258) ~[task-driver-1.0.0.jar:1.0.0]
        at org.taskdriver.TaskDriverDemo.handleDoTask(TaskDriverDemo.java:82) [task-driver-1.0.0.jar:1.0.0]
        at org.taskdriver.TaskDriver.run(TaskDriver.java:107) ~[task-driver-1.0.0.jar:1.0.0]
        at org.taskdriver.TaskDriverDemo.main(TaskDriverDemo.java:99) [task-driver-1.0.0.jar:1.0.0]
```

## Implementing

*Note: This program makes of use of Java 8 streams and requires **JRE 1.8.***

Create a new project using the provided **build.gradle** and **gradle.properties** as a template. The properties file
contains parameters that get compiled into the JAR manifest. These parameters are retrieved from the JAR when
generating the help screen.

When running JUnit tests placeholder values will be used if there is no JAR file. You can run target
**installLaunch4jDist** and execute the program from **build/install/task-driver** to see the final output.

```properties
# Project Settings
description         = TASK DRIVER - Command-line Task Framework
group               = org.taskdriver
programName         = task-driver
sourceCompatibility = 1.8
targetCompatibility = 1.8

# This version number is incremented with each release
version = 1.0.0

# Launch4j settings.
mainClassName   = org.taskdriver.TaskDriverDemo
copyright       = 2017
programIcon     = dist/cmd.ico
```

The new program should extend **org.taskdriver.TaskDriver**. The following steps are required for configuration:

1. Define a new **enum** for each of the tasks.
1. Call **addTask()** to register each task.
1. Call **addOption()** to register each option.
1. Implement **printHelpFooter()** for the help screen.

Below is an example configuration:

```java
public TaskDriverDemo()
{
    addOption("v", "verbose", false, "Verbose mode. (show request/response message)");
    addOption("o", "optional", true, "Optional Option");
    addOption("r", "required", true, "Required Option");

    addTask(DemoTaskEnum.NO_PARAM, null, "Task with no params.");
    addTask(DemoTaskEnum.STR_PARAM, "PARAM-STR", "Task with string param.");
    addTask(DemoTaskEnum.INT_PARAM, "PARAM-INT", "Task with integer param.");
}
```

The following steps are required to implement program functionality:
1. Implement **handleGetArgs()** to save parameters to member variables.
1. Implement **handleDoTask()** to execute program functionality.
1. Get any task specific arguments in **handleDoTask()**.

The new project can reference the task-driver library from a Maven repository:
```gradle
dependencies {

    // this will import slf4j libraries
    compile group: 'org.taskdriver', name: 'task-driver', version: '1.0.0'
}
```

## Controlling Output

Task driver uses [Logback][LOGBACK-MANUAL] for all program output. Verbose and debug logging are implemented by
enabling debug for various loggers.

The --debug option is added by the TaskDriver base class and will enable debug logging on the **org.taskdriver**
package. The Task Driver demo provides an example of adding the --verbose flag.
You should call **setPackageDebug()** for each java package that should output debug messages for verbose or
debug output.

```java
if(_cmdArgs.hasOption("d"))
{
    // add additional loggers here
}

if(_cmdArgs.hasOption("v"))
{
    setPackageDebug(TaskDriverDemo.class.getPackage());
    // add additional loggers here
}
```

There is an example Logback configuration file copied to **task-driver\lib\config\logback.xml**. You can customize
this file if you need messages logged to a file or you want more control over the loggers. (see the
[documentation][LOGBACK-MANUAL])

*Note: There should only be 1 **logback.xml** on your classpath. It should never be included in a jar that will be shared
with other programs.*

[LOGBACK-MANUAL]: <http://logback.qos.ch/manual/index.html>

## Building

Important Gradle targets are:

* **build**: Compile java sources and create jar file in **./build/libs/**.
* **test**: Execute JUnit tests.
* **installLaunch4jDist**: Create the distribution in **./build/install/task-driver**.
* **publishMavenJavaPublicationToMavenLocal**: Publish to local Maven repository.

## Release Notes

- **1.0.0:** Initial Release

## Author

- [Chad Juliano](https://github.com/chadj2)

## License

This program is licensed under [GNU Lesser General Public License v3.0 only][LGPL-3.0].
Some rights reserved. See [LICENSE][].

![](images/lgplv3b-72.png "LGPL-3.0")
![](images/spdx-72.png "SPDX")

[LGPL-3.0]: <https://spdx.org/licenses/LGPL-3.0>
[LICENSE]: <LICENSE.md>
