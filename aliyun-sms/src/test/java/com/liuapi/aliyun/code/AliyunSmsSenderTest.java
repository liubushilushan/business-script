package com.liuapi.aliyun.code;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.liuapi.aliyun.code.util.PwdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.io.IOException;
import java.nio.charset.Charset;

class AliyunSmsSenderTest {
    @Test
    void demo(){
        StandardPasswordEncoder standardPasswordEncoder = new StandardPasswordEncoder("111");
        // 不用的密文比对同一个明文都能校验通过
        System.out.println(standardPasswordEncoder.matches("123456", "74676964f948b82aaa5d75e6d9ca4e1cc4a3b0d452ba06981aaeec4564095e714fc768ae5a1631ad"));
        System.out.println(standardPasswordEncoder.matches("123456", "79634a7b7f8a4d536cb40558f4155770fdafa64579df04d34acf71ec56accd3c18fc2cb95c6ce5dd"));
        System.out.println(standardPasswordEncoder.matches("123456", "83ab82f4fb2ece390cc76304c66ac26a054ff5eda61256356bb868ba586499d2cb2cc7a8753fd5b8"));
        System.out.println(standardPasswordEncoder.matches("123456", "0946da89a613bb73a6ab79a1e76e6cde41bdd3991ed6ccd01fb43cbd265c30d329ac5e26215002fd"));
    }

    @Test
    void sendMe(){
        AliyunSmsSender.sendCode("17621257001","78adihj");
    }
    @Test
    void testCsv() throws IOException {
        String writerCsvFilePath = "C:\\Users\\Administrator\\Documents\\test.csv";
        CsvWriter csvWriter = new CsvWriter(writerCsvFilePath, ',', Charset.forName("UTF-8"));
        String[] header = {"名字","节日","生日","性别"};
        csvWriter.writeRecord(header);

        String[] contents = {"Lily","五一","90","女"};
        csvWriter.writeRecord(contents);
        String[] content1s = {"Jemmy","留意","98","女"};
        csvWriter.writeRecord(content1s);
        csvWriter.close();


        CsvReader csvReader = new CsvReader(writerCsvFilePath, ',', Charset.forName("UTF-8"));
        csvReader.readHeaders(); // 跳过表头   如果需要表头的话，不要写这句。
        String[] head = csvReader.getHeaders(); //获取表头
        while (csvReader.readRecord())
        {
            for (int i = 0; i < head.length; i++)
            {
                System.out.println(head[i] + ":" + csvReader.get(head[i]));
            }
        }
        csvReader.close();
    }
    @Test
    void sd() throws IOException {
        StandardPasswordEncoder standardPasswordEncoder = new StandardPasswordEncoder("+kads^jsmdx*msx#ls55x2sdax8()=_$hsdks.x");

        // csv 读入文件
        String accountPath = "C:\\Users\\Administrator\\Documents\\account.csv";
        CsvReader csvReader = new CsvReader(accountPath, ',', Charset.forName("UTF-8"));
        csvReader.readHeaders(); // 跳过表头   如果需要表头的话，不要写这句。
        String[] head = csvReader.getHeaders(); //获取表头
        String writerCsvFilePath = "C:\\Users\\Administrator\\Documents\\account_new.csv";
        CsvWriter csvWriter = new CsvWriter(writerCsvFilePath, ',', Charset.forName("UTF-8"));
        csvWriter.writeRecord(new String[]{"手机号","密码"});
        // 第一列为手机号
        // 第二列为昵称
        // 第三列为旧密码
        // 生成密码
        while (csvReader.readRecord())
        {

            String newPwd = PwdGenerator.generate(8);
            String value = csvReader.get(head[0]);
            String encodedPwd = standardPasswordEncoder.encode(newPwd);
            // 输出一行内容
            System.out.println("update account set `password`=\""+encodedPwd+"\" where `mobile`= \""+value+"\" and `status`!=-2;");
            csvWriter.writeRecord(new String[]{value,newPwd});
            // 测试每次加密出来的密文都是不一样的
        }
        // 密码刷库
        csvReader.close();
        csvWriter.close();
    }
    @Test
    void ad() throws IOException {
        // csv读入密码文件
        String accountPath = "C:\\Users\\Administrator\\Documents\\account_new.csv";
        CsvReader csvReader = new CsvReader(accountPath, ',', Charset.forName("UTF-8"));
        csvReader.readHeaders(); // 跳过表头   如果需要表头的话，不要写这句。
        String[] head = csvReader.getHeaders(); //获取表头
        // 发送短信验证码
        while (csvReader.readRecord())
        {
            System.out.println(csvReader.get(head[0])+":"+csvReader.get(head[1]));
//            AliyunCodeSender.sendPwd(csvReader.get(head[0]),csvReader.get(head[1]));
        }
        csvReader.close();
    }

}