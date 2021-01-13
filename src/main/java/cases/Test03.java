package cases;

import lombok.Data;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Test03 {

    @Test(dataProvider = "dateprovider1")
    public void test01(String name,int age){
        System.out.println(name+age);
    }

    @DataProvider
    public Object[][] dateprovider1(){
        Object[][] o=new Object[][]{
                {"zhangsan",10},
                {"lisi",20},
                {"wangwu",30}


        };
        return o;
    }
}
