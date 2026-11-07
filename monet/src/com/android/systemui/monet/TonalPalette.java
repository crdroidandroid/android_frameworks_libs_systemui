/*
 * Copyright (C) 2024 The Android Open Source Project
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

import com.google.ux.material.libmonet.hct.Hct;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TonalPalette {
    /** Untouched source palette. Never mutated: libmonet hands out its key color by reference. */
    private final com.google.ux.material.libmonet.palettes.TonalPalette mMaterialTonalPalette;
    private final float mChromaFactor;
    /**
     * @deprecated Do not use. For color system only
     */
    @Deprecated
    public final List<Integer> allShades;
    public final Map<Integer, Integer> allShadesMapped;

    TonalPalette(com.google.ux.material.libmonet.palettes.TonalPalette materialTonalPalette) {
        this(materialTonalPalette, 1f, 1f);
    }

    /**
     * Shades are computed exactly like the {@code system_<palette>_<shade>} overlay tokens in
     * {@link DynamicColors}: tone = shade tone * luminanceFactor (clamped to [0, 100]), then the
     * chroma at that tone is multiplied by chromaFactor. Keep the two in sync.
     */
    TonalPalette(com.google.ux.material.libmonet.palettes.TonalPalette materialTonalPalette,
            float luminanceFactor, float chromaFactor) {
        this.mMaterialTonalPalette = materialTonalPalette;
        this.mChromaFactor = Float.isFinite(chromaFactor) ? Math.max(0f, chromaFactor) : 1f;
        this.allShades = SHADE_KEYS.stream()
                .map(key -> getAtTone(key.floatValue(), luminanceFactor))
                .collect(Collectors.toList());
        this.allShadesMapped = SHADE_KEYS.stream().collect(
                Collectors.toMap(key -> key, key -> getAtTone(key.floatValue(), luminanceFactor)));
    }

    /**
     * Dynamically computed tones across the full range from 0 to 1000
     * @param shade expected shade from 0 (white) to 1000 (black)
     * @return Int representing color at new shade / tone
     */
    public int getAtTone(float shade) {
        return getAtTone(shade, 1f);
    }

    /**
     * Dynamically computed tones across the full range from 0 to 1000
     * @param shade expected shade from 0 (white) to 1000 (black)
     * @param luminanceFactor luminance factor to multiply by
     * @return Int representing color at new shade / tone
     */
    public int getAtTone(float shade, float luminanceFactor) {
        final double factor = Float.isFinite(luminanceFactor) ? luminanceFactor : 1d;
        final double tone = Math.max(0d, Math.min(100d, (1000.0 - shade) / 10.0 * factor));
        Hct hct = mMaterialTonalPalette.getHct(tone);
        if (mChromaFactor != 1f) {
            hct = Hct.from(hct.getHue(), hct.getChroma() * mChromaFactor, hct.getTone());
        }
        return hct.toInt();
    }

    // Predefined & precomputed tones
    public int getS0() {
        return this.allShades.get(0);
    }

    public int getS10() {
        return this.allShades.get(1);
    }

    public int getS50() {
        return this.allShades.get(2);
    }

    public int getS100() {
        return this.allShades.get(3);
    }

    public int getS200() {
        return this.allShades.get(4);
    }

    public int getS300() {
        return this.allShades.get(5);
    }

    public int getS400() {
        return this.allShades.get(6);
    }

    public int getS500() {
        return this.allShades.get(7);
    }

    public int getS600() {
        return this.allShades.get(8);
    }

    public int getS700() {
        return this.allShades.get(9);
    }

    public int getS800() {
        return this.allShades.get(10);
    }

    public int getS900() {
        return this.allShades.get(11);
    }

    public int getS1000() {
        return this.allShades.get(12);
    }

    public static final List<Integer> SHADE_KEYS = Arrays.asList(0, 10, 50, 100, 200, 300, 400, 500,
            600, 700, 800, 900, 1000);
}
