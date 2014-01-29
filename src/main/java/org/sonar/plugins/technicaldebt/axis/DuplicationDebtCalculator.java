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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class DuplicationDebtCalculator extends AxisDebtCalculator {
	public static final int NUMBER_OF_LINES_PER_BLOCK = 50;

	public static final Logger LOG = LoggerFactory.getLogger("TechnicalDebt");

	/**
	 * {@inheritDoc}
	 */
	public DuplicationDebtCalculator(Settings settings) {
		super(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	public double calculateAbsoluteDebt(DecoratorContext context) {
		Measure measure = context
				.getMeasure(CoreMetrics.DUPLICATED_LINES_DENSITY);

		if (!MeasureUtils.hasValue(measure)) {
			return 0.0;
		}

		return measure.getValue() / 100 * calculateTotalPossibleDebt(context);
	}

	/**
	 * {@inheritDoc}
	 */
	public double calculateTotalPossibleDebt(DecoratorContext context) {
		Measure measure = context.getMeasure(CoreMetrics.LINES);

		if (!MeasureUtils.hasValue(measure)) {
			if (context.getResource() != null && context.getResource().getKey() != null) {
				LOG.error("No lines for resource "
						+ context.getResource().getKey());
			}

			return 0.0;
		}

		return measure.getValue()
				/ NUMBER_OF_LINES_PER_BLOCK
				* settings
						.getDouble(TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS)
				/ HOURS_PER_DAY;

	}

	/**
	 * {@inheritDoc}
	 */
	public List<Metric> dependsOn() {
		return Arrays.asList(CoreMetrics.DUPLICATED_LINES_DENSITY,
				CoreMetrics.LINES);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Duplication";

	}
}
