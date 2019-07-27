# Commit Viewer

The commit viewer tool allows you to view the commit list for a given github public repository.

## Getting Started

### Prerequisites

* scala 2.12+
* maven
* git 

### Installing
1. First, clone the project

    ```
    git clone https://github.com/joseluisvf/commit-viewer.git
    ```

1. All done. Now run the tool using maven inside the project folder and supply a github repository URL e.g.:
    ```
    cd commit-viewer && mvn scala:run -DaddArgs=https://github.com/joseluisvf/commit-viewer
    ```

1. You will find the [commit history file](src/main/resources/commit_history_result/commit-history.txt) in the project's resources. 

## Running the tests

1. While in the project's directory, run the unit tests via maven

    ```
    cd commit-viewer && mvn test
    ```

## Built With

* [Maven](https://maven.apache.org/) - Dependency management
* [Scalastyle](http://www.scalastyle.org/) - Scala style checker
* [Scoverage](http://scoverage.org/) - Scala code coverage
* [Log4j2](https://logging.apache.org/log4j/2.x/) - Logging

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
