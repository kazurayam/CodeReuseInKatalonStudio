/**
 * This one liner Groovy script shows how to download a file and save it anywhere you want.
 * For example, the following script downloads a Groovy script from the Gist, and store it
 * into the Keywords directory of this Katalon Project.
 * This implies that a Keyword posted into Gist is quite easily shared by others.
 * 
 *  @author kazurayam
 */
// I you are behind the proxy of your orgianization, uncomment the following lines to set proxy config
//System.setProperty("proxySet", "true");
//System.setProperty("proxyHost", yourProxyIpAddress);
//System.setProperty("proxyPort", yourProxyPortNumber);

new File("Keywords/helloWorld.groovy") << new URL(
	"https://gist.githubusercontent.com/kazurayam/a80a93e4376eae3164adb8e021b78385/raw/f3cc41c07fec95a8840f716ffb931337154c7010/hellowWorld.groovy"
	).openStream()