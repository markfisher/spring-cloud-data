/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.server.config;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.dataflow.server.interim.StreamDefinitionController;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * Configuration for the Data Flow Server Controllers.
 *
 * @author Mark Fisher
 */
@Configuration
@EnableHypermediaSupport(type = HAL)
@ConditionalOnBean(AppDeployer.class)
@ComponentScan(basePackageClasses = StreamDefinitionController.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class DataFlowControllerAutoConfiguration {

	//@Configuration
	//@ComponentScan(basePackageClasses = StreamDefinitionController.class)
	//@ConditionalOnBean(AppDeployer.class)
	//public static class NewControllers {
	//}
}
