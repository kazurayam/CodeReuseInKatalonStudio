package codesharing

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration

/**
 * 
 * @author kazurayam
 *
 */
public class KeywordPortability {

	static final String version = '0.1'

	static String getVersion() {
		return version
	}

	@Keyword
	static boolean includeCustomKeywords(
			String zipUrl,
			String username='',
			String password='',
			List<String> packages=[]) {
		if (zipUrl == null) {
			throw new IllegalArgumentException("zipUrl is required")
		}

		URL url = new URL(zipUrl)

		// create the helper class to download files via HTTP with Proxy awareness
		Downloader downloader
		if (amIBehindProxy()) {
			downloader = new Downloader(createProxyConfig())
		} else {
			downloader = new Downloader()
		}

		// make a HEAD request to the URL in order to be informed of the recommended file name
		String contentDisposition = downloader.getContentDispositionHeader(url)

		// name of the downloaded file
		String filename = this.getRecommendedFilename(contentDisposition)

		// we will save the downloaded zip file into this directory
		Path zipFile = Paths.get(System.getProperty('user.home'), 'Downloads', filename)

		// now download the zipFile
		downloader.download(url, zipFile.toFile())

		println "zipUrl=${zipUrl} username='${username}' password=\"${password.replaceAll('.','*')}\" packageNames=${packages}"
		return true
	}

	// check if I am behind Proxy or not
	static boolean amIBehindProxy() {
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
	static RequestConfig createProxyConfig() {
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
	static String getRecommendedFilename(String contentDisposition, String defaultFilename='a.zip') {
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
