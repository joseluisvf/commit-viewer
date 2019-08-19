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

All done!

### Trying it out
1. Run the tool using maven inside the project folder:
    ```
    cd commit-viewer && mvn scala:run
    ```
    
   This will setup an HTTP endpoint to port 12345 to which we can send requests using curl. Try one of the following:

#### Request examples
(Keep in mind all forward slashes in your github url will have to be UTF-encoded (%2F))   

##### GET Commit History
```
http://localhost:12345/commits/<REPOSITORY_URL>
```
Checking out a busy public repository with a moderate amount of commits
> curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy

##### GET paginated Commit History
```
http://localhost:12345/commits/<REPO_URL>/<PAGE_NUMBER>
```
or
```
http://localhost:12345/commits/<REPO_URL>/<PAGE_NUMBER>/<COMMITS_PER_PAGE>
```

Note: COMMITS_PER_PAGE is optional and defaults to 10 commits per page.

Second page of commits from a busy repository
> curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy/2

Fifth page of commits from a busy repository, displaying 20 results per page
> curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy/5/20

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
