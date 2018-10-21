package au.edu.unimelb.eldercare;

import com.sinch.android.rtc.calling.Call;

public interface SinchServiceInterface {

    Call callUser(String userId);

    String getUserId();

    Call getCall(String callId);
}
