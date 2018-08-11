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
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptRuntime
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject

internal class RequireFunction(private val require: JsRequire) : BaseFunction() {

  private val exportsCache = mutableMapOf<String, Any>()

  override fun call(cx: Context, scope: Scriptable?, thisObj: Scriptable?, args: Array<out Any>?): Any {
    if (args == null || args.isEmpty()) {
      throw ScriptRuntime.throwError(cx, scope, "require() needs one argument")
    }

    val path = Context.jsToJava(args[0], String::class.java) as String

    var exports = exportsCache[path]
    if (exports == null) {
      val reader = require.require(path)
      exports = reader.use {
        val moduleScope = cx.initStandardObjects()
        val moduleObject = cx.newObject(moduleScope)
        val exportsObject = cx.newObject(moduleScope)
        ScriptableObject.putProperty(moduleObject, "exports", exportsObject)
        ScriptableObject.putProperty(moduleScope, "module", moduleObject)
        cx.evaluateReader(moduleScope, it, "<module:$path>", 1, null)
        val newModuleObject = ScriptRuntime.toObject(cx, moduleScope, ScriptableObject.getProperty(moduleScope, "module"))
        ScriptableObject.getProperty(newModuleObject, "exports")!!
      }
      exportsCache[path] = exports
    }

    return exports
  }

  override fun construct(cx: Context, scope: Scriptable, args: Array<Any>): Scriptable {
    throw ScriptRuntime.throwError(cx, scope, "require() can not be invoked as a constructor")
  }

  override fun getFunctionName(): String {
    return "require"
  }

  override fun getArity(): Int {
    return 1
  }

  override fun getLength(): Int {
    return 1
  }
}
