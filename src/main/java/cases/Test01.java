package cases;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Test01 {
    @Test(description = "测试用例1")
    public void test01(){
        Assert.assertEquals(1,1);
    }
}
