package com.gakki.reggie.controller;

import com.gakki.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传及下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("upload")
    public R<String> upload(MultipartFile file){
        //现在的file是临时文件，需要转存到指定位置，否则本次请求完成后文件就会删除
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();

        //获取原始文件名的后缀，通过截取获得
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir=new File(basePath);
        //判断当前的目录是否存在
        if (!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
    //文件下载的方法,需要response（响应）进行接收
    @GetMapping("/download")
    public void download(String  name, HttpServletResponse response){
        //输入流，通过输入流来读取文件内容
        try {
            //输入流，通过输入流来读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //因为下载的是图片文件,所以设置下载文件的类型
            response.setContentType("image/jpeg");
            byte[] bytes=new byte[1024];
            int length=0;
            //-1表示读完
            while ((length=fileInputStream.read(bytes))!=-1) {
                outputStream.write(bytes,0,length);
                //通过flush来刷新
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
