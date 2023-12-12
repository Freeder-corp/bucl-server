package com.freeder.buclserver.crypto;

import com.freeder.buclserver.global.util.CryptoAes256;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class CryptoAes256Test {
    @Autowired
    CryptoAes256 cryptoAes256;

    @Test
    @DisplayName("AES 암호화 테스트")
    void encryptTest() throws Exception {
        String plainText = "100000,100000,12";
        String encrypt = cryptoAes256.encrypt(plainText);

        System.out.println(encrypt);
        assertThat(encrypt).isNotIn(plainText);
    }

    @Test
    @DisplayName("AES 복호화 테스트")
    void decryptTest() throws Exception {
        String plainText = "100000,100000,12";
        String encrypt = cryptoAes256.encrypt(plainText);
        String decrypt = cryptoAes256.decrypt(encrypt);

        assertThat(decrypt).isEqualTo(plainText);
    }



}