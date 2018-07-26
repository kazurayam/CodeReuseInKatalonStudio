IncludeCustomKeyword
====

# What is this?

This is a simple [Katalon Studio](https://www.katalon.com/) project for demonstration purpose. You can check this out onto your PC and execute with your Katalon Studio.

This project proposes a Custom Keyword `includeCustomKeyword`. This keyword does the following:

1. `includeCustomKeyword` requires a few arguments which includeCustomKeyword
    1. (Required) a URL to download the zip file from a remote Git repository (such as GitHub, BitBucket, GitLab etc). For example `https://github.com/kazurayam/MyCustomKeywords/archive/master.zip`
    2. (Optional) a pair of credentials to get access to the Git repository. Be careful to encrypt the credential strings.
    3. (Required) a set of Fully Qualified Groovy Class names to include out of the zip into the local katalon project. For example `com.kazurayam.ksbackyard.MyCustomKeywords`
