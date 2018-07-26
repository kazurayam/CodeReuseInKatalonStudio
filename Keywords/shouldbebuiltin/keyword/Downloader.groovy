package shouldbebuiltin.keyword

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.http.Header
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
 * Copy&pasted from
 * https://gist.github.com/rponte/09ddc1aa7b9918b52029
 */
public class Downloader {

	// Proxy config
	RequestConfig requestConfig

	Downloader() {
		requestConfig = null
	}

	Downloader(RequestConfig rc) {
		this.requestConfig = rc
	}
	
	public Header[] getHeaders(URL url) {
		CloseableHttpClient httpclient = HttpClients.custom()
			.setRedirectStrategy(new LaxRedirectStrategy())
			.build()
		try {
			HttpHead head = new HttpHead(url.toURI())
			if (this.requestConfig != null) {
				// set Proxy config if necessary
				get.setConfig(requestConfig)
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

	public File download(URL url, File dstFile) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build()
		try {
			HttpGet get = new HttpGet(url.toURI())
			if (this.requestConfig != null) {
				// set Proxy config if necessary
				get.setConfig(requestConfig)
			}
			File downloaded = httpclient.execute(get, new FileDownloadResponseHandler(dstFile))
			return downloaded
		} catch (Exception e) {
			throw new IllegalStateException(e)
		} finally {
			IOUtils.closeQuietly(httpclient)
		}
	}



	static class FileDownloadResponseHandler implements ResponseHandler<File> {
		private final File target
		public FileDownloadResponseHandler(File target) {
			this.target = target
		}
		@Override
		public File handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			InputStream source = response.getEntity().getContent()
			FileUtils.copyInputStreamToFile(source, this.target)
			return this.target
		}
	}
}
