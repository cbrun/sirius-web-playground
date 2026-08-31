/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.restfulemf.configuration;

import org.eclipse.sirius.web.restfulemf.GetResourceContentEventHandler;
import org.eclipse.sirius.web.restfulemf.ReplaceDocumentEventHandler;
import org.eclipse.sirius.web.restfulemf.ResourceSnapshotService;
import org.eclipse.sirius.web.restfulemf.application.RestfulEMFReadApplicationService;
import org.eclipse.sirius.web.restfulemf.application.RestfulEMFWriteApplicationService;
import org.eclipse.sirius.web.restfulemf.controllers.RestfulEMFExceptionHandler;
import org.eclipse.sirius.web.restfulemf.controllers.RestfulEMFResourceController;
import org.eclipse.sirius.web.restfulemf.services.ProjectDocumentsService;
import org.eclipse.sirius.web.restfulemf.services.ResourceFormatService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * Configures RESTful EMF when the matching Sirius Web feature is enabled.
 *
 * @since 2026.7.3
 */
@AutoConfiguration
@Conditional(OnRestfulEMFEnabled.class)
@EnableConfigurationProperties(RestfulEMFProperties.class)
@Import({ GetResourceContentEventHandler.class, ReplaceDocumentEventHandler.class, ResourceSnapshotService.class,
        RestfulEMFReadApplicationService.class, RestfulEMFWriteApplicationService.class, RestfulEMFExceptionHandler.class,
        RestfulEMFResourceController.class, ProjectDocumentsService.class, ResourceFormatService.class })
public class RestfulEMFAutoConfiguration {
}
