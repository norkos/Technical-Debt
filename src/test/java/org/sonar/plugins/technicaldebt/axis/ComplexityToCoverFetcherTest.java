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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.fest.assertions.AssertExtension;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.plugins.cxx.coverage.NoCoverageMetrics;

import static org.junit.Assert.assertEquals;

public class ComplexityToCoverFetcherTest {

	private DecoratorContext context;
	private ComplexityToCoverFetcher sut;

	@Before
	public void setUp() throws Exception {
		sut = new ComplexityToCoverFetcher();
		context = mock(DecoratorContext.class);
	}

	@Test(expected = NoCalculation.class)
	public void testNoComplexity() throws Exception {
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(null);
		sut.getValue(context);
	}

	public void testComplexityForCode() throws Exception {
		double complexity = 10;
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));
		when(context.getMeasure(NoCoverageMetrics.NOT_COVERED_COMPLEXITY))
				.thenReturn(null);

		double value = sut.getValue(context);
		assertEquals(complexity, value, 0.00001d);
	}

	public void testComplexityForTest() throws Exception {
		double complexity = 10;
		double notCovered = 3;
		when(context.getMeasure(CoreMetrics.COMPLEXITY)).thenReturn(
				new Measure(CoreMetrics.COMPLEXITY, complexity));
		when(context.getMeasure(NoCoverageMetrics.NOT_COVERED_COMPLEXITY))
				.thenReturn(
						new Measure(NoCoverageMetrics.NOT_COVERED_COMPLEXITY,
								notCovered));

		double value = sut.getValue(context);
		assertEquals(complexity - notCovered, value, 0.00001d);
	}
}
