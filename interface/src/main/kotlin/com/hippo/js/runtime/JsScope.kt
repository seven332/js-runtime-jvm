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

package com.hippo.js.runtime

import java.io.Closeable
import java.io.Reader

/**
 * A `JsScope` is a operational single-threading pure js runtime.
 * Js scripts can only run directly in `JsScope`s through [eval].
 * JsScope`s partially support commonJS Module. It supports `require()`
 * and `module.exports`.
 */
interface JsScope : Closeable {

  /**
   * Evaluates the string as JavaScript source.
   * Returns the result of evaluating the source.
   */
  fun <T> eval(source: String): T

  /**
   * Evaluates the reader as JavaScript source.
   * Returns the result of evaluating the source.
   * All characters of the reader are consumed.
   * This closes the Reader.
   */
  fun <T> eval(reader: Reader): T

  /**
   * Closes the `JsScope` and releases any resources associated with it.
   * Once the `JsScope` has been closed, further [eval] invocations
   * will throw an exception. Closing a previously closed `JsScope` has no effect.
   */
  override fun close()
}
