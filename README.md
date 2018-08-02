A custom Katalon Studio keyword: includeCustomKeywords()
====

# What is this?

This is a simple [Katalon Studio](https://www.katalon.com/) project for demonstration purpose.
You can check this out onto your PC and execute with your Katalon Studio.

This project provides a Groovy class `katalon.codereuse.KeywordTransferer` which
implements a method `includeCustomKeywords` with `@Keyword` annotation.

`includeCustomKeywords` enables you
to include keywords developed by other Katalon Studio project into your project and let you reuse them. `includeCustomKeywords` downloads the distributable zip of a source project file from GitHub/BitBucket, extract \*.groovy files out of the zip, and copy them into the `Keywords` directory of your current Katalon project.

## Problem to solve

In the [Katalon Forum](https://forum.katalon.com/discussions) I have found several posts asking *how to share artifacts developed in a Katalon Studio project with others*.

1. [Drunda Nibel wants automated code sharing](https://forum.katalon.com/discussion/comment/19738)
2. [how to create jar libraries from custom keywords](https://forum.katalon.com/discussion/8518/how-to-create-jar-libraries-from-custom-keywords?new=1)
3. [Sharing test cases or keywords to multiple projects](https://forum.katalon.com/discussion/5343/sharing-test-cases-or-keywords-to-multiple-projects)
3. [Sharing methods between tests](https://forum.katalon.com/discussion/2159/sharing-methods-between-tests)
4. [Is it possible to share test case between different projects?](https://forum.katalon.com/discussion/2104/is-it-possible-to-share-test-case-between-different-projects)
5. [sharing of test cases](https://forum.katalon.com/discussion/7432/sharing-of-test-cases)
6. [Is there any way to share test listeners and custom keywords code to multiple projects?](https://forum.katalon.com/discussion/6063/is-there-any-way-to-share-test-listeners-and-custom-keywords-code-to-multiple-projects)

Here I would define a word *artifacts of a Katalon Studio project* includes the following stuff:

- Test Cases
- Test Suites
- Test Objects
- Custom Keywords
- Test Listeners

If you have a single Web Application and 2 or more people work together to test it, then the best solution is to create a Katalon Studio project and put it into remote Git Repository as Vinh Nguyen mentioned in ["Sharing test cases or keywords to multiple projects"](https://forum.katalon.com/discussion/5343/sharing-test-cases-or-keywords-to-multiple-projects). In the following figure, Alice and Bob share all artifacts of the project X via a Git remote repository. The project will be fully version-controlled, tagged by Git. The access to the Remote repository will be well administrated by the hosting services (GitHub, BitBucket etc)

![Sharing project by remote Git repository](https://github.com/kazurayam/CodeReuseInKatalonStudio/blob/master/docs/Sharing%20project%20by%20remote%20Git%20repository.png)

Perfect solution, isn't it?

However, what would happen if your team has multiple Web applications to test and therefore you created multiple Katalon Studio projects? It is quite likely, you want to reuse some artifacts across project border.
