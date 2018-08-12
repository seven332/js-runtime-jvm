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

/**
 * A `JsContext` is a single-threading top-level js runtime, holding several [JsScope]s.
 * But js scripts can only run directly in `JsScope`s instead of `JsContext`s.
 */
interface JsContext : Closeable {

  /**
   * Creates a new `JsScope` with the id. Each `JsScope` in the same `JsContext`
   * is independent in js level. Throws an exception if the id is bound to an `JsScope`.
   */
  fun newScope(id: String): JsScope

  /**
   * Returns the `JsScope` with the id, `null` if no one has the id.
   */
  fun getScope(id: String): JsScope?

  /**
   * Closes the `JsContext`, all of its `JsScope` and releases any resources associated with it.
   * Once the `JsContext` has been closed, further [newScope] or its `JsScope`s invocations
   * will throw an exception. Closing a previously closed `JsContext` has no effect.
   */
  override fun close()
}
