#! /bin/bash

if [ "$#" -ne 4 ] || ! [ -d "$2" ]; then
  echo "usage: $0 <github repo url> <temporary directory path> <commit history file name> <placeholder separator>"
  echo "e.g. get-commit-history.sh https://github.com/joseluisvf/commit-viewer . ---"
  exit 1
fi

gitRepoUrl=$1
temporaryDirectoryPath=$2
commitHistoryFileName=$3
placeholderSeparator=$4
temporaryFolder=$temporaryDirectoryPath/eraseme

SUCCESS_ERROR_CODE=0
REPOSITORY_NOT_FOUND_ERROR_CODE=2

# create a temporary folder and clone the repository in it
mkdir $temporaryFolder && cd $temporaryFolder
git clone $gitRepoUrl

# cd to the repo directory
repositoryNameWithExtension=${gitRepoUrl##*/}
repositoryName=${repositoryNameWithExtension%*\.git}

# ensure the repository directory exists
if ! [ -d "$repositoryName" ]; then
  echo "Aborting execution: unable to clone the provided repository $gitRepoUrl"
  rm -rf $temporaryFolder
  exit $REPOSITORY_NOT_FOUND_ERROR_CODE
fi

# create (or overwrite) a file with the commit history
cd $repositoryName
git log --format="%H$placeholderSeparator%s" > $temporaryDirectoryPath/$commitHistoryFileName
rm -rf $temporaryFolder
exit $SUCCESS_ERROR_CODE
