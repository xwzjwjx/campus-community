package com.wjx.community.common;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wjx
 * @description 敏感词过滤器
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //根节点,其内容为空
    private TireNode rootNode = new TireNode();
    //替换符
    private static final String REPLACEMENT = "*";

    /**
     * PostConstruct 注解用于需要在依赖注入完成后执行任何初始化的方法。
     * 该方法必须在类投入使用之前调用。所有支持依赖注入的类都必须支持这个注解。
     * 即使类没有请求注入任何资源，也必须调用带有 PostConstruct 注释的方法。此注解只能注解一种方法。
     */
    @PostConstruct
    private void init(){
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            ) {
            String keyword;
            if ((keyword = bufferedReader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败："+ e.getMessage());
        }
    }

    /**
     * 将关键词添加到前缀树
     * @param keyword
     */
    private void addKeyWord(String keyword) {
        TireNode tempNode = rootNode;
        char[] chars = keyword.toCharArray();
        for(int i=0;i<chars.length;i++){
            TireNode tempNodeChildNode = tempNode.getChildNode(chars[i]);
            if (tempNodeChildNode == null){
                //初始化
                tempNodeChildNode = new TireNode();
                tempNode.addChildNode(chars[i],tempNodeChildNode);
            }
            tempNode = tempNodeChildNode;
            //设置结束标志
            if (i == chars.length-1){
                tempNode.setKeyWord(true);
            }
        }
    }

    /**
     * 过滤文本
     * @param text 带过滤文本
     * @return 过滤后的结果
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        TireNode node = rootNode;
        int position = 0;
        int begin = 0;
        StringBuilder res = new StringBuilder();
        while(position < text.length()){
            char c = text.charAt(position);
            //跳过特殊符号
            if (isSymbol(c)){
                //符号在开头就加入结果，在中间不加
                if (node == rootNode){
                    res.append(c);
                    begin++;
                }
                //无论是否在中间与开头，position都向下移动
                position++;
                continue;
            }
            //获取下级节点
            node = node.getChildNode(c);
            if (node == null){
                //下级节点不存在，说明以begin为首的字符不是敏感词
                res.append(text.charAt(begin));
                //进入下一个文本位置
                position = ++begin;
                node = rootNode;
            } else if (node.isKeyWord()){
                //是敏感词，则替换
                res.append(REPLACEMENT);
                //跳到敏感词之后的一个词
                begin = ++position;
                node = rootNode;
            }else {
                position++;
            }

        }
        res.append(text.substring(begin));
        return res.toString();
    }


    /**
     * 判断是否为符号
     * @param c
     * @return
     */
    private boolean isSymbol(Character c){
        //0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    /**
     *前缀树结构
     */
    private class TireNode{
        //结尾标记，判断是不是到达了前缀树的叶子节点，若是代表这是一个敏感词
        private boolean isKeyWord = false;
        //存放节点和子节点，为什么用map？
        private Map<Character,TireNode> childNodes = new HashMap<>();

        //判断是否是敏感词
        private boolean isKeyWord(){
            return isKeyWord;
        }

        private void setKeyWord(boolean keyWord){
            isKeyWord = keyWord;
        }
        //为某个节点添加子节点，构造前缀树的时候使用
        private void addChildNode(Character key,TireNode node){
            childNodes.put(key,node);
        }

        private TireNode getChildNode(Character c){
            return childNodes.get(c);
        }

    }

}
