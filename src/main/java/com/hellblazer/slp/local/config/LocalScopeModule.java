/*
 * (C) Copyright 2014 Chiral Behaviors, All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.hellblazer.slp.local.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.slp.config.ServiceScopeConfiguration;

/**
 * @author hhildebrand
 * 
 */
@JsonSubTypes({ @Type(value = LocalScopeModule.class, name = "localScope") })
public class LocalScopeModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public LocalScopeModule() {
        super("Gossip Scope Module");
    }

    public LocalScopeModule(String name) {
        super(name);
    }

    @Override
    public void setupModule(SetupContext context) {

        context.setMixInAnnotations(ServiceScopeConfiguration.class,
                                    LocalScopeConfiguration.class);
        super.setupModule(context);
    }
}
