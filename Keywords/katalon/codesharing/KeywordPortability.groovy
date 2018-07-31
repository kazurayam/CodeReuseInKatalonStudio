package katalon.codesharing

import static java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileVisitOption
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipFile

import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.config.RequestConfig

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import groovy.util.AntBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 
 * @author kazurayam
 *
 */
public class KeywordPortability {

	private static final Logger logger_ = LoggerFactory.getLogger(KeywordPortability.class)

	static final String version = '0.1'

	static String getVersion() {
		return version
	}

	@Keyword
	static boolean includeCustomKeywords(
			String zipUrl,
			String username,
			String password,
			List<String> packages=[],
			Path destKeywords=Paths.get(System.getProperty('user.dir')).resolve('Keywords')) {

		// download the zip file from the given URL into the Downloads dir
		URL url = new URL(zipUrl)
		Credentials credentials = new UsernamePasswordCredentials(username, password)
		Path zipFile = downloadZip(url, credentials)

		// unzip the downloaded zip file into the Downloads dir
		Path downloadsDir = zipFile.getParent()
		List<Path> topLevelDirectories = unzip(new ZipFile(zipFile.toFile()), downloadsDir)
		assert topLevelDirectories.size() == 1

		/*
		 * Here we assume that we have the following directory tree extracted from the zip file
		 *   d Downloads
		 *     d MyCustomKeywords-0.2
		 *       d Keywords
		 *         d com
		 *           d kazurayam
		 *             d ksbackyard
		 *               f MyCustomKyewords.groovy
		 *       d Object Repository
		 *       d Profiles
		 *       ...
		 *     f MyCustomKeywords-0.2.zip
		 */

		// now copy the Keywords source files
		// from the downloaded archive
		// into the current Katalon Project
		Path src = downloadsDir.resolve(topLevelDirectories[0]).resolve('Keywords')
		Path dst = destKeywords
		List<Path> packagesToCopy = whichPackagesToCopy(packages)
		for (Path p : packagesToCopy) {
			def s = src.resolve(p)
			def d = dst.resolve(p)
			Files.createDirectories(d)
			def c = copyDirectory(s, d)
			logger_.info("copied ${c} file${(c > 1) ? 's' : ''} from ${s} to ${d}")
		}
		return true
	}


	/**
	 * 
	 * @param zipUrl
	 * @return
	 */
	static Path downloadZip(URL zipUrl, Credentials credentials) {
		if (zipUrl == null) {
			throw new IllegalArgumentException("zipUrl is required")
		}

		Downloader downloader = new Downloader()

		// make a HEAD request to the URL in order to find recommended file name
		Header[] headers = downloader.getAllHeaders(zipUrl, credentials)
		if (headers != null) {
			// we will save the downloaded zip file into this directory
			Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')
			String filename = downloader.getContentDispositionFilename(headers)
			if (filename == null) {
				filename = this.getClass().getName() + '.zip'
			}
			File zipFile = downloadsDir.resolve(filename).toFile()
			// now download the zipFile
			downloader.download(zipUrl, credentials, zipFile)
			return zipFile.toPath()
		} else {
			throw new IllegalStateException("HEAD ${zipUrl} failed")
		}
	}


	/**
	 * 
	 * @param zipFile
	 * @param outputDir
	 * @return
	 */
	static List<Path> unzip(ZipFile zip, Path outputDir) {
		Set<String> topLevelDirectories = new HashSet<String>()
		zip.entries().each {
			if (it.isDirectory()) {
				//logger_.debug("it is a Directory ${it.toString()}")
				topLevelDirectories.add(it.toString().split('/')[0])
			} else {
				//logger_.debug("it is a File ${it.toString()}")
				def fOut = outputDir.resolve(it.getName()).toFile()
				//create output dir if not exists
				new File(fOut.parent).mkdirs()
				def fos = new FileOutputStream(fOut)
				//logger_.debug("name:${it.name}, size:${it.size}")
				def buf = new byte[it.getSize()]
				def len = zip.getInputStream(it).read(buf) //println zip.getInputStream(it).text
				//logger_.debug("it.getSize()=${it.getSize()} len=${len}")
				if (it.getSize() > 0) {
					fos.write(buf, 0, len)
				}
				fos.close()
			}
		}
		zip.close()

		// return the list of top-level directories extracted from the ZIP file
		List<Path> dirs = []
		for (String dir : topLevelDirectories) {
			dirs.add(outputDir.resolve(dir))
		}
		return dirs
	}

	/**
	 * Copies descendent files and directories recursively
	 * from the source directory into the target directory.
	 *
	 * @param source a directory from which child files and directories are copied
	 * @param target a directory into which child files and directories are copied
	 * @return number of regular files copied
	 */
	static int copyDirectory(Path source, Path target) {
		if (source == null) {
			throw new IllegalArgumentException('source is null')
		}
		if (!Files.exists(source)) {
			throw new IllegalArgumentException("${source.normalize().toAbsolutePath()} does not exist")
		}
		if (!Files.isDirectory(source)) {
			throw new IllegalArgumentException("${source.normalize().toAbsolutePath()} is not a directory")
		}
		if (!Files.isReadable(source)) {
			throw new IllegalArgumentException("${source.normalize().toAbsolutePath()} is not readable")
		}
		if (target == null) {
			throw new IllegalArgumentException('target is null')
		}
		int copyCount = 0
		Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
				Integer.MAX_VALUE,
				new SimpleFileVisitor<Path>() {
					@Override
					FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
						Path targetdir = target.resolve(source.relativize(dir))
						try {
							Files.copy(dir, targetdir)
						} catch (FileAlreadyExistsException e) {
							if (!Files.isDirectory(targetdir))
								throw e
						}
						return CONTINUE
					}
					@Override
					FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
						Path targetFile = target.resolve(source.relativize(file))
						if (Files.exists(targetFile)) {
							Files.delete(targetFile)
						}
						Files.copy(file, targetFile)
						copyCount += 1
						return CONTINUE
					}
				}
				)
		return copyCount
	}

	/**
	 *
	 * @param packages
	 * @return
	 */
	static List<Path> whichPackagesToCopy(List<String> packages) {
		println "#whichPackagesToCopy packages=${packages}"
		List<Path> subpaths = new ArrayList<Path>()
		for (String pkg : packages) {
			String[] nodes = pkg.split('\\.')     // ['com', 'kazurayam', 'ksbackyard'] as String[]
			println "#whichPackagesToCopy nodes=${nodes}"
			if (nodes.length > 0) {
				Path p = Paths.get(nodes[0])
				for (int i = 1; i < nodes.length; i++) {
					p = p.resolve(nodes[i])
				}
				subpaths.add(p)
			}
		}
		return subpaths
	}
}
