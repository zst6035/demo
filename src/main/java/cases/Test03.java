package cases;

import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Test03 {

    @Test(dataProvider = "dateprovider1",testName = "测试名字")
    public void test01(String name,int age,String grade){

       // System.out.println(name+age);
        Assert.assertEquals(10,age);

    }

    @DataProvider
    public Object[][] dateprovider1(){
        Object[][] o=new Object[][]{
                {"zhangsan",10,"三班"},
                {"lisi",20,"四班"},
                {"wangwu",30,"五班"}


        };
        return o;
    }
}
