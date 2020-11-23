package com.alm.octane.infra;

public class Constants {
	public static final String errorPrefix = "<OctaneJBehaveGherkinReporter Error> ";
	public static final String XML_VERSION = "1";
	public static final String TAG_ID1 = "TID";
	public static final String TAG_ID2 = "BSPID";

	public static final String LINE_SEPARATOR = System.lineSeparator();

	//Step statuses
	public static final String PASSED = "passed";
	public static final String FAILED = "failed";
	public static final String PENDING = "pending";
	public static final String SKIPPED = "skipped";

	public static final String JB_LEFT_PARAM_CHAR = "\uFF5F";
	public static final String JB_RIGHT_PARAM_CHAR = "\uFF60";

	public static final String GHERKIN_LEFT_PARAM_CHAR = "<";
	public static final String GHERKIN_RIGHT_PARAM_CHAR = ">";


}
