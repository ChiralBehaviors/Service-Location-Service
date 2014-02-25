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

import com.hellblazer.slp.ServiceScope;
import com.hellblazer.slp.config.ServiceScopeConfiguration;
import com.hellblazer.slp.local.LocalScope;

/**
 * @author hhildebrand
 * 
 */
public class LocalScopeConfiguration implements ServiceScopeConfiguration {

    private int notificationThreads = 2;

    /* (non-Javadoc)
     * @see com.hellblazer.slp.config.ServiceScopeConfiguration#construct()
     */
    @Override
    public ServiceScope construct() throws Exception {
        return new LocalScope(notificationThreads);
    }

}
