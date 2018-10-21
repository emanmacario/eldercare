package au.edu.unimelb.eldercare.service;

import com.sinch.android.rtc.calling.Call;

public interface SinchServiceInterface {
    Call callUser(String userId);
    Call getCall(String callId);
}
