package utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


//可以看到变量名都是静态的全局变量，不需要new对象；
@Data
public class TestConfig {

    //公共变量
    public static  String url;
    public static HttpClient defaultHttpClient ;
    public static CookieStore store;
    public static SqlSession session;
    public static SqlSession sessionBf;
    public static SqlSession sessionHsqTrade;
    public static SqlSession sessionHsqGateway;
    public static Integer userId;


//    static {
//        try {
//            session = DatabaseUtil.getSqlSession1();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    //因为merchant不经常变化，所以直接在这里定义好
 //   public static Merchant merchant=session.selectOne("selMerchantInfo");

    //以json方式访问公共方法,获取请求结果 状态message
    public static  String getResult(String url,String body)throws IOException {

        HttpPost post=new HttpPost("http://"+ TestConfig.url+url);
        post.setHeader("Content-Type", "application/json;charset=utf-8");
        //  System.out.println(body);
        post.setEntity(new StringEntity(body,Charset.forName("utf-8")));
        HttpResponse response=TestConfig.defaultHttpClient.execute(post);
        String  result= EntityUtils.toString(response.getEntity(),"utf-8");
        JSONObject resultJson = JSONObject.parseObject(result);
        // System.out.println(resultJson.get("message"));
        String result2=(String) resultJson.get("message");
        return result2;
    }

//公共方法，获取请求结果，以便截取，替换
public static  String getResponse(String url,String body)throws IOException{
     HttpPost post=new HttpPost("http://"+ TestConfig.url+url);
     post.setHeader("Content-Type", "application/json;charset=utf-8");
     //解决发送数据中文乱码问题
     post.setEntity(new StringEntity(body,Charset.forName("UTF-8")));
     HttpResponse response=TestConfig.defaultHttpClient.execute(post);
     String  result= EntityUtils.toString(response.getEntity(),"utf-8");
     return result;
}

    //httpsend普通请求，此处声明了httpClient
    public static String HttpSend(String url, Map<String,String> map) {
        UrlEncodedFormEntity urlEncodedFormEntity=null;
        //将map转化为urlEncodedFormEntity
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if (Objects.nonNull(map)) {
            //获取map所有的key，并放入set集合中
            Set<String> keySet = map.keySet();

            for (String key : keySet) {
                nameValuePairList.add(new BasicNameValuePair(key, map.get(key)));

            }
        }
        try {
            urlEncodedFormEntity=new UrlEncodedFormEntity(nameValuePairList,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("转换为urlentity失败");
        }
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(urlEncodedFormEntity);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }





//获取文件夹中最新的测试报告
public static File orderByDate() throws Exception{
    //获取文件夹路径
    File file = new File("");

    String filePath=file.getCanonicalPath().concat(File.separatorChar+"").concat("test-output");
    System.out.println("=============="+filePath);
    File file1=new File(filePath);
    File[] files=file1.listFiles();
    Arrays.sort(files, new Comparator<File>() {
        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
        }
    });
   // System.out.println(files[files.length-1].getName());
    return files[files.length-1];

}



//发送带附件的邮件
public static void sendMail(String[] tos,File file) throws Exception {
    //获得session对象
    final Properties props = new Properties();

    //下面两段代码是设置ssl和端口，不设置发送不出去。
    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.smtp.socketFactory.port", "465");
    // 发送服务器需要身份验证
    props.setProperty("mail.transport.protocol", "smtp");// 发送邮件协议名称
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.host", "smtp.qq.com");//QQ邮箱的服务器 如果是企业邮箱或者其他邮箱得更换该服务器地址
    // 发件人的账号
    props.put("mail.user", "1148744992@qq.com");
    // 访问SMTP服务时需要提供的密码 
    props.put("mail.password", "nejjmhrmkkjnhffg");
    // 构建授权信息，用于进行SMTP进行身份验证
    Authenticator authenticator = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            // 用户名、密码
            String userName = props.getProperty("mail.user");
            String password = props.getProperty("mail.password");
            //发件邮箱
            return new PasswordAuthentication(userName, password);
        }
    };
    //上面的都是基础设置



    Session se = Session.getInstance(props, authenticator);
    //创建一个表示邮件的对象message
    Message mes = new MimeMessage(se);
    try {
        mes.setFrom(new InternetAddress("1148744992@qq.com"));//发件人
        //定义收件人地址为一个数组，可以添加多个收件人
        Address[] address=new Address[tos.length];
        for (int i = 0; i < tos.length; i++) {
            address[i] = new InternetAddress(tos[i]);
        }
        mes.setRecipients(Message.RecipientType.TO, address);
        mes.setSubject("测试报告");//主题
        //设置邮件内容
        // mes.setContent("你好啊，大家", "text/html;charset=UTF-8");
        Multipart multipart = new MimeMultipart();
        // 设置邮件的文本内容
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText("测试结果详情请查看附件");
        multipart.addBodyPart(contentPart);
        //声明附件地址
//            File file = new File("");
//            String filePath = file.getCanonicalPath();
//            String affix=filePath+"\\test-output\\202006301304.html";
//            //声明附件名称
//            String affixName="测试报告";
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));

        sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();


//           设置后，附件不会出现乱码
        messageBodyPart.setFileName("=?UTF-8?B?"
                + enc.encode(file.getName().getBytes("utf-8")) + "?=");

        multipart.addBodyPart(messageBodyPart);
        mes.setContent(multipart);
        mes.saveChanges();
        //发送邮件transport
        Transport.send(mes);
    } catch (AddressException e) {
        e.printStackTrace();
    } catch (MessagingException e) {
        e.printStackTrace();
    }
}

//获取特定的日期格式

    public static String dateString(){
        DateFormat dateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        Date now=new Date();
       String dateString= dateFormat.format(now);
       // System.out.println(dateString);
        return dateString;

    }


    //将一年的日期循环插入表中
    public static void DateIn(){
        DateTime parse = DateUtil.parse("2020-01-01", "yyyy-MM-dd");
        System.out.println(parse);
        for (int i = 0; i < 365; i++) {
            DateTime offset = DateUtil.offset(parse, DateField.DAY_OF_MONTH, i);
            TestConfig.session.insert("InDate", offset.toString());
            TestConfig.session.commit();

        }


    }
//json值转换相关
    public static void getJsonArray ()throws Exception{

        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<2;i++) {
            //jsonobject需要在里面定义，在外面定义，读取不到；
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id", i);
            jsonObject.put("name", "张三");
            jsonArray.add(jsonObject);

        }
        JSONObject jsonObject1=new JSONObject();
        jsonObject1.put("data",jsonArray);
        System.out.println(jsonObject1);


    }

    //获取区间随机值
      public static int intRandom(int number){

        return new Random().nextInt(number)+1;
  }

  //获取日期新增一天
  public static String getNextDate(String date)throws Exception{
      DateFormat format=new SimpleDateFormat("yyyy-mm-dd");
      Date date1=format.parse(date);
      Calendar calendar=Calendar.getInstance();
      calendar.setTime(date1);
      calendar.add(Calendar.DAY_OF_MONTH,+1);
      Date date2=calendar.getTime();
      return format.format(date2);
  }


    //传入一个文件，返回一个String字符串
    public static String getFiletoString(File file)throws Exception{
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String s = "";
        while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中

        }
        bReader.close();
        String str = sb.toString();
        return str;
    }


//返回区间任意大小的值 比如传入100,200，返回111；
    public static int getNum(int start,int end) {

        return (int)(Math.random()*(end-start+1)+start);
    }


    //根据性别，随机生成姓名

    public static String  getChineseName(int sex){
        String firstName="赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯咎管卢莫经房裘缪干解应宗宣丁贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄魏加封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘姜詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲台从鄂索咸籍赖卓蔺屠蒙池乔阴郁胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍却璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庚终暨居衡步都耿满弘匡国文寇广禄阙东殴殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜养鞠须丰巢关蒯相查后江红游竺权逯盖益桓公万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公冶宗政濮阳淳于仲孙太叔申屠公孙乐正轩辕令狐钟离闾丘长孙慕容鲜于宇文司徒司空亓官司寇仉督子车颛孙端木巫马公西漆雕乐正壤驷公良拓拔夹谷宰父谷粱晋楚阎法汝鄢涂钦段干百里东郭南门呼延归海羊舌微生岳帅缑亢况后有琴梁丘左丘东门西门商牟佘佴伯赏南宫墨哈谯笪年爱阳佟第五言福百家姓续";
        String girl="秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";
        String boy="伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

        int index=getNum(0, firstName.length()-1);
        String first=firstName.substring(index, index+1);
        //先当做传入的是男性
        String str=boy;
        int length=boy.length();
        if(sex==0) {
            str = girl;
            length = girl.length();
        }
        index=getNum(0,length-1);
        String second=str.substring(index, index+1);
        int hasThird=getNum(0,1);
        String third="";
        if(hasThird==1){
            index=getNum(0,length-1);
            third=str.substring(index, index+1);
        }
        return "测试"+ first+second+third;

    }


    //随机返回手机号码
    public static String getTel() {
        String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        //String.substring(i)表示去掉前i个字符，返回一个新的字符串；
        //string.substring(i,j)取第i,到j-1个字符串；
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }
    //获取邮箱
    public static String getEmail(int lMin,int lMax) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        String[] email_suffix="@gmail.com,@yahoo.com,@msn.com,@hotmail.com,@aol.com,@ask.com,@live.com,@qq.com,@0355.net,@163.com,@163.net,@263.net,@3721.net,@yeah.net,@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn".split(",");

        int length=getNum(lMin,lMax);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = (int)(Math.random()*base.length());
            sb.append(base.charAt(number));
        }
        sb.append(email_suffix[(int)(Math.random()*email_suffix.length)]);
        return sb.toString();
    }

    //返回指定长度的随机字符串花名，邮箱都可以用这个
    public static String getString(int i){
        String  base = "abcdefghijklmnopqrstuvwxyz0123456789";
        int length=getNum(i,i+3);
        int index=getNum(0,base.length()+1);
        StringBuffer sb = new StringBuffer();
        for (int j= 0; j < length; j++) {
            int number = (int)(Math.random()*base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    //返回随机护照
    public static String getID(){
        String l="1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return l;
    }
	
	
	//生成指定长度的数字字符串
    public static String getRandom4(int len) {
        String source = "0123456789";
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int j = 0; j < len; j++) {
            rs.append(source.charAt(r.nextInt(10)));
        }
        return rs.toString();
    }

    //查询请求
    public static JSONObject selTrans(String cookie, String businessNo)throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet=new HttpGet("http://vad.baofoo.com/fi/weixinOrder/batchSearch.do?businessNo="+businessNo);
        httpGet.addHeader("Cookie",  cookie);
        httpGet.addHeader("Content-Type","application/json");
        HttpResponse response=httpClient.execute(httpGet);
        HttpEntity entity=response.getEntity();
        String result= EntityUtils.toString(entity,"utf-8");
        JSONObject jsonObject= JSONObject.parseObject(result);
        return jsonObject;

    }
	
}
