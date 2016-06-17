package com.blocks.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Evaluator {

	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static ScriptEngine engine = manager.getEngineByName("js");

	private static Object evaluate(String expression) {

		Object result = null;

		try {
			result = engine.eval(expression);
		} catch (ScriptException e) {
			throw new RuntimeException("ERROR: could not evaluate expression: " + expression);
		}

		return result;
	}

	public static Boolean evaluateToBoolean(String expression) {
		Object result = evaluate(expression);

		if (result != null) {
			return new Boolean(result.toString());
		}

		return null;
	}

	public static void main(String[] args) {
		boolean result = evaluateToBoolean("(4*5!=20) || ('abc'!='abc' || 1.2==1.2)");
		System.out.println(result);
	}
}
