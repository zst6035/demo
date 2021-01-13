package cases;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Test02 {
    @Test(description = "测试用例2")
    public void test01(){
        Assert.assertEquals("1","1");
    }
}
