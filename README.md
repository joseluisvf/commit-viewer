# Commit Viewer

The commit viewer tool allows you to view the commit list for a github public repository.

It sets-up HTTP endpoints to perform this task: one with support for pagination and one without.

## Strategy
The application will first attempt to use Github's API. In the event of any failure, a fallback method will be used.

For this reason, the path to a file containing a valid Github access token should be provided to the application, although this is not mandatory. 



## Getting Started

### Prerequisites

* scala 2.12+
* maven
* git 

### Installing
1. Simply clone the project. That's all there is to it.

    ```
    git clone https://github.com/joseluisvf/commit-viewer.git
    ```

### Trying it out
1. Run the tool using maven
    ```
    cd commit-viewer && mvn scala:run [-DaddArgs=<"PATH_TO_GITHUB_ACCESS_TOKEN">]
    ```
    
   This will setup an HTTP endpoint to port 12345 to which we can send requests using curl. Try one of the following:

#### Requests
Note: Keep in mind all forward slashes in your github url will have to be UTF-encoded (%2F)

E.g.: `https://github.com/twbs/bootstrap` should be `https:%2F%2Fgithub.com%2Ftwbs%2Fbootstrap`

##### GET commit history
```
http://localhost:12345/commits/<REPOSITORY_URL>
```

This command yields a list of commits, defaulting to 30.

###### E.g.

Checking out a busy public repository with a moderate amount of commits
```
curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy
```

##### GET paginated commit history
```
curl --request GET   --url http://localhost:12345/commits/<REPO_URL>/<PAGE_NUMBER>
```
or
```
curl --request GET   --url http://localhost:12345/commits/<REPO_URL>/<PAGE_NUMBER>/<COMMITS_PER_PAGE>
```

This offers similar functionality to that of "Get Commit History" but with the added flexibility of pagination. 

To this end, one must specify the page number desired and optionally how many commits one wants per page.

Note: COMMITS_PER_PAGE is optional and defaults to 10.


###### E.g.
Getting the second page of commits for a busy repository
```
curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy/2
```

Getting the fifth page of commits from a busy repository, displaying 20 results per page
```
curl --request GET   --url http://localhost:12345/commits/https:%2F%2Fgithub.com%2Fpython%2Fmypy/5/20
```

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

## Note to Reviewers
Hi! I hope this project meets your standards; I sure had fun with its development.

Please feel free to take your gloves off and be as brutal as possible; there's always room for improvement!

Thank you for the challenge and I am looking forward to meeting you someday.

Kind regards,

José Luís de Valle-Flôr
