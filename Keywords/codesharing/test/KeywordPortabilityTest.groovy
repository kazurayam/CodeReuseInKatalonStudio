package codesharing.test

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore

import codesharing.KeywordPortability

import org.apache.commons.io.FileUtils


/**
 *
 * @author kazurayam
 *
 */

class KeywordPortabilityTest {

	// tag stands for version of artifacts developed by the MyCustomKeywords project
	static String tagOfMyCustomKeywords = '0.2'

	// URL of the zip file of projects on the GitHub
	static String gitReposZip_public = "https://github.com/kazurayam/MyCustomKeywords/archive/${tagOfMyCustomKeywords}.zip"
	static String gitReposZip_private = "https://github.com/kazurayam/HappyMigrationSiteTest/archive/master.zip"

	static List<String> myPackages = ['com.kazurayam.ksbackyard']

	static Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')
	static Path zipFilePath = downloadsDir.resolve('MyCustomKeywords-0.2.zip')
	static Path unzippedDir = downloadsDir.resolve('MyCustomKeywords-0.2')

	@BeforeClass
	static void beforeClass() {
	}

	@Test
	void testGetVersion() {
		assertThat(KeywordPortability.getVersion(), is('0.1'))
	}

	@Test
	void testUnzip() {
		List<Path> dirs = KeywordPortability.unzip(new ZipFile(zipFilePath.toFile()), downloadsDir)
		assertTrue(dirs.size() == 1)
		assertTrue(Files.exists(unzippedDir))
	}

	@Test
	void testIncludeCustomKeywords_public() {
		KeywordPortability instance = new KeywordPortability()
		Path destKeywords = Paths.get(System.getProperty('user.dir')).resolve('tmp').resolve('Keywords_testIncludeCustomKeywords_public')
		FileUtils.deleteDirectory(destKeywords.toFile())
		instance.includeCustomKeywords(gitReposZip_public, '', '', myPackages, destKeywords)
		assertTrue(Files.exists(unzippedDir))
	}

	@Ignore
	@Test
	void testIncludeCustomKeywords_private() {
		KeywordPortability instance = new KeywordPortability()
		Path destKeywords = Paths.get(System.getProperty('user.dir')).resolve('tmp').resolve('Keywords_testIncludeCustomKeywords_private')
		FileUtils.deleteDirectory(destKeywords.toFile())
		instance.includeCustomKeywords(gitReposZip_private, '', '', myPackages, destKeywords)
		assertTrue(Files.exists(unzippedDir))
	}

	@Test
	void testWhichPackagesToCopy() {
		def packages = ['com.kazurayam.ksbackyard']
		List<Path> paths = KeywordPortability.whichPackagesToCopy(packages)
		assertThat(paths.size(), is(1))
		assertThat(paths[0], is(Paths.get('com', 'kazurayam', 'ksbackyard')))
	}
}
