package codesharing

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
import org.apache.http.client.config.RequestConfig

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import groovy.util.AntBuilder

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

		// download the zip file from the given URL into the Downloads dir
		Path zipFile = downloadZip(zipUrl, username, password)

		// unzip the downloaded zip file into the Downloads dir
		Path downloadsDir = zipFile.getParent()
		List<Path> topLevelDirectories = unzip(new ZipFile(zipFile.toFile()), downloadsDir)
		assert topLevelDirectories.size() == 1


		/*
		 * Here we assume that we have the following directory tree
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
		Path dst = Paths.get(System.getProperty('user.dir')).resolve('Keywords')
		copyDirectory(src, dst)
		println "copied files from ${src} into ${dst}"
		return true
	}

	/**
	 * 
	 * @param zipUrl
	 * @return
	 */
	static Path downloadZip(String zipUrl, String username='', String password='') {
		if (zipUrl == null) {
			throw new IllegalArgumentException("zipUrl is required")
		}

		URL url = new URL(zipUrl)

		// create the helper class to download files via HTTP with Proxy awareness
		Downloader downloader = new Downloader()

		// make a HEAD request to the URL in order to be informed of the recommended file name
		String filename = downloader.getContentDispositionFilename(url)

		// we will save the downloaded zip file into this directory
		Path downloadsDir = Paths.get(System.getProperty('user.home'), 'Downloads')
		File zipFile = downloadsDir.resolve(filename).toFile()

		// now download the zipFile
		downloader.download(url, zipFile)

		return zipFile.toPath()
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
				//println "it is a Directory ${it.toString()}"
				topLevelDirectories.add(it.toString().split('/')[0])
			} else {
				//println "it is a File ${it.toString()}"
				def fOut = outputDir.resolve(it.getName()).toFile()
				//create output dir if not exists
				new File(fOut.parent).mkdirs()
				def fos = new FileOutputStream(fOut)
				//println "name:${it.name}, size:${it.size}"
				def buf = new byte[it.getSize()]
				def len = zip.getInputStream(it).read(buf) //println zip.getInputStream(it).text
				//println "it.getSize()=${it.getSize()} len=${len}"
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
	 * @param source a directory from which files and directories are copied
	 * @param target a directory into which files and directories are copied
	 * @return
	 */
	static boolean copyDirectory(Path source, Path target) {
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
					return CONTINUE
				}
			}
		)
	}
}
