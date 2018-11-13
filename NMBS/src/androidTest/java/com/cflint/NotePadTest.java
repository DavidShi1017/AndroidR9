/*
 * This is an example test project created in Eclipse to test NotePad which is a sample 
 * project located in AndroidSDK/samples/android-11/NotePad
 * 
 * 
 * You can run these test cases either on the emulator or on device. Right click
 * the test project and select Run As --> Run As Android JUnit Test
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

package com.cflint;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageView;

import com.cflint.activities.MainActivity;
//import com.robotium.solo.Solo;

@SuppressWarnings("rawtypes")
public class NotePadTest extends ActivityInstrumentationTestCase2<MainActivity> {


    //private Solo solo;

    public NotePadTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        //super.setUp();
        //setUp() is run before a test case is started.
        //This is where the solo object is created.
       // solo = new Solo(getInstrumentation(), getActivity());

    }

    protected void tearDown() throws Exception {
        //super.tearDown();
        //solo.finishOpenedActivities();
    }

    public void testRun() {
        //Wait for activity: 'com.example.ExampleActivty'
        //solo.waitForActivity("MainActivity", 2000);
        //ImageView searchButton = (ImageView) solo.getView(com.nmbs.R.id.iv_menu);
        //solo.clickOnView(searchButton);
        //solo.sleep(15000);
        //Clear the EditText editText1
        //solo.clearEditText((android.widget.EditText) solo.getView("editText1"));
        //solo.enterText((android.widget.EditText) solo.getView("editText1"), "This is an example text");
    }
}