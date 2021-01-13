package test;

import utils.TestConfig;

import java.io.File;

public class SendMail {
    public static void main(String [] args) throws Exception{
        System.out.println("哈哈哈");
        String[] emails="1148744992@qq.com,shouting_zhang@baofu.com".split(",");

       File  file=TestConfig.orderByDate();
       TestConfig.sendMail(emails,file);
    }
}
