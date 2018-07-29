package codesharing.test

import static org.hamcrest.CoreMatchers.is
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import codesharing.KeywordPortability



/**
 *
 * @author kazurayam
 *
 */

class KeywordPortabilityTest {

	@BeforeClass
	static void beforeClass() {
	}

	@Test
	void testGetVersion() {
		assertThat(KeywordPortability.getVersion(), is('0.1'))
	}
}
