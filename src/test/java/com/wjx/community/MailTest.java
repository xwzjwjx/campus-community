package com.wjx.community;

import com.wjx.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wjx
 * @description 邮件发送测试
 */
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;
    @Test
    void test01(){
        mailClient.sendMail("1814335073@qq.com","test","测试邮件发送!");
    }
}
