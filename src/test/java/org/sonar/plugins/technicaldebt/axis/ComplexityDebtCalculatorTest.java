/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.technicaldebt.axis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.plugins.cxx.coverage.NoCoverageMetrics;
import org.sonar.plugins.cxx.cppncss.CxxCppNcssSensor;
import org.sonar.plugins.cxx.distance.DistanceMetrics;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

public class ComplexityDebtCalculatorTest {

	private DecoratorContext context;
	private ComplexityDebtCalculator calculator;

	@Before
	public void setUp() throws Exception {
		Settings settings = new Settings(new PropertyDefinitions(
				TechnicalDebtPlugin.class));
		calculator = new ComplexityDebtCalculator(settings);
		context = mock(DecoratorContext.class);
	}

	@Test(expected = NoCalculation.class)
	public void testTotalPossibleComplexityWhenNoComplexity() throws Exception {
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(null);
		calculator.calculatePossibleDebt(context);
	}

	@Test
	public void testTotalPossibleComplexity() throws Exception {
		double complexityDelta = 12;
		double complexity = CxxCppNcssSensor.DEFAULT_MAX_COMPLEXITY
				+ complexityDelta;
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));

		assertEquals(complexityDelta
				* TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL
				/ DuplicationDebtCalculator.HOURS_PER_DAY,
				calculator.calculatePossibleDebt(context), 0.0001);
	}

	@Test
	public void testTotalPossibleComplexityWithSomeNotCovered() throws Exception {
		double complexityDelta = 12;
		double complexity = CxxCppNcssSensor.DEFAULT_MAX_COMPLEXITY
				+ complexityDelta;
		double notCoveredComplexity = 2;
		
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));
		
		when(context.getMeasure(NoCoverageMetrics.NOT_COVERED_COMPLEXITY)).thenReturn(
				new Measure(NoCoverageMetrics.NOT_COVERED_COMPLEXITY, notCoveredComplexity));

		assertEquals((complexityDelta - notCoveredComplexity)
				* TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL
				/ DuplicationDebtCalculator.HOURS_PER_DAY,
				calculator.calculatePossibleDebt(context), 0.0001);
	}
	
	@Test(expected = NoCalculation.class)
	public void testTotalPossibleComplexityNotPossible() throws Exception {
		double complexityDelta = -1;
		double complexity = CxxCppNcssSensor.DEFAULT_MAX_COMPLEXITY
				+ complexityDelta;
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));

		calculator.calculatePossibleDebt(context);
	}
	
	@Test
	public void testAbsoluteComplexity() throws Exception {
		double complexity = 12;
		when(context.getMeasure(DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH)).thenReturn(
				new Measure(DistanceMetrics.DISTANCE_COMPLEXITY_LENGTH, complexity));

		assertEquals(complexity
				* TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL
				/ DuplicationDebtCalculator.HOURS_PER_DAY,
				calculator.calculateActualDebt(context), 0.0001);
	}
}
