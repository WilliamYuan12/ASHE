package agent;

import ashe.Ashe;

public class Test {
	
	public static void main(String[] args) {
		try {
			AgentBase ashe = new Ashe(1);
			ashe.newMatch();
			String[] tests = {
				"MATCHSTATE:0:30::9s8h|",
				"MATCHSTATE:0:30:c:9s8h|",
				"MATCHSTATE:0:30:cc/:9s8h|/8c8d5c",
				"MATCHSTATE:0:30:cc/c:9s8h|/8c8d5c",
				"MATCHSTATE:0:30:cc/cc/:9s8h|/8c8d5c/6s",
				"MATCHSTATE:0:30:cc/cc/r500:9s8h|/8c8d5c/6s",
				"MATCHSTATE:0:30:cc/cc/r500c/:9s8h|/8c8d5c/6s/2d",
				"MATCHSTATE:0:30:cc/cc/r500c/r1250:9s8h|/8c8d5c/6s/2d",
				"MATCHSTATE:0:30:cc/cc/r500c/r1250f:9s8h|/8c8d5c/6s/2d",
				"MATCHSTATE:1:31::|JdTc",
				"MATCHSTATE:1:31:r300:|JdTc",
				"MATCHSTATE:1:31:r300r900:|JdTc",
				"MATCHSTATE:1:31:r300r900c/:|JdTc/6dJc9c",
				"MATCHSTATE:1:31:r300r900c/r1800:|JdTc/6dJc9c",
				"MATCHSTATE:1:31:r300r900c/r1800r3600:|JdTc/6dJc9c",
				"MATCHSTATE:1:31:r300r900c/r1800r3600r9000:|JdTc/6dJc9c",
				"MATCHSTATE:1:31:r300r900c/r1800r3600r9000c/:|JdTc/6dJc9c/Kh",
				"MATCHSTATE:1:31:r300r900c/r1800r3600r9000c/r20000:|JdTc/6dJc9c/Kh",
				"MATCHSTATE:1:31:r300r900c/r1800r3600r9000c/r20000c/:JsTh|JdTc/6dJc9c/Kh/Qc",
				"MATCHSTATE:0:32::9s8h|",
				"MATCHSTATE:0:32:c:9s8h|",
				"MATCHSTATE:0:32:cc/:9s8h|/8c8d5c",
				"MATCHSTATE:0:32:cc/r20000:9s8h|/8c8d5c",
				"MATCHSTATE:0:32:cc/r20000c/:9s8h|/8c8d5c/6s",
				"MATCHSTATE:0:32:cc/cc/r20000c//:9s8h|AcKc/8c8d5c/6s/2c",
			};
			for (int i = 0; i < tests.length; i++) {
				System.out.println(tests[i]);
				ashe.parse(tests[i]);
				System.out.println(ashe.stateReport());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
