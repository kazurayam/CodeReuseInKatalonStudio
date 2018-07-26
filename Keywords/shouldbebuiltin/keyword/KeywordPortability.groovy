package shouldbebuiltin.keyword

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern
import java.util.regex.Matcher
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.annotation.Keyword
import org.apache.http.client.config.RequestConfig
import org.apache.http.HttpHost

/**
 * 
 * @author kazurayam
 *
 */
public class KeywordPortability {

	static String version_ = '0.0.1'

	@Keyword
	static boolean includeCustomKeywords(
			String zipUrl,
			String username='',
			String password='',
			List<String> packages=[]) {
		if (zipUrl == null) {
			throw new IllegalArgumentException("zipUrl is required")
		}

		// directory to save the zip file
		Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')

		


		println "zipUrl=${zipUrl} username='${username}' password=\"${password.replaceAll('.','*')}\" packageNames=${packages}"
		return true
	}

	// check if I am behind Proxy or not
	private static boolean amIBehindProxy() {
		def pi = RunConfiguration.getProxyInformation()
		if (pi.proxyOption == 'MANUAL_CONFIG' &&
		pi.proxyServerType == 'HTTP' &&
		pi.proxyServerAddress.length() > 0 &&
		pi.proxyServerPort > 0) {
			return true
		} else {
			return false
		}
	}

	// read Proxy Configuration out of Katalon Studio's config
	private static RequestConfig createProxyConfig() {
		def pi = RunConfiguration.getProxyInformation()
		if (pi.proxyOption == 'MANUAL_CONFIG' &&
		pi.proxyServerType == 'HTTP' &&
		pi.proxyServerAddress.length() > 0 &&
		pi.proxyServerPort > 0) {
			HttpHost proxy = new HttpHost(pi.proxyServerAddress,
					pi.proxyServerPort, 'http')
			RequestConfig config = RequestConfig.custom()
					.setProxy(proxy)
					.build()
			return config
		} else {
			return null
		}

	}


	static Pattern pattern = Pattern.compile(/^attachment; filename=(.+)$/)

	// get the recommended filename out of HTTP response header
	private static String getRecommendedFilename(String contentDisposition, String defaultFilename='a.zip') {
		// Content-Disposition: attachment; filename=MyCustomKeywords-master.zip
		Matcher matcher = pattern.matcher(contentDisposition)
		if (matcher.matches()) {
			String filename = matcher.group(1)
			return filename
		} else {
			println("contentDisposition is '${contentDisposition}', which does not match ${pattern}.")
			println("Returning '${defaultFilename}'")
			return defaultFilename
		}
	}

}
