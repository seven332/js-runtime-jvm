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

import com.hippo.js.runtime.JsContext
import com.hippo.js.runtime.JsRequire
import com.hippo.js.runtime.JsScope
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject

internal class RhinoJsContext(private val finder: JsRequire, prepare: (Context) -> Unit) : JsContext {

  private val context = Context.enter().also(prepare)
  private val require by lazy { RequireFunction(finder) }
  private var closed = false

  override fun newScope(): JsScope {
    if (closed) {
      throw IllegalStateException("Can't create a new JS scope from a closed JS context.")
    }

    // TODO check thread
    // TODO record all new scopes

    val scope = context.initSafeStandardObjects()
    ScriptableObject.putProperty(scope, "require", require)
    return RhinoJsScope(context, scope)
  }

  override fun close() {
    if (!closed) {
      closed = true
      Context.exit()
      // TODO close all JsScopes
    }
  }
}

fun rhinoJsContext(finder: JsRequire, optimizationLevel: Int): JsContext = RhinoJsContext(finder) { context ->
  context.optimizationLevel = optimizationLevel
}
