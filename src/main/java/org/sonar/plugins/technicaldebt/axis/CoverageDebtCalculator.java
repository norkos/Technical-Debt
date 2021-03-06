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

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.cxx.coverage.NoCoverageMetrics;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class CoverageDebtCalculator extends AxisDebtCalculator {

	public static final double COVERAGE_TARGET = 0.8;
	private ValueFetcher complexityFetcher;

	/**
	 * {@inheritDoc}
	 */
	public CoverageDebtCalculator(Settings settings, ValueFetcher fetcher) {
		super(settings);

		this.complexityFetcher = fetcher;
	}

	/**
	 * {@inheritDoc}
	 */
	public double calculateActualDebt(DecoratorContext context) {
		Measure coverage = context.getMeasure(CoreMetrics.COVERAGE);

		if (!MeasureUtils.hasValue(coverage)) {
			return 0.0;
		}

		double complexity;
		try {
			complexity = complexityFetcher.getValue(context);
		} catch (NoCalculation e) {
			return 0.0;
		}

		// It is not reasonable to have an objective at 100%, so target is 80%
		// for coverage
		double gap = (COVERAGE_TARGET - coverage.getValue() / 100) * complexity;

		return (gap > 0.0 ? gap : 0.0)
				* settings
						.getDouble(TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY)
				/ HOURS_PER_DAY;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws NoCalculation
	 */
	public double calculatePossibleDebt(DecoratorContext context)
			throws NoCalculation {
		Measure coverage = context.getMeasure(CoreMetrics.COVERAGE);

		if (!MeasureUtils.hasValue(coverage)) {
			throw new NoCalculation();
		}

		double complexity = complexityFetcher.getValue(context);

		return COVERAGE_TARGET
				* complexity
				* settings
						.getDouble(TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY)
				/ HOURS_PER_DAY;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Metric> dependsOn() {
		return Arrays.asList(CoreMetrics.COMPLEXITY, CoreMetrics.COVERAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Coverage";

	}
}
