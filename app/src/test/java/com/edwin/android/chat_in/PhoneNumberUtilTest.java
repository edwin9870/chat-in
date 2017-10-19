package com.edwin.android.chat_in;

import com.edwin.android.chat_in.util.PhoneNumberUtil;

import org.junit.Test;

import static org.junit.Assert.*;


public class PhoneNumberUtilTest {
    @Test
    public void formatPhoneNumber_FormatPlusCharacter() throws Exception {
        String phoneNumber = "+8292779870";
        assertEquals("18292779870", PhoneNumberUtil.formatPhoneNumber(phoneNumber));

        phoneNumber = "+18292779870";
        assertEquals("18292779870", PhoneNumberUtil.formatPhoneNumber(phoneNumber));
    }

    @Test
    public void formatPhoneNumber_WithoutOneNumberBeginning() throws Exception {
        String phoneNumber = "8292779870";
        assertEquals("18292779870", PhoneNumberUtil.formatPhoneNumber(phoneNumber));
    }

    @Test
    public void formatPhoneNumber_WithOneNumberBeginning() throws Exception {
        String phoneNumber = "18292779870";
        assertEquals("18292779870", PhoneNumberUtil.formatPhoneNumber(phoneNumber));
    }
}