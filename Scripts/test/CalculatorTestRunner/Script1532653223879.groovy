import org.junit.runner.JUnitCore
import org.junit.runner.Result
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil

Result result = JUnitCore.runClasses(junit.tutorial.AllTests.class)

WebUI.comment("Run:${result.getRunCount()}, Failure:${result.getFailureCount()}, Ignored:${result.getIgnoreCount()}")

if (result.getFailureCount() > 0) {
	KeywordUtil.markFailed("${result.getFailureCount()} test${result.getFailureCount() > 1 ? 's' : ''} failed")
}