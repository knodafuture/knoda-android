package com.knoda.knoda.test;

/**
 * Created by jeffcailteux on 9/23/14.
 */

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import models.Follow;
import views.core.MainActivity;

import static junit.framework.Assert.*;

@Config(emulateSdk = 18) //Robolectric support API level 18,17, 16, but not 19
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @Before
    public void setup() throws Exception {
        //do whatever is necessary before every test
    }

    @Test
    public void testWhoppingComplex() {
        assertTrue(true);
    }

    @Test
    public void testFollows() {
        ArrayList<Follow> myfollowing = new ArrayList<Follow>();
        Follow follow = new Follow();
        follow.leader_id = 2;
        myfollowing.add(follow);
        assertNotNull(MainActivity.Helper.checkIfFollowingUser(2, myfollowing));
    }

    @Test
    public void testStuff() {
        assertTrue(false);
    }


}