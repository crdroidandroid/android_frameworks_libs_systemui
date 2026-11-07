/*
 * Copyright (C) 2026 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.monet;

/**
 * Shared normalization for per-palette chroma and value (tone) factors.
 *
 * Factor arrays are indexed as {@link #PRIMARY}, {@link #SECONDARY}, {@link #TERTIARY},
 * {@link #NEUTRAL}. Missing, null or non-finite entries fall back to {@link #DEFAULT}.
 */
final class MonetFactors {
    static final int PRIMARY = 0;
    static final int SECONDARY = 1;
    static final int TERTIARY = 2;
    static final int NEUTRAL = 3;
    static final int PALETTE_COUNT = 4;

    static final float DEFAULT = 1f;
    static final float MAX = 6f;
    /** Chroma may go to zero (fully desaturated palette). */
    static final float MIN_CHROMA = 0f;
    /** Value is floored so a palette can never collapse to all-black. */
    static final float MIN_VALUE = 0.1f;

    private MonetFactors() {}

    static float clampChroma(float factor) {
        return clamp(factor, MIN_CHROMA);
    }

    static float clampValue(float factor) {
        return clamp(factor, MIN_VALUE);
    }

    static float[] normalizeChromas(float[] factors) {
        return normalize(factors, MIN_CHROMA);
    }

    static float[] normalizeValues(float[] factors) {
        return normalize(factors, MIN_VALUE);
    }

    static float valueAt(float[] factors, int index) {
        return factors == null || factors.length <= index ? DEFAULT : clampValue(factors[index]);
    }

    static boolean allDefault(float[] factors) {
        for (float factor : factors) {
            if (factor != DEFAULT) return false;
        }
        return true;
    }

    static double clampTone(double tone) {
        return Math.max(0.0, Math.min(100.0, tone));
    }

    private static float clamp(float factor, float min) {
        return Float.isFinite(factor) ? Math.max(min, Math.min(MAX, factor)) : DEFAULT;
    }

    private static float[] normalize(float[] factors, float min) {
        float[] normalized = new float[PALETTE_COUNT];
        for (int i = 0; i < PALETTE_COUNT; i++) {
            normalized[i] = factors != null && i < factors.length
                    ? clamp(factors[i], min) : DEFAULT;
        }
        return normalized;
    }
}
