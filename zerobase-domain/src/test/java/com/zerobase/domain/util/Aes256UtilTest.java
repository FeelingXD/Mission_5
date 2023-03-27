package com.zerobase.domain.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class Aes256UtilTest {



    @Test
    void encrypt(){
        //given
        String encrypt = Aes256Util.encrypt("Hello world");
        assertEquals("Hello world",Aes256Util.decrypt(encrypt));
        //when
        //then
    }

    @Test
    void decrypt(){
        //given
        //when
        //then
    }
}