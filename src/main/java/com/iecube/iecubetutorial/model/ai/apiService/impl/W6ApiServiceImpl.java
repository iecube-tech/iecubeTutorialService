package com.iecube.iecubetutorial.model.ai.apiService.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import com.iecube.iecubetutorial.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class W6ApiServiceImpl implements W6ApiService {

    @Value("${Ai.baseUrl}")
    private String baseUrl;

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Value("${Ai.model.procedure}")
    private String modelProcedure;

    @Value("${Ai.model.llm}")
    private String modelLlm;

    @Value("${Ai.model.llm_short}")
    private String modelLlmShort;

    @Value("${Ai.module.name}")
    private String moduleName;

    private static final String INSTRUCTION= """
            您是一名具有高等教育经验的教学设计师和前端开发专家，对现代教学演示设计有深入理解，尤其擅长创建符合高校教学规范的交互式讲义。您的设计需兼顾知识体系的严谨性和教学呈现的直观性。
            请根据提供的内容，设计一个符合中国高等院校教育教学风格和表达习惯的"中文" 可视化网页作品。\s
            
            ## 内容要求
            - 采用学术性中文表述，符合学科专业术语规范
            - 保持原文件的核心信息，但以更易读、可视化的方式呈现
            - 涉及理论知识的内容，要概述性的讲解
            - 必须包含 关键知识点的动画或交互仿真展示
            - 必要时要添加如下内容，并做到非常详细的展开
              - 核心知识点标题（可采用疑问句式）
              - 关键理论/公式（突出显示，并详细解释关键参数定义与理论基础）
              - 教学案例/示意图（如果需要）
              - 思考题/延伸问题（底部固定区域，如果需要）
            - 在页面底部添加作者信息区域，包含：
              - 版权信息: IECUBE Tutorial 和2025年
              - 页脚用较小字号和灰色字体声明，"本内容为人工智能生成，观点为转述原作者，不代表本公司立场，仅供参考和批判"
            
            ## 仿真动画要求
              - 使用JavaScript代码生成动画，确保动画流畅且易于理解
              - 动画必须与内容紧密相关，并有助于解释复杂概念
              - 动画应具有教育意义，能够帮助学习者更好地理解概念
              - 动画应遵循社会主义核心价值观，符合中国教育标准
              - 动画支持参数修改，有开始/结束等按钮调整参数实现仿真效果
            
            ## 公式要求
            - 使用KaTeX进行公式渲染，确保公式正确显示
            - 公式必须使用LaTeX语法书写，并确保公式在页面中正确渲染
            - 公式必须使用Markdown语法进行标记，并确保公式在页面中正确渲染
            
            ## 设计风格
            - 整体风格参考Linear App的简约现代设计
            - 使用清晰的视觉层次结构，突出重要内容
            - 配色方案应专业、和谐，适合长时间阅读
            
            ## 数据可视化
            - 必要时用JavaScript代码生成图表来增强表达
            - 数据需要忠实引用自原文，不要使用原文中不包含的数据
            - 使用标准化图表：柱状图、折线图、比例图等，适当位置展示
            - 图表配色应符合整体主题
            - 每个图表包含清晰标题和数据来源
            - 确保图表清晰可读，附有必要的解释文字
            
            ## 交互体验
            - 添加适当的微交互效果提升用户体验：
              - 按钮悬停时有轻微放大和颜色变化
              - 卡片元素悬停时有精致的阴影和边框效果
              - 页面滚动时有平滑过渡效果
              - 内容区块加载时有优雅的淡入动画
            
            ## 图标与视觉元素
            - 使用专业图标库如Font Awesome或Material Icons
            - 根据内容主题选择合适的插图或图表展示数据
            - 避免使用emoji作为主要图标
            
            ## 媒体资源
            - 使用文档中的Markdown图片链接（如果有的话）
            - 使用文档中的嵌入代码（如果有的话）
            
            ## 响应式设计
            - 页面能够自适应在所有设备上（手机、平板、桌面）完美展示
             - 使用相对单位（如em、rem、vh、vw）而非固定像素值
             - 添加媒体查询，针对不同屏幕尺寸优化布局和字体大小
            - 针对不同屏幕尺寸优化布局和字体大小
            - 确保移动端有良好的触控体验
            - 简化复杂组件：对于时间线、多列布局等复杂组件，确保它们能够自适应不同屏幕尺寸，必要时简化设计或提供替代布局。
            
            ## 技术规范
            - 使用HTML5、TailwindCSS 3.0+（通过CDN引入）和必要的JavaScript
            - 专业图标库的展示通过CDN引入必要资源
            - 图表展示时通过CDN引入chart.js， 并保证无错误
            - 动画功能要保证动画可用(如果有动画)
            - 实现完整的深色/浅色模式切换功能，默认跟随系统设置
            - 代码结构清晰，包含适当注释，便于理解和维护
            - 注意，Tailwindcss 3.0+通过CDN引入的正确方式是:`<script src="https://cdn.tailwindcss.com"></script>`
            - 界面中引入的CDN链接必须保证中国大陆地区可访问性，如不可访问则使用国内镜像源
            
            ## 特别注意事项
            - 测试指令：请在设计过程中模拟测试不同屏幕尺寸（特别是高度较小的屏幕），确保所有内容都能完整且优雅显示。
            
            ## 性能优化
            - 确保页面加载速度快，避免不必要的大型资源
            - 图片使用现代格式(WebP)并进行适当压缩
            - 实现懒加载技术用于长页面内容
            
            
            ## 输出要求
            - 提供完整可运行的单一HTML文件，包含所有必要的CSS和JavaScript
            - 确保代码符合W3C标准，无错误警告
            - 页面在不同浏览器中保持一致的外观和功能
            
            请你像一个真正的网页设计专家一样思考，充分发挥你的专业技能和创造力，打造一个令人惊艳的HTML可视化网页作品！
            """;

    @Override
    public String genChat() {
        String uri;
        if(modelProcedure.equals("default")){
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .toUriString();
        }else {
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .queryParam("llm", modelLlm)
                    .queryParam("llm_short", modelLlmShort)
                    .toUriString();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                return checkResult.getBodyData().get("chat_id").asText();
            }else {
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }

    }

    @Override
    public void usePageMaker(String chatId, String title, String knowledgePoints, String outline) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/agent").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("title", title);
        payloadMap.put("knowledge_points", knowledgePoints);
        payloadMap.put("pagemaking_instruction", INSTRUCTION);
        payloadMap.put("is_need_optimize","yes");
        payloadMap.put("optimize_instruction",outline);
        requestBodyMap.put("payload", payloadMap);
        requestBodyMap.put("agent_name", "pagemaker");
        requestBodyMap.put("chat_id", chatId);
        requestBodyMap.put("llm_model_override", modelLlm);
        requestBodyMap.put("module_name", moduleName);
        requestBodyMap.put("module_source", null);
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("动态讲义(pagemaker):{},{},{}",chatId, title, knowledgePoints);
            }else {
                log.warn("动态讲义(pagemaker):{};{};{}",chatId, title, knowledgePoints);
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("动态讲义(pagemaker):{};{};{}",chatId, title, knowledgePoints);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
       }
    }

    @Override
    public JsonNode getJsonRes(String artefactId) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/artefact/"+artefactId)
                .queryParam("include_content", true)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("获取ai message json格式 {}", artefactId);
                return checkResult.getBodyData();
            }else {
                log.warn("获取ai message json格式 WARNING {}", artefactId);
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("获取ai message json格式 ERROR {}", artefactId);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }
}
