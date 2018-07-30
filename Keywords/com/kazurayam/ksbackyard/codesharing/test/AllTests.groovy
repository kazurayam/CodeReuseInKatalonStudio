package com.kazurayam.ksbackyard.codesharing.test

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite.class)
@SuiteClasses([
	DownloaderTest.class,
	KeywordPortabilityTest.class
])
// please note.
// In Java, this will be written as @SuiteClasses({...}).
// In Groovy, @SuiteClasses([...])


public class AllTests {
}