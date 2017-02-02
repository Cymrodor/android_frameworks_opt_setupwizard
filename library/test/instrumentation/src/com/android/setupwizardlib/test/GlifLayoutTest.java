/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.setupwizardlib.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.ContextThemeWrapper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.setupwizardlib.GlifLayout;
import com.android.setupwizardlib.template.ColoredHeaderMixin;
import com.android.setupwizardlib.template.HeaderMixin;
import com.android.setupwizardlib.template.IconMixin;
import com.android.setupwizardlib.template.ProgressBarMixin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GlifLayoutTest {

    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = new ContextThemeWrapper(InstrumentationRegistry.getContext(),
                R.style.SuwThemeGlif_Light);
    }

    @Test
    public void testDefaultTemplate() {
        GlifLayout layout = new GlifLayout(mContext);
        assertDefaultTemplateInflated(layout);
    }

    @Test
    public void testSetHeaderText() {
        GlifLayout layout = new GlifLayout(mContext);
        TextView title = (TextView) layout.findViewById(R.id.suw_layout_title);
        layout.setHeaderText("Abracadabra");
        assertEquals("Header text should be \"Abracadabra\"", "Abracadabra", title.getText());
    }

    @Test
    public void testAddView() {
        GlifLayout layout = new GlifLayout(mContext);
        TextView tv = new TextView(mContext);
        tv.setId(R.id.test_view_id);
        layout.addView(tv);
        assertDefaultTemplateInflated(layout);
        View view = layout.findViewById(R.id.test_view_id);
        assertSame("The view added should be the same text view", tv, view);
    }

    @Test
    public void testInflateFromXml() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        GlifLayout layout = (GlifLayout) inflater.inflate(R.layout.test_glif_layout, null);
        assertDefaultTemplateInflated(layout);
        View content = layout.findViewById(R.id.test_content);
        assertTrue("@id/test_content should be a TextView", content instanceof TextView);
    }

    @Test
    public void testPrimaryColorFromXml() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        GlifLayout layout =
                (GlifLayout) inflater.inflate(R.layout.test_glif_layout_primary_color, null);
        assertDefaultTemplateInflated(layout);

        assertEquals(ColorStateList.valueOf(Color.RED), layout.getPrimaryColor());
    }

    @Test
    public void testGetScrollView() {
        GlifLayout layout = new GlifLayout(mContext);
        assertNotNull("Get scroll view should not be null with default template",
                layout.getScrollView());
    }

    @Test
    public void testSetPrimaryColor() {
        GlifLayout layout = new GlifLayout(mContext);
        layout.setProgressBarShown(true);
        layout.setPrimaryColor(ColorStateList.valueOf(Color.RED));
        assertEquals("Primary color should be red",
                ColorStateList.valueOf(Color.RED), layout.getPrimaryColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.suw_layout_progress);
            assertEquals("Progress bar should be tinted red",
                    ColorStateList.valueOf(Color.RED), progressBar.getIndeterminateTintList());
            assertEquals("Determinate progress bar should also be tinted red",
                    ColorStateList.valueOf(Color.RED), progressBar.getProgressBackgroundTintList());
        }
    }

    @Test
    public void testWrongTheme() {
        // Test the error message when using the wrong theme
        mContext = new ContextThemeWrapper(InstrumentationRegistry.getContext(),
                android.R.style.Theme);
        try {
            new GlifLayout(mContext);
            fail("Should have thrown InflateException");
        } catch (InflateException e) {
            assertEquals("Exception message should mention correct theme to use",
                    "Unable to inflate layout. Are you using @style/SuwThemeGlif "
                            + "(or its descendant) as your theme?", e.getMessage());
        }
    }

    @Test
    public void testPeekProgressBarNull() {
        GlifLayout layout = new GlifLayout(mContext);
        assertNull("PeekProgressBar should return null initially", layout.peekProgressBar());
    }

    @Test
    public void testPeekProgressBar() {
        GlifLayout layout = new GlifLayout(mContext);
        layout.setProgressBarShown(true);
        assertNotNull("Peek progress bar should return the bar after setProgressBarShown(true)",
                layout.peekProgressBar());
    }

    @Test
    public void testSetProgressBarShownInvalid() {
        GlifLayout layout = new GlifLayout(mContext, R.layout.test_template);
        layout.setProgressBarShown(true);
        // This is a no-op because there is no progress bar stub
    }

    @Test
    public void testMixins() {
        GlifLayout layout = new GlifLayout(mContext);
        final HeaderMixin header = layout.getMixin(HeaderMixin.class);
        assertTrue("Header should be instance of ColoredHeaderMixin. "
                + "Found " + header.getClass() + " instead.", header instanceof ColoredHeaderMixin);

        assertNotNull("GlifLayout should have icon mixin", layout.getMixin(IconMixin.class));
        assertNotNull("GlifLayout should have progress bar mixin",
                layout.getMixin(ProgressBarMixin.class));
    }

    private void assertDefaultTemplateInflated(GlifLayout layout) {
        View title = layout.findViewById(R.id.suw_layout_title);
        assertNotNull("@id/suw_layout_title should not be null", title);

        View icon = layout.findViewById(R.id.suw_layout_icon);
        assertNotNull("@id/suw_layout_icon should not be null", icon);

        View scrollView = layout.findViewById(R.id.suw_scroll_view);
        assertTrue("@id/suw_scroll_view should be a ScrollView", scrollView instanceof ScrollView);
    }
}