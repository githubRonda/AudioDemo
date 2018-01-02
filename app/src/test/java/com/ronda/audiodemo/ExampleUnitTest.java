package com.ronda.audiodemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ronda.audiodemo.ui.ActionBarCastActivity;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testLog() throws Exception {
        //String s = LogHelper.makeLogTag("1234567890");
//        String s = LogHelper.makeLogTag(this.getClass());
//
//
//        System.out.println("s = "+s);
//
//
//        LogHelper.log("TestTag", Log.ERROR, new RuntimeException("run Error"), "testmsg");

        System.out.println("1: " + ActionBarCastActivity.class.isAssignableFrom(Activity.class)); //false
        System.out.println("2: " + ActionBarCastActivity.class.isAssignableFrom(AppCompatActivity.class)); //false
        System.out.println("3: " + ActionBarCastActivity.class.isAssignableFrom(ActionBarCastActivity.class)); // ture

        System.out.println("4: " + AppCompatActivity.class.isAssignableFrom(ActionBarCastActivity.class));// ture
        System.out.println("5: " + Activity.class.isAssignableFrom(ActionBarCastActivity.class));// ture
        System.out.println("6: " + Window.Callback.class.isAssignableFrom(ActionBarCastActivity.class));// ture

    }

    @Test
    public void testNetJson() throws Exception {
        final String urlString = "http://storage.googleapis.com/automotive-media/music.json";

        URLConnection urlConnection = new URL(urlString).openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "iso-8859-1"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        System.out.println("sb.toString(): " + sb.toString());
    }


}