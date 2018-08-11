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

import com.hippo.js.runtime.JsScope
import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject
import java.io.Reader

internal class RhinoJsScope(private val context: Context, private val scope: ScriptableObject) : JsScope {

  private var closed = false

  private fun checkClosed(errorMessage: String) {
    if (closed) {
      throw IllegalStateException(errorMessage)
    }
  }

  override fun <T> eval(source: String): T {
    checkClosed("eval called on recycled JsScope")
    return context.evaluateString(scope, source, "<source>", 1, null) as T
  }

  override fun <T> eval(reader: Reader): T {
    checkClosed("eval called on recycled JsScope")
    return context.evaluateReader(scope, reader, "<reader>", 1, null) as T
  }

  override fun close() {
    closed = true
  }
}
