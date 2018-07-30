package codesharing.test

import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.CoreMatchers.nullValue
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore

import com.kazurayam.KatalonProperties

import codesharing.Downloader
import internal.GlobalVariable

import org.apache.http.Header
import org.apache.http.HttpHost

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
		assertThat(Downloader.getVersion(), is('0.1'))
	}

	@Test
	void testGetProxy() {
		HttpHost proxy = Downloader.getProxy()
		assertThat(proxy, is( not( nullValue() ) ))
		assertThat(proxy.getHostName(), is('172.24.2.10'))
		assertThat(proxy.getPort(), is(8080))
	}
	
	@Test
	void testGetAllHeaders() {
		Downloader downloader = new Downloader()
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip))
		assertTrue(headers.length > 0)
		for (Header header : headers) {
			println "${header}"
		}
	}

	@Ignore
	@Test
	void testGetHeader() {
		String headerName = 'Content-Disposition'
		Downloader downloader = new Downloader()
		Header header = downloader.getHeader(new URL(gitReposZip), headerName)
		assertNotNull(header)
		println "#testGetHeader ${headerName}: ${header.toString()}"
	}


}
