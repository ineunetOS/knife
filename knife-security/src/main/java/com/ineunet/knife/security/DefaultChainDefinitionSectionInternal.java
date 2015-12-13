/*
 * Copyright 2013-2016 iNeunet OpenSource and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ineunet.knife.security;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.springframework.beans.BeansException;

/**
 * @author Hilbert Wang
 * @since 2.0.0
 */
class DefaultChainDefinitionSectionInternal {

	private DefaultChainDefinitionSection defaultChainDefinitionSection;
	
	DefaultChainDefinitionSectionInternal(DefaultChainDefinitionSection defaultChainDefinitionSection) {
		this.defaultChainDefinitionSection = defaultChainDefinitionSection;
	}
	
	Section getObject() throws BeansException {
    	Ini ini = new Ini();
        ini.load(defaultChainDefinitionSection.filterChainDefinitions);
        //did they explicitly state a 'urls' section?  Not necessary, but just in case:
        Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
        if (CollectionUtils.isEmpty(section)) {
            //no urls section.  Since this _is_ a urls chain definition property, just assume the
            //default section contains only the definitions:
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        defaultChainDefinitionSection.section = section;
        return section;
    }
	
}
