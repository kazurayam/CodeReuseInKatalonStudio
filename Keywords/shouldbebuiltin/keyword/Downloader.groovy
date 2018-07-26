package shouldbebuiltin.keyword

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.http.Header
import org.apache.http.HeaderElement
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy

/**
 * Downloader drives HTTP request & response to download a distribute file from a web site.
 * Downloader is Proxy aware, but the caller need to pass the Proxy config to the constructor.
 * 
 * 
 * Copy&pasted from
 * https://gist.github.com/rponte/09ddc1aa7b9918b52029
 */
public class Downloader {

	String version = '0.1'
	
	// Proxy config
	RequestConfig requestConfig

	Downloader() {
		requestConfig = null
	}

	Downloader(RequestConfig rc) {
		this.requestConfig = rc
	}

	String getVersion() {
		return this.version	
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public Header[] getAllHeaders(URL url) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build()
		try {
			HttpHead head = new HttpHead(url.toURI())
			if (this.requestConfig != null) {
				// set Proxy config if necessary
				head.setConfig(requestConfig)
			}
			HttpResponse response = httpclient.execute(head)
			Header[] headers = response.getAllHeaders()
			return headers
		} catch (Exception e) {
			throw new IllegalStateException(e)
		} finally {
			IOUtils.closeQuietly(httpclient)
		}
	}

	/**
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	public Header getHeader(URL url, String name) {
		Header[] headers = this.getAllHeaders(url)
		for (Header header : headers) {
			HeaderElement[] elements = header.getElements()
			for (HeaderElement he : elements) {
				if (he.getName() == name) {
					return header
				}
			}
		}
		return null
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public String getContentDispositionHeader(URL url) {
		String name = 'Content-Disposition'
		Header header = this.getHeader(url, name)
		if (header != null) {
			HeaderElement[] elements = header.getElements()
			for (HeaderElement he : elements) {
				if (he.getName() == name) {
					return he.getValue()
				}
			}
		} else {
			println "${name} Header is not found in the response of ${url}"
		}
		return null
	}

	/**
	 * 
	 * @param url
	 * @param dstFile
	 * @return
	 */
	public File download(URL url, File distributedFile) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build()
		try {
			HttpGet get = new HttpGet(url.toURI())
			if (this.requestConfig != null) {
				// set Proxy config if necessary
				get.setConfig(requestConfig)
			}
			File downloaded = httpclient.execute(get,
					new FileDownloadResponseHandler(distributedFile))
			return downloaded
		} catch (Exception e) {
			throw new IllegalStateException(e)
		} finally {
			IOUtils.closeQuietly(httpclient)
		}
	}



	/**
	 * 
	 * @author kazurayam
	 *
	 */
	static class FileDownloadResponseHandler implements ResponseHandler<File> {
		private final File target

		public FileDownloadResponseHandler(File target) {
			this.target = target
		}

		@Override
		public File handleResponse(HttpResponse response)
		throws ClientProtocolException, IOException {
			InputStream source = response.getEntity().getContent()
			FileUtils.copyInputStreamToFile(source, this.target)
			return this.target
		}
	}
}
