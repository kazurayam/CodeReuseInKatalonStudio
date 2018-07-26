import com.kazurayam.KatalonProperties
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

/**
 * 
 * @author kazurayam
 *
 */

// tag stands for version of artifacts developed by the MyCustomKeywords project
def tagOfMyCustomKeywords = '0.2'

// URL of the zip file of the MyCustomKeywords project
def gitReposZip = "https://github.com/kazurayam/MyCustomKeywords/archive/${tagOfMyCustomKeywords}.zip"

// package name of custom keywords to be included into this project
def myPackages = ['com.kazurayam.ksbackyard']

// the KatalonProperties class is contained in MultiSourcedProperites-*.jar registered in the Project's External Libraries
KatalonProperties props = new KatalonProperties()

// override GlobalVariable with value loaded from the %USERPROFILE%\katalon.properties file
GlobalVariable.GitHubUsername = props.getProperty('GlobalVariable.GitHubUsername') ?: '?'
GlobalVariable.GitHubPassword = props.getProperty('GlobalVariable.GitHubPassword') ?: '?'

def result = CustomKeywords.'shouldbebuiltin.keyword.KeywordPortability.includeCustomKeywords'(
	gitReposZip,
	GlobalVariable.GitHubUsername,
	GlobalVariable.GitHubPassword, 
    myPackages)

if (result) {
	WebUI.comment(">>> Successfully downloaded ${gitReposZip} and included Keywords ${myPackages}")
	WebUI.comment(">>> Possibly you need to force Katalon Studio to compile Groovy codes" + 
		" by removing ${System.getProperty('user.dir')}/Keywords directory")
	WebUI.comment(">>> Possibly you need to stop Katalon Stduio and restart it")
}
