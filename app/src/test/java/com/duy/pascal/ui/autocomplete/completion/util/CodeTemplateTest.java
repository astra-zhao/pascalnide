/*
 *  Copyright (c) 2017 Tran Le Duy
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

package com.duy.pascal.ui.autocomplete.completion.util;

import junit.framework.TestCase;

/**
 * Created by Duy on 11/26/2017.
 */
public class CodeTemplateTest extends TestCase {
    public void testCreateProgramTemplate() throws Exception {
        String sample = CodeTemplate.createProgramTemplate("sample");
        System.out.println(sample);
    }

    public void testCreateUnitTemplate() throws Exception {
        String sample = CodeTemplate.createUnitTemplate("sample");
        System.out.println(sample);
    }

}