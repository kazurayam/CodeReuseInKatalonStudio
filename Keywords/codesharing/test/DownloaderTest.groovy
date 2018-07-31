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
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials

import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 * @author kazurayam
 *
 */

class DownloaderTest {

	// tag stands for version of artifacts developed by the MyCustomKeywords project
	static String tagOfMyCustomKeywords = '0.2'

	// URL of the zip file of the MyCustomKeywords project
	static String gitReposZip_public = "https://github.com/kazurayam/MyCustomKeywords/archive/${tagOfMyCustomKeywords}.zip"
	static String gitReposZip_private = "https://github.com/kazurayam/HappyMigrationSiteTest/archive/master.zip"

	// package name of custom keywords to be included into this project
	static String[] myPackages = ['com.kazurayam.ksbackyard']


	@BeforeClass
	static void beforeClass() {
		// the KatalonProperties class is contained in MultiSourcedProperites-*.jar registered in the Project's External Libraries
		KatalonProperties props = new KatalonProperties()
		// override GlobalVariables with value loaded from the %USERPROFILE%\katalon.properties file
		GlobalVariable.GithubUsername = props.getProperty("GlobalVariable.GithubUsername") ?: ''
		GlobalVariable.GithubPassword = props.getProperty("GlobalVariable.GithubPassword") ?: ''
	}

	
	@Test
	void testGetVersion() {
		assertThat(Downloader.getVersion(), is('0.1'))
	}

	@Ignore
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
		Credentials credentials = new UsernamePasswordCredentials('', '')
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip_public), credentials)
		assertTrue(headers.length > 0)
		for (Header header : headers) {
			println "#testGetAllHeaders ${header}"
		}
	}

	
	@Test
	void testGetHeader() {
		String headerName = 'Content-Disposition'
		Downloader downloader = new Downloader()
		Credentials cred = new UsernamePasswordCredentials('', '')
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip_public), cred)
		Header header = downloader.getHeader(headers, headerName)
		assertNotNull(header)
		assertThat(header.getName(), is(headerName))
		assertTrue(header.getValue().contains('attachment; filename=MyCustomKeywords-0.2.zip'))
		println "#testGetHeader ${headerName}: ${header.toString()}"
	}

	
	@Test
	void testGetContentDispositionFilename() {
		String headerName = 'Content-Disposition'
		Downloader downloader = new Downloader()
		Credentials cred = new UsernamePasswordCredentials('', '')
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip_public), cred)
		String filename = downloader.getContentDispositionFilename(headers)
		println "#testGetContentDispositionFilename filename is ${filename}"
		assertNotNull(filename)
		assertThat(filename, is('MyCustomKeywords-0.2.zip'))
	}

	@Test
	void testDownload() {
		Downloader downloader = new Downloader()
		Credentials cred = new UsernamePasswordCredentials('', '')
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip_public), cred)
		String filename = downloader.getContentDispositionFilename(headers)
		Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')
		File downloadedFile = downloadsDir.resolve(filename).toFile()
		downloader.download(new URL(gitReposZip_public), cred, downloadedFile)
		assertTrue(downloadedFile.exists())
		assertTrue(downloadedFile.length() > 0)
	}

	@Test
	void testDownload_private() {
		Downloader downloader = new Downloader()
		Credentials cred = new UsernamePasswordCredentials(GlobalVariable.GithubUsername, GlobalVariable.GithubPassword)
		Header[] headers = downloader.getAllHeaders(new URL(gitReposZip_private), cred)
		for (Header h : headers) {
			println "${h}"
		}
		assertNotNull("headers is null", headers)
		String filename = downloader.getContentDispositionFilename(headers)
		assertNotNull("filename is null", filename)
		Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')
		File downloadedFile = downloadsDir.resolve(filename).toFile()
		downloader.download(new URL(gitReposZip_private), cred, downloadedFile)
		assertTrue("${downloadedFile} does not exists", downloadedFile.exists())
		assertTrue("${downloadedFile} has 0 bytes content", downloadedFile.length() > 0)
		println "#testDownload_private downloaded ${downloadedFile}"
	}
}
