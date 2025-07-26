package com.iecube.iecubetutorial.model.materials.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.mOutline.clientService.W6ClientService;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import com.iecube.iecubetutorial.model.mOutline.wsHandler.WsHandler;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialChatMapper;
import com.iecube.iecubetutorial.model.materials.mapper.MaterialMapper;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;
import com.iecube.iecubetutorial.model.materials.service.OneClickService;
import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.service.ProjectService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.points.exception.PointsNotEnoughException;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Service
public class OneClickServiceImpl implements OneClickService {

    @Autowired
    private MOutlineService mOutlineService;

    @Autowired
    private WsManager wsManager;

    @Autowired
    private W6ClientService w6ClientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private W6ApiService w6ApiService;

    @Autowired
    private MaterialChatMapper materialChatMapper;

    @Autowired
    private ProjectService projectService;

    @Override
    public void oneClickGenMaterial(MaterialQo materialQo) {
        //todo 先生成 materialEntity  再生成 Project 再生成 大纲 再 进行文件生成
        Long accountId = ThreadLocalUtil.getAccountId();
        Account account = accountService.getAccount(accountId);
        MaterialEntity material = new MaterialEntity(); // material
        material.setUserId(accountId);
        material.setName(materialQo.getName());
        material.setTitle(materialQo.getTitle());
        material.setKnowledgePoint(materialQo.getKnowledgePoints());
        material.setInstruction(null);
        material.setStatus(MaterialStatus.NOTReady.getStatus());
        material.setDeleted(0);
        material.setCreateTime(new Date());
        int res = materialMapper.addMaterial(material);
        if(res!=1){
            throw new InsertException("服务错误，新增数据异常");
        }
        String chatId = w6ApiService.genChat();
        MaterialChat materialChat = new MaterialChat();  // materialChat
        materialChat.setChatId(chatId);
        materialChat.setMaterialId(material.getId());
        int res2 = materialChatMapper.addMaterialChat(materialChat);
        if(res2!=1){
            throw new InsertException("服务错误，新增数据异常");
        }
        // 创建工程
        Project project = projectService.createProjectByMaterial(material);
        // 数据准备工作完毕
        //和 生产消费者模型 AI建立websocket连接，处理生成任务  连接之后 material.setStatus(MaterialStatus.GENERATING.getStatus()); 更新状态
        if(!pointsService.pointsEnough(account)){
            throw new PointsNotEnoughException("余额不足");
        }
        // todo 生成大纲，由outlineGenHandler 处理启动生成文件
        MOutline mOutline = mOutlineService.genMOutline(materialQo,true, project.getId());
        // 连接webSocket去处理outline
        wsManager.OneClickGen().put(mOutline.getChatId(), materialChat);
        WebSocketSession w6Client = w6ClientService.connect(mOutline.getChatId()); // 发起和w6的socket连接，并发送消息
        WsHandler.SendTow6 sendTow6 = new WsHandler.SendTow6();
        sendTow6.setType("send-message");
        sendTow6.setImages(new ArrayList<>());
        sendTow6.setMessage("""
                        - 你是一个大学老师，要设计一套提示词，交给大语言模型，用于自动生成演示网页。
                        - 我会给你一段可供模仿的提示词，请模仿参考提示词，设计一段提示词，满足我的需求。
                        - 课程要介绍清楚知识点的背景、原理、典型应用场景等；
                        - 必要的地方插入用JS生成的示意图；
                        - 最后提供1个能够体现这个知识点的经典演示程序；
                        - 演示程序的构思要用简洁的演示来体现知识点，不要搞得太复杂。用3-5句话描述清楚对演示程序的需求。
                        - 请记住，演示程序或者示意图都是利用JS或者html5技术生成的。
                        - 设计的实验应该能够通过JS演示程序实现，如果不能就不要提出这样的要求。
                        - 不需要输出技术要求，只需输出教学内容要求。也不需要通过备注输出任何与JS或者html5相关的信息。
                        ## 题目
                        - 课程名称：%s
                        - 知识点名称： %s
                        ## 参考提示词如下
                        ==================
                        ## 需求：通信原理课程QPSK知识点演示网页
                        #### 1. 课程背景与动机介绍（教学区）
                        - 页面最前面部分，要详细说明QPSK在现代数字通信系统中的地位和意义，包括：
                            - 通信原理作为工程类专业的基础课程主要研究内容，调制方式在数字通信中的作用。
                            - QPSK的提出背景：为提高在有限带宽上的数据传输速率，二维正交载波引入所带来的增益。
                            - 与BPSK、QAM等常见数字信号调制方式的比较和发展历程。
                            - QPSK在实际系统（如卫星通信、移动通信、Wi-Fi、LTE等）中的典型应用场景和技术优势（抗噪声、频谱效率等）。
                            - 简要论述选择QPSK进行课堂讲授的意义。
                        #### 2. QPSK原理与知识点详细讲解
                        - 正交相移键控QPSK的基本定义和数学模型。
                        - **插入示意图**：QPSK调制原理框图，显示串并转换、I/Q两路正交调制、信号合成
                        - QPSK调制思想：2比特一组，4种相位（0°、90°、180°、270°）映射原理。
                        - **插入示意图**：QPSK调制思想
                        - 星座图形象解读：I/Q二维空间符号分布，如何实现正交性。
                        - **插入示意图**：星座图形象解读
                        - QPSK与其它调制方式（BPSK、16QAM等）的对比优缺点。
                        - （可包含公式、简明推导，便于学生理解）
                        - 详细举例说明一个比特流（如“11001100”）分组到QPSK符号的映射过程。
                        - QPSK符号的解调方法（判决准则、受噪声影响的判断误差）。
                        #### 3. 可交互参数及仿真控制区
                        - 输入区：支持手动输入或自动生成二进制比特流（建议范围8~16位），可一键随机生成。
                        - 设置区：信噪比SNR（支持滑块或数值输入）、模拟添加高斯白噪声，以及“启动仿真”按钮。
                        #### 4. 可视化演示区
                        - 星座图：显示调制后各符号在I/Q空间的分布，噪声影响下的点云变化。
                        - 比特流与调制/解调流程动画：高亮符号变换、解调比特对比（如出错点颜色标注）。
                        - 可选：I路、Q路调制波形及叠加噪声的时域变化图。
                        #### 要求
                        - 交互区、知识区、绘图区版面清晰，操作流畅，方便课堂演示和学生自学。
                        - 各部分（背景、原理、演示、交互）都有清晰注释，页面显著位置有操作指南和变量意义解释。
                        """.formatted(mOutline.getTitle(),mOutline.getKnowledgePoint()));
        try {
            w6Client.sendMessage(new TextMessage(objectMapper.writeValueAsString(sendTow6)));
        } catch (Exception e) {
            if(w6Client.isOpen()) {
                try {
                    w6Client.close();
                } catch (IOException ex) {
                    throw new ServiceException(ex);
                }
            }
            throw new ServiceException(e);
        }
    }
}
