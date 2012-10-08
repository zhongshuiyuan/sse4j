package org.sse.test;

import java.util.Date;

import org.sse.io.IdxParser;
import org.sse.io.Enums.AnalyzerType;

public class WordSplitTest {
	public static void main(String[] args) throws Exception {
		String info = "北京华烟云";
		Date date1 = new Date();

		String addr = IdxParser.getInstance().tokenize(info, AnalyzerType.IK);
		System.out.println(addr + "----IK--" + ((new Date()).getTime() - date1.getTime()));

		addr = IdxParser.getInstance().tokenize(info, AnalyzerType.SMARTCN);
		System.out.println(addr + "----SmartCN---" + ((new Date()).getTime() - date1.getTime()));

	}

}
