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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoverageDebtCalculatorTest {
	private DecoratorContext context;
	private CoverageDebtCalculator calculator;

	@Before
	public void setUp() throws Exception {
		Settings settings = new Settings(new PropertyDefinitions(
				TechnicalDebtPlugin.class));
		calculator = new CoverageDebtCalculator(settings);
		context = mock(DecoratorContext.class);
	}

	@Test(expected = NoCalculation.class)
	public void testTotalPossibleCoverageWhenNoCoverageNeeded()
			throws Exception {
		when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(null);
		calculator.calculatePossibleDebt(context);
	}
	

	@Test
	public void testAbsoluteCoverageWhenNoCoverageNeeded()
			throws Exception {
		when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(null);
		assertEquals(0.0d, calculator.calculateActualDebt(context), 0);
	}


	@Test
	public void testTotalPossibleCoverage() throws NoCalculation {
		double complexity = 123;
		double randomCoverage = 2;

		when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(
				new Measure(CoreMetrics.COVERAGE, randomCoverage));

		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));

		assertEquals(complexity * CoverageDebtCalculator.COVERAGE_TARGET
				* TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY_DEFVAL
				/ DuplicationDebtCalculator.HOURS_PER_DAY,
				calculator.calculatePossibleDebt(context), 0.0001);
	}
	
	@Test
	public void testAbsoluteCoverageForCoveredFile() throws NoCalculation {
		double complexity = 123;
		double coverage = 100 * ( CoverageDebtCalculator.COVERAGE_TARGET + 0.01);

		when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(
				new Measure(CoreMetrics.COVERAGE, coverage));

		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));

		assertEquals(0.0d, calculator.calculateActualDebt(context), 0);
	}

	@Test
	public void testAbsoluteCoverage() throws NoCalculation {
		double complexity = 123;
		double coverage = 100 * ( CoverageDebtCalculator.COVERAGE_TARGET - 0.01);

		when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(
				new Measure(CoreMetrics.COVERAGE, coverage));

		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));

		assertEquals((CoverageDebtCalculator.COVERAGE_TARGET - coverage/100) * complexity * TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY_DEFVAL
				/ DuplicationDebtCalculator.HOURS_PER_DAY, calculator.calculateActualDebt(context), 0.0001);
	}

}
