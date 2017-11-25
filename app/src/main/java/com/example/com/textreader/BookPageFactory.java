package com.example.com.textreader;

import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class BookPageFactory {
    /**
     * 随机读取文件
     */
    private RandomAccessFile randomAccessFile;

    /**
     * 宽能显示的字数，汉字
     */
    private int lenW;
    /**
     * 高能显示的字数
     */
    private int lenH;
    /**
     * 开始读书本页的位置
     */
    private long begin = 0;

    /**
     * 记录当前页字符数目
     */
    private long pageSize = 0;
    private long prepageSize=0;//当前页的前一页大小
    private ArrayList<Long> pagesizes;
    private int pagenum=1;//记录当前页码

    private boolean FLAG_END = false;

    /**
     * 从开始位置打开书本
     *
     * @param bookFile 书本文件
     */
    public void openBook(File bookFile) throws IOException {
        this.begin = 0;
        randomAccessFile = new RandomAccessFile(bookFile, "r");//只读方式打开文件，安装randomAccessFile读取
        pagesizes=new ArrayList<Long>();
        //randomAccessFile.seek(begin);
    }

    /**
     * 读当前页UTF-8
     *
     * @return
     * @throws java.io.IOException
     */
    public String readPageUTF8() {
        long sign = begin;//标记当前页读取了几多字符
        String result = "";//读取结果字符串
        byte[] bytes = new byte[3 * lenW];//汉语占三个字节所以byte最大应该为3倍的lenW
        try {
            randomAccessFile.seek(begin);
            //System.out.println("begin="+begin);

            for (int i = 0; i < lenH || FLAG_END; i++) {//读取能显示的最大行数
                FLAG_END = false;
                bytes = new byte[3 * lenW];
                int l = 0;//l用来标记bytes的坐标
                for (int k = 0; k < 2 * lenW; k++) {
                    bytes[++l] = randomAccessFile.readByte();
                    sign++;
                    if (bytes[l] == 10) {//遇到换行符换到下一行
                        if (i == lenH - 1) {
                            FLAG_END = true;//读到最后一行了
                        }
                        break;
                    } else if (bytes[l] < 0) {//汉字读取
                        if (l < (3 * lenW - 2)) {//当前行剩余的空间能够在剩一个汉字
                            //汉字在utf-8中占三个字节，读取一个字节后再额外读两个字节
                            bytes[++l] = randomAccessFile.readByte();
                            sign++;
                            bytes[++l] = randomAccessFile.readByte();
                            sign++;
                            k++;//汉字展示是英文的两倍的宽度，所以占的字符宽度要+1
                        } else {
                            bytes[l] = 0;
                            randomAccessFile.seek(--sign);
                            break;
                        }
                    }
                }
                result += new String(bytes);
                //System.out.println(new String(bytes));
            }
        } catch (EOFException e){//到达文件尾
            if (sign-begin>0){
                result += new String(bytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (sign-begin>0){
                result += new String(bytes);
            }
        }
//        FLAG_END = false;
        if (sign-begin>0){
            pageSize = sign - begin;
        }
       // System.out.println("currentpagesize="+pageSize);
        return result;
    }

    /*读取第一页*/
     public String firstPage(){
         //System.out.println("这是第1页");
         this.begin=0;
         String result = readPageUTF8();
         //pagenum++;
         pagesizes.add(pageSize);
         return result;
     }

    /**
     * 读取下一页
     */
    public String nextPage() {
        //System.out.println("点击下一页");

        begin += pageSize;
        //System.out.println("读要显示的这页");
        String result = readPageUTF8();

        if(result.equals("")){//如果要显示的这页为空
            begin-=pagesizes.get(pagesizes.size()-1);
            //System.out.println("这是最后一页了");
            result=readPageUTF8();//读要显示的这页的前一页
        }
        else{
            //System.out.println("这页可以显示出来");
            if(pagenum==pagesizes.size()){//这页还没加载过
                pagesizes.add(pageSize);//把这页大小加进去
            }
            pagenum++;
        }
        //System.out.println("当前页码"+pagenum);
        //System.out.println("上一页大小"+prepageSize);
        //不为空result就直接是上边的result
        return result;
    }

    /**
     * 读取前一页
     */
    public String prePage() {
        //System.out.println("点击上一页");
        String result;
        if(pagenum>1){//没到第1页
            prepageSize=pagesizes.get(pagenum-2);
            begin-=prepageSize;//读取要显示的一页
            result=readPageUTF8();
            pagenum--;
        }
        else{//是第一页
            begin=0;
            result=readPageUTF8();
        }
        //System.out.println("当前页码"+pagenum);
        return result;
    }

    public void setLenW(int lenW) {
        this.lenW = lenW;
    }

    public void setLenH(int lenH) {
        this.lenH = lenH;
    }

    public long GetCurrentPageNum(){
        return pagenum;
    }

    public String setCurrentPageNum(long page){
        firstPage();
        for(long i = 0; i< page; i++){
            nextPage();
        }
        return nextPage();
    }

    public long GetCurrentAllPages(){
        return pagesizes.size();
    }


}
