package com.xxxx.crm;

import com.xxxx.crm.exceptions.ParamsException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrmApplicationTests {

    @Test
    void contextLoads() {
        A(true, "A888");
        // System.out.println("66666666666666666666");
    }

    public static void A(boolean a, String msg) {
        if (a) {
            throw new ParamsException(msg);
        }
    }

}
