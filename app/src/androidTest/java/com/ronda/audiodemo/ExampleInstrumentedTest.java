package com.ronda.audiodemo;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ronda.audiodemo.ui.ActionBarCastActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.ronda.audiodemo", appContext.getPackageName());
    }

    @Test
    public void testLog() throws Exception {
//        LogHelper.log("TestTag", Log.ERROR, new RuntimeException("run Error"), "testmsg", "testmsg1", "testmsg2");


        System.out.println("1: " + ActionBarCastActivity.class.isAssignableFrom(Activity.class)); //false
        System.out.println("2: " + ActionBarCastActivity.class.isAssignableFrom(AppCompatActivity.class)); //false
        System.out.println("3: " + ActionBarCastActivity.class.isAssignableFrom(ActionBarCastActivity.class)); // ture

        System.out.println("4: " + AppCompatActivity.class.isAssignableFrom(ActionBarCastActivity.class));// ture
        System.out.println("5: " + Activity.class.isAssignableFrom(ActionBarCastActivity.class));// ture
        System.out.println("6: " + Window.Callback.class.isAssignableFrom(ActionBarCastActivity.class));// ture


    }
}
