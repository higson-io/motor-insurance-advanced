package pl.decerto.motorinsuranceadvanced.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.decerto.hyperon.runtime.profiler.ProfilerHelper;
import pl.decerto.hyperon.runtime.profiler.engine.EngineProfiler;
import pl.decerto.hyperon.runtime.profiler.jdbc.JdbcProfiler;

@RestController
@RequestMapping("/profilers")
public class ProfilersResource {

	@GetMapping("/parameters")
	public String hyperonParametersStatistics() {
		var invokeStats = EngineProfiler.PARAMETER.getInvokeStats();
		var loadStats = EngineProfiler.PARAMETER.getLoadStats();

		return ProfilerHelper.printSummaryAsHtml(invokeStats, "invoke statistics (gross)") +
			ProfilerHelper.printSummaryAsHtml(loadStats, "load statistics");
	}

	@GetMapping("/functions")
	public String hyperonFunctionsStatistics() {
		var invokeStats = EngineProfiler.FUNCTION.getInvokeStats();
		var loadStats = EngineProfiler.FUNCTION.getLoadStats();

		return ProfilerHelper.printSummaryAsHtml(invokeStats, "invoke statistics (gross)") +
			ProfilerHelper.printSummaryAsHtml(loadStats, "load statistics");
	}

	@GetMapping("/jdbc")
	public String hyperonJdbcStatistics() {
		return JdbcProfiler.getSingleton().printSummaryAsHtml();
	}

	@GetMapping("/clear")
	public void clear() {
		EngineProfiler.PARAMETER.getInvokeProfiler().clear();
		EngineProfiler.PARAMETER.getLoadProfiler().clear();
		EngineProfiler.FUNCTION.getInvokeProfiler().clear();
		EngineProfiler.FUNCTION.getLoadProfiler().clear();
		JdbcProfiler.getSingleton().clear();
	}
}
