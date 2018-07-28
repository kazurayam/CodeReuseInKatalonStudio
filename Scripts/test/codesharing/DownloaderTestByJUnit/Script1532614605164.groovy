import org.junit.runner.JUnitCore
import org.junit.Test
import shouldbebuiltin.keyword.Downloader
import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.is

JUnitCore.runClasses(DownloaderTest.class)

class DownloaderTest {
	
	@Test
	void testGetVersion() {
		Downloader instance = new Downloader()
		assertThat(instance.getVersion(), is('0.2'))
	}
	
}