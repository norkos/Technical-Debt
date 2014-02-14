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

package org.sonar.plugins.technicaldebt;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(
    key = TechnicalDebtPlugin.DAILY_RATE,
    defaultValue = "" + TechnicalDebtPlugin.DAILY_RATE_DEFVAL,
    name = "Daily rate of a developer (in $)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_METHOD_COMPLEXITY,
    defaultValue = "" + TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL,
    name = "Average time to split a method that has a too high complexity (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS,
    defaultValue = "" + TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS_DEFVAL,
    name = "Average time to fix one block duplication block (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY,
    defaultValue = "" + TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY_DEFVAL,
    name = "Average time to cover complexity of one (in hours)",
    type = PropertyType.FLOAT
  )
})
public final class TechnicalDebtPlugin extends SonarPlugin {
  public static final String DAILY_RATE = "technicaldebt.daily.rate";
  public static final double DAILY_RATE_DEFVAL = 500.0;

  public static final String COST_METHOD_COMPLEXITY = "technicaldebt.split.meth";
  public static final double COST_METHOD_COMPLEXITY_DEFVAL = 0.2;

  public static final String COST_DUPLICATED_BLOCKS = "technicaldebt.dupli.blocks";
  public static final double COST_DUPLICATED_BLOCKS_DEFVAL = 2.0;

  public static final String COST_UNCOVERED_COMPLEXITY = "technicaldebt.uncovered.complexity";
  public static final double COST_UNCOVERED_COMPLEXITY_DEFVAL = 1.5;


  /**
   * {@inheritDoc}
   */
  public List getExtensions() {
    return Arrays.asList(
        TechnicalDebtMetrics.class,
        TechnicalDebtDecorator.class,
        TechnicalDebtWidget.class
        );
  }
}
