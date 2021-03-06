/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DefinitionLibraryPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports = DefinitionLibraryPatientDataDefinition.class)
public class DefinitionLibraryPatientDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private AllDefinitionLibraries definitionLibraries;

    @Autowired
    private PatientDataService patientDataService;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
        DefinitionLibraryPatientDataDefinition def = (DefinitionLibraryPatientDataDefinition) definition;
        PatientDataDefinition referencedDefinition = definitionLibraries.getDefinition(PatientDataDefinition.class, def.getDefinitionKey());

        // parameters without values explicitly set should be mapped straight through
        Mapped<PatientDataDefinition> mapped = Mapped.mapStraightThrough(referencedDefinition);
        if (def.getParameterValues() != null) {
            for (Map.Entry<String, Object> e : def.getParameterValues().entrySet()) {
                mapped.addParameterMapping(e.getKey(), e.getValue());
            }
        }
        return patientDataService.evaluate(mapped, context);
    }

}
