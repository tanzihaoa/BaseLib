package com.tzh.baselib.util.lock;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * 指纹解锁工具
 */
public class FingerprintUnlock {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    public FingerprintUnlock(FragmentActivity activity, BiometricPrompt.AuthenticationCallback callback) {
        biometricPrompt = new BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("指纹解锁")
                .setSubtitle("请验证指纹以解锁")
                .setNegativeButtonText("取消")
                .build();
    }

    public void startAuthentication() {
        biometricPrompt.authenticate(promptInfo);
    }

    public void cancelAuthentication() {
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
        }
    }
}
