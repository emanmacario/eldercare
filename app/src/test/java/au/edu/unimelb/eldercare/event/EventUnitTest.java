package au.edu.unimelb.eldercare.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Local unit test for event package
 */
public class EventUnitTest {
    @Test
    public void EventRegisterUserIsCorrect(){
        Event event = new Event();
        event.registerUser("userId", "state");
        assertEquals(event.registeredUserId.get("userId"), "state");
    }

    @Test
    public void EventUnregisterUserIsCorrect(){
        Event event = new Event();
        event.registerUser("userId", "state");
        assertEquals(event.registeredUserId.get("userId"), "state");
        event.unregisterUser("userId");
        assertEquals(event.registeredUserId.get("userId"), null);
    }
}
