package com.example.wjmas_000.menu;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MockitoTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    @Test
    public void loginTest_returnsTrue() {
        LoginFragment testing = mock(LoginFragment.class);

        //int test = testing.validate("James", "Bond");
        //assertEquals(test,0);
    }

    /*
    @Test
    public void loginTest_returnsFalse() {
        LoginFragment testing = mock(LoginFragment.class);

        int test = testing.validate("Jimmy", "Boron");
        assertEquals(test,0);
    }
    */

    @Test
    public void LeaderboardTest(){
        LeaderboardFragment testing = mock(LeaderboardFragment.class);
        int test = testing.jsonParseLeader();
        assertEquals(test,0);

    }
}