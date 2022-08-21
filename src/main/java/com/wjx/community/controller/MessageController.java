package com.wjx.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.wjx.community.aop.annotation.LoginRequired;
import com.wjx.community.common.Page;
import com.wjx.community.common.UserLocal;
import com.wjx.community.entity.Message;
import com.wjx.community.entity.User;
import com.wjx.community.service.MessageService;
import com.wjx.community.service.UserService;
import com.wjx.community.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

import static com.wjx.community.common.CommunityConstant.*;

/**
 * @author wjx
 * @description
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;


    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    @LoginRequired
    public String getLetterList(Model model, Page page) {
        User user = UserLocal.get();
        page.setLimit(5);
        page.setPath("/letter/list");
        //获取当前用户的所有会话数
        page.setRows(messageService.findConversationCount(user.getId()));
        //获取当前用户的所有会话
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                System.out.println(message.getCreateTime());
                //获取当前用户未读取的会话数目
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //获取私信数目
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                int targetId = user.getId().equals(message.getFromId()) ? message.getToId() : message.getFromId();
                //获取发私信的人
                map.put("target", userService.getOneUser(String.valueOf(targetId)));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //获取当前用户未读的私信数目
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //获取当前用户未读的通知数目
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    @LoginRequired
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //获取该会话的所有私信
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.getOneUser(String.valueOf(message.getFromId())));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取该会话的发信人
     * @param conversationId
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (UserLocal.get().getId() == id0) {
            return userService.getOneUser(String.valueOf(id1));
        }
        return userService.getOneUser(String.valueOf(id0));
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (UserLocal.get().getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        if (UserLocal.get() == null) {
            return ResponseUtils.getJSONString(-1, "未登录", null);
        }
        User target = userService.getUserByName(toName);
        if (target == null) {
            return ResponseUtils.getJSONString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(UserLocal.get().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return ResponseUtils.getJSONString(0);
    }

    @PutMapping("/letter/remove/{letterId}")
    @ResponseBody
    public String removeLetterById(@PathVariable("letterId") int letterId) {
        if (UserLocal.get() == null) {
            return ResponseUtils.getJSONString(-1, "未登录", null);
        }
        messageService.removeLetterById(letterId);
        return ResponseUtils.getJSONString(0);
    }

    @GetMapping("/notice/list")
    @LoginRequired
    public String getNoticeList(Model model) {
        User user = UserLocal.get();
        //获取最近评论的通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.getOneUser((String) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            //获取评论相关通知数目
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            //获取评论未读相关通知数目
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", unread);
            model.addAttribute("commentNotice", messageVO);
        }
        //点赞
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.getOneUser((String) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unread);
            model.addAttribute("likeNotice", messageVO);
        }
        //关注
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            Map<String, Object> messageVO = new HashMap<>();
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.getOneUser((String) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unread);
            model.addAttribute("followNotice", messageVO);
        }

        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    @LoginRequired
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = UserLocal.get();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, Map.class);
                map.put("user", userService.getOneUser((String) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("fromUser", userService.getOneUser(String.valueOf(notice.getFromId())));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }


}
