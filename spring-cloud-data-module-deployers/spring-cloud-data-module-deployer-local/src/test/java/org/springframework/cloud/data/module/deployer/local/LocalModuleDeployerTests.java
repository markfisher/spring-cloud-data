/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.data.module.deployer.local;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.data.core.ModuleCoordinates;
import org.springframework.cloud.data.core.ModuleDefinition;
import org.springframework.cloud.data.core.ModuleDeploymentId;
import org.springframework.cloud.data.core.ModuleDeploymentRequest;
import org.springframework.cloud.data.module.ModuleStatus;
import org.springframework.cloud.data.module.deployer.ModuleDeployer;
import org.springframework.cloud.stream.module.launcher.ModuleLauncher;
import org.springframework.cloud.stream.module.launcher.ModuleLauncherConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests deployment of the time-source and log-sink modules.
 *
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModuleLauncherConfiguration.class)
@DirtiesContext
public class LocalModuleDeployerTests {

	private static final String GROUP_ID = "org.springframework.cloud.stream.module";

	private static final String VERSION = "1.0.0.BUILD-SNAPSHOT";

	@Autowired
	private ModuleLauncher moduleLauncher;

	@Test
	public void timeToLogStream() throws InterruptedException {
		LocalModuleDeployer deployer = new LocalModuleDeployer(moduleLauncher);
		ModuleDefinition timeDefinition = new ModuleDefinition.Builder()
				.setGroup("test")
				.setName("time")
				.setParameter("spring.cloud.stream.bindings.output", "test.0")
				.build();
		ModuleDefinition logDefinition = new ModuleDefinition.Builder()
				.setGroup("test")
				.setName("log")
				.setParameter("spring.cloud.stream.bindings.input", "test.0")
				.build();
		ModuleCoordinates timeCoordinates = new ModuleCoordinates.Builder()
				.setGroupId(GROUP_ID)
				.setArtifactId("time-source")
				.setVersion(VERSION)
				.setClassifier("exec")
				.build();
		ModuleCoordinates logCoordinates = new ModuleCoordinates.Builder()
				.setGroupId(GROUP_ID)
				.setArtifactId("log-sink")
				.setVersion(VERSION)
				.setClassifier("exec")
				.build();
		ModuleDeploymentRequest time = new ModuleDeploymentRequest(timeDefinition, timeCoordinates);
		ModuleDeploymentRequest log = new ModuleDeploymentRequest(logDefinition, logCoordinates);
		ModuleDeploymentId logId = deployer.deploy(log);
		ModuleDeploymentId timeId = deployer.deploy(time);
		waitForDeployment(deployer, timeId, logId);
		deployer.undeploy(timeId);
		deployer.undeploy(logId);
		waitForUndeployment(deployer, timeId, logId);
	}

	private void waitForDeployment(ModuleDeployer deployer, ModuleDeploymentId... ids) throws InterruptedException {
		waitForState(ModuleStatus.State.deployed, deployer, ids);
	}

	private void waitForUndeployment(ModuleDeployer deployer, ModuleDeploymentId... ids) throws InterruptedException {
		// TODO: change when the 'undeployed' state is supported
		waitForState(ModuleStatus.State.unknown, deployer, ids);
	}

	private void waitForState(ModuleStatus.State state, ModuleDeployer deployer, ModuleDeploymentId... ids)
			throws InterruptedException {
		List<ModuleDeploymentId> done = new ArrayList<>();
		for (int i = 0; i < 60; i++) {
			for (ModuleDeploymentId id : ids) {
				if (state.equals(deployer.status(id).getState())) {
					done.add(id);
				}
			}
			if (ids.length == done.size()) {
				return;
			}
			Thread.sleep(1000);
		}
		throw new IllegalStateException(String.format("timed out waiting for state '%s' for: %s", state, ids));
	}
}
