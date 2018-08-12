/*
 * Copyright 2018 Hippo Seven
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

package com.hippo.js.runtime.rhino

import com.hippo.js.runtime.JsRequire
import org.junit.Test
import java.io.Reader

class RhinoJsContextTest {

  @Test
  fun testRequire() {
    val finder = ResourceJsFinder()
    rhinoJsContext(finder, 9).use { context ->
      val scope = context.newScope("scope")
      finder.require("main").use { reader ->
        scope.eval<Unit>(reader)
      }
    }
  }

  class ResourceJsFinder : JsRequire {
    override fun require(path: String): Reader = javaClass.classLoader.getResourceAsStream("$path.js").reader()
  }
}
