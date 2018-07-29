package codesharing.test

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.kazurayam.KatalonProperties

import codesharing.Downloader
import internal.GlobalVariable

/**
 *
 * @author kazurayam
 *
 */

class DownloaderTest {

	// tag stands for version of artifacts developed by the MyCustomKeywords project
	static String tagOfMyCustomKeywords = '0.2'

	// URL of the zip file of the MyCustomKeywords project
	static String gitReposZip = "https://github.com/kazurayam/MyCustomKeywords/archive/${tagOfMyCustomKeywords}.zip"

	// package name of custom keywords to be included into this project
	static String[] myPackages = ['com.kazurayam.ksbackyard']


	@BeforeClass
	static void beforeClass() {
		// the KatalonProperties class is contained in MultiSourcedProperites-*.jar registered in the Project's External Libraries
		KatalonProperties props = new KatalonProperties()
		// override GlobalVariables with value loaded from the %USERPROFILE%\katalon.properties file
		GlobalVariable.GitHubUsername = props.getProperty("GlobalVariable.GitHubUsername") ?: "?"
		GlobalVariable.GitHubPassword = props.getProperty("GlobalVariable.GitHubPassword") ?: "?"
	}


	@Test
	void testGetVersion() {
		Downloader instance = new Downloader()
		assertThat(instance.getVersion(), is('0.1'))
	}

}
