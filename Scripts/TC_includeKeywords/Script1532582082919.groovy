import com.kazurayam.KatalonProperties
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable

def versionOfMyCustomKeywords = '0.2'

def gitRepos = "https://github.com/kazurayam/MyCustomKeywords/archive/${versionOfMyCustomKeywords}.zip"
def packages = ['com.kazurayam.ksbackyard']

// KatalonProperties class is in MultiSourcedProperites-*.jar in the External Libraries
KatalonProperties props = new KatalonProperties()

// override GlobalVariable with value loaded from the katalon.properties file
GlobalVariable.GitHubUsername = props.getProperty('GlobalVariable.GitHubUsername') ?: '?'
GlobalVariable.GitHubPassword = props.getProperty('GlobalVariable.GitHubPassword') ?: '?'

def result = CustomKeywords.'shouldbebuiltin.keyword.Portability.includeCustomKeywords'(
	gitRepos,
	GlobalVariable.GitHubUsername,
	GlobalVariable.GitHubPassword, 
    packages)

if (result) {
	WebUI.comment(">>> Successfully downloaded ${gitRepos} and included Keywords ${packages}")
	WebUI.comment(">>> Possibly you need to force Katalon Studio to compile Groovy codes" + 
		" by removing ${System.getProperty('user.dir')}/Keywords directory")
	WebUI.comment(">>> Possibly you need to stop Katalon Stduio and restart it")
}
