/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.llama.cpp;

import io.gravitee.llama.cpp.nativelib.LlamaLibLoader;
import java.lang.foreign.Arena;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the native libraries can actually be loaded and the backends
 * registered on the current platform. It does NOT require a model file, so it
 * is a cheap end-to-end check of the FFM bindings + DLL loading.
 *
 * <p>It only runs when {@code LLAMA_CPP_LIB_PATH} is set (CI / local dev with
 * the prebuilt llama.cpp DLLs). On machines without the native libraries it is
 * silently skipped so it never breaks a normal {@code mvn test} run.
 */
public class SmokeTest {

  @Test
  void loadNativeLibraries() {
    String libPath = System.getenv("LLAMA_CPP_LIB_PATH");
    Assumptions.assumeTrue(libPath != null && !libPath.isBlank(),
        "LLAMA_CPP_LIB_PATH not set; skipping native load verification");

    String loaded = LlamaLibLoader.load();
    System.out.println("[SMOKE] Libraries loaded from: " + loaded);

    LlamaRuntime.llama_backend_init();
    try (Arena arena = Arena.ofConfined()) {
      LlamaRuntime.ggml_backend_load_all_from_path(arena, loaded);
      BackendRegistry.printSummary();
    }
  }
}
