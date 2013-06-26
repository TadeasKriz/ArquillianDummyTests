package org.jboss.aerogear.android.dummytests;

import org.arquillian.droidium.container.api.AndroidDevice;
import org.arquillian.droidium.container.api.AndroidDeviceOutputReciever;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.android.AndroidDriver;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * We need to use RunAsClient, because this test isn't going to run on the device itself.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContainerTest {

    @ArquillianResource
    AndroidDevice device;

    /**
     * testable = false is important otherwise the APK won't get installed, because Arquillian will add it's own logic into the APK, but won't sign it afterwards. And not-signed APKs can't be installed on android.
     *
     * @return Archive which is created from APK we want to test.
     */
    @Deployment(testable = false, name = "android")
    public static Archive<?> createDeployment() {
        System.out.println("creating deployment android");
        return ShrinkWrap.createFromZipFile(JavaArchive.class, new File("selendroid-test-app-0.4.0.apk"));
    }

    @Test
    @InSequence(1)
    @OperateOnDeployment("android")
    public void testDriver(@Drone AndroidDriver driver) {
        assertTrue(device != null);
        assertTrue(driver != null);
        System.out.println(driver.getCapabilities().toString());
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("android")
    public void test(@Drone AndroidDriver driver) {
        WebElement textField = driver.findElementById("my_text_field");

        assertNotNull(textField);

        textField.sendKeys("test");

        assertEquals(textField.getText(), "test");
    }

    @Test
    @Ignore
    @InSequence(3)
    @OperateOnDeployment("android")
    public void runInstrumentationTests() {
        AndroidDeviceOutputReciever reciever = new AndroidDeviceOutputReciever() {
            @Override
            public void processNewLines(String[] lines) {
                for(String line : lines) {
                    System.out.println(line);
                }
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };
        device.executeShellCommand("", reciever);
    }

}
