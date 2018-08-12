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
import java.io.Reader

internal class RhinoJsContext(private val finder: JsRequire, prepare: (Context) -> Unit) : JsContext {

  private val context = Context.enter().also(prepare)
  private val require by lazy { RequireFunction(finder) }

  private val thread = Thread.currentThread()
  private var closed = false

  private val scopes = mutableMapOf<String, RhinoJsScope>()

  internal fun checkThread(action: String) {
    if (Thread.currentThread() != thread) {
      throw IllegalStateException("$action called in a different thread")
    }
  }

  private fun checkClosed(action: String) {
    if (closed) {
      throw IllegalStateException("$action called on recycled JsContext")
    }
  }

  override fun newScope(id: String): JsScope {
    checkThread("newScope")
    checkClosed("newScope")

    if (scopes.containsKey(id)) {
      throw IllegalStateException("There is a JsScope with the id: $id")
    }

    // It's a safe one. Java stuff is not allowed.
    val scope = context.initSafeStandardObjects()
    ScriptableObject.putProperty(scope, "require", require)
    val jsScope = RhinoJsScope(this, id, scope)
    scopes[id] = jsScope
    return jsScope
  }

  override fun getScope(id: String): JsScope? {
    checkThread("getScope")
    checkClosed("getScope")
    return scopes[id]
  }

  internal fun <T> eval(scope: ScriptableObject, source: String): T {
    // TODO A better way to covert result
    return context.evaluateString(scope, source, "<source>", 1, null) as T
  }

  internal fun <T> eval(scope: ScriptableObject, reader: Reader): T {
    // TODO A better way to covert result
    return context.evaluateReader(scope, reader, "<reader>", 1, null) as T
  }

  internal fun closeScope(id: String) {
    scopes.remove(id)
  }

  override fun close() {
    checkThread("close")

    if (!closed) {
      closed = true
      scopes.values.forEach { it.close() }
      Context.exit()
    }
  }
}

fun rhinoJsContext(finder: JsRequire, optimizationLevel: Int): JsContext = RhinoJsContext(finder) { context ->
  context.optimizationLevel = optimizationLevel
}
