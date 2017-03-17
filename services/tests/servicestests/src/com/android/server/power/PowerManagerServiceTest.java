/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.server.power;

import android.hardware.display.DisplayManagerInternal.DisplayPowerRequest;
import android.os.PowerSaveState;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link com.android.server.power.PowerManagerService}
 */
public class PowerManagerServiceTest extends AndroidTestCase {
    private static final float PRECISION = 0.001f;
    private static final float BRIGHTNESS_FACTOR = 0.7f;
    private static final boolean BATTERY_SAVER_ENABLED = true;

    private @Mock BatterySaverPolicy mBatterySaverPolicy;
    private PowerManagerService mService;
    private PowerSaveState mPowerSaveState;
    private DisplayPowerRequest mDisplayPowerRequest;

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mPowerSaveState = new PowerSaveState.Builder()
                .setBatterySaverEnabled(BATTERY_SAVER_ENABLED)
                .setBrightnessFactor(BRIGHTNESS_FACTOR)
                .build();
        when(mBatterySaverPolicy.getBatterySaverPolicy(
                eq(BatterySaverPolicy.ServiceType.SCREEN_BRIGHTNESS), anyBoolean()))
                .thenReturn(mPowerSaveState);
        mDisplayPowerRequest = new DisplayPowerRequest();
        mService = new PowerManagerService(getContext(), mBatterySaverPolicy);
    }

    @SmallTest
    public void testUpdatePowerScreenPolicy_UpdateDisplayPowerRequest() {
        mService.updatePowerRequestFromBatterySaverPolicy(mDisplayPowerRequest);
        assertThat(mDisplayPowerRequest.lowPowerMode).isEqualTo(BATTERY_SAVER_ENABLED);
        assertThat(mDisplayPowerRequest.screenLowPowerBrightnessFactor)
                .isWithin(PRECISION).of(BRIGHTNESS_FACTOR);
    }
}
