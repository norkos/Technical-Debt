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

package org.sonar.plugins.technicaldebt.it;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.ResourceQuery;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StrutsIT {

  private static Sonar sonar;
  private static final String PROJECT_STRUTS = "CxxPlugin:Debt";
  private static final String FILE_ACTION = "CxxPlugin:Debt:lib/component1.cc";

  @BeforeClass
  public static void buildServer() {
    sonar = Sonar.create("http://localhost:9000");
  }

  @Test
  public void isAnalyzed() {
    assertThat(sonar.find(new ResourceQuery(PROJECT_STRUTS)).getName(), is("Debt"));
  }

  @Test
  public void projectsMetrics() {
    assertThat(getProjectMeasure("technical_debt_repart").getData(),
        is("Comments=4.68;Complexity=12.63;Coverage=32.63;Design=3.49;Duplication=29.87;Violations=16.66"));

    assertThat(getProjectMeasure("technical_debt").getValue(), is(278655.3));
    assertThat(getProjectMeasure("technical_debt_ratio").getValue(), is(22.7));
    assertThat(getProjectMeasure("technical_debt_days").getValue(), is(557.3));
  }


  @Test
  public void filesMetrics() {
    assertThat(getFileMeasure("technical_debt").getValue(), is(662.5));
    assertThat(getFileMeasure("technical_debt_ratio").getValue(), is(26.5));
    assertThat(getFileMeasure("technical_debt_days").getValue(), is(1.3));
    assertThat(getFileMeasure("technical_debt_repart").getData(), is("Coverage=75.47;Violations=24.52"));
  }

  private Measure getFileMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(FILE_ACTION, metricKey)).getMeasure(metricKey);
  }

  private Measure getProjectMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PROJECT_STRUTS, metricKey)).getMeasure(metricKey);
  }
}
