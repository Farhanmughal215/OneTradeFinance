package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.component.OssComponent;
import com.xstocks.uc.config.ApplicationContextWrapper;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import com.xstocks.uc.pojo.enums.TodoStatusEnum;
import com.xstocks.uc.pojo.enums.TodoTypeEnum;
import com.xstocks.uc.pojo.event.TodoDoneEvent;
import com.xstocks.uc.pojo.param.CreateTodoParam;
import com.xstocks.uc.pojo.param.ProcessTodoParam;
import com.xstocks.uc.pojo.param.TodoQueryParam;
import com.xstocks.uc.pojo.po.TodoPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.UserProfileVO;
import com.xstocks.uc.service.TodoService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ToDoController {

    @Autowired
    private OssComponent ossComponent;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContextWrapper applicationContextWrapper;

    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/c/todo/c", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResp<Boolean> createByUser(CreateTodoParam createTodoParam,
                                          @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                          UserPO currentLoginUser) {

        return createToDo(createTodoParam, currentLoginUser, currentLoginUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping(value = "/b/todo/c", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResp<Boolean> createByAdmin(CreateTodoParam createTodoParam,
                                           @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                           UserPO currentLoginUser) {

        if (Objects.isNull(createTodoParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        if (!userService.isManageable(currentLoginUser, createTodoParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no auth to manage this user");
        }
        UserPO user = userService.getById(createTodoParam.getUserId());
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        return createToDo(createTodoParam, user, currentLoginUser);
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    @PostMapping(value = "/s/todo/c", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResp<Boolean> createBySystem(CreateTodoParam createTodoParam,
                                            @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                            UserPO currentLoginUser) {

        if (Objects.isNull(createTodoParam.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userId required");
        }
        UserPO user = userService.getById(createTodoParam.getUserId());
        if (Objects.isNull(user)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding user");
        }
        return createToDo(createTodoParam, user, currentLoginUser);
    }

    private BaseResp<Boolean> createToDo(CreateTodoParam createTodoParam, UserPO targetUser, UserPO createUser) {
        if (Objects.isNull(createTodoParam.getTodoType())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "todoType required");
        }

        if (TodoTypeEnum.PROFILE_REVIEW.getCode() == createTodoParam.getTodoType() ||
                TodoTypeEnum.BANKNO_REVIEW.getCode() == createTodoParam.getTodoType() ||
                TodoTypeEnum.DEPOSIT.getCode() == createTodoParam.getTodoType()) {
            if (CollectionUtils.isEmpty(createTodoParam.getAttachments())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "attachments required");
            }
        }

        if (CollectionUtils.isNotEmpty(createTodoParam.getAttachments()) &&
                createTodoParam.getAttachments().size() >= 5) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "too much attachments");
        }

        TodoPO todo = new TodoPO();
        todo.setTodoType(createTodoParam.getTodoType());
        todo.setUserId(targetUser.getId());
        todo.setCreateBy(createUser.getId());
        todo.setUpdateBy(createUser.getId());

        if (CollectionUtils.isNotEmpty(createTodoParam.getAttachments())) {
            List<String> attachmentUrlList = createTodoParam.getAttachments()
                    .stream()
                    .map(file -> {
                        log.info("createToDo originalFilename:{},lastDot index:{}", file.getOriginalFilename(),
                                file.getOriginalFilename().lastIndexOf("."));
                        String picFormat =
                                file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

                        Set<String> allowedPicFormatSet = new HashSet<>(
                                Arrays.asList(".xbm", ".tif", "pjp", ".svgz", ".jpg", ".jpeg", ".ico", ".tiff", ".gif",
                                        ".svg", ".jfif", ".webp", ".png", ".bmp", ".pjpeg", ".avif"));

                        if (!allowedPicFormatSet.contains(picFormat)) {
                            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "Unsupported File Format");
                        }

                        String yyyyMMddHHmmssSSS =
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
                        String objectName = targetUser.getBizId() + "/" + yyyyMMddHHmmssSSS + picFormat;
                        String uploadResult;
                        try {
                            uploadResult = ossComponent.uploadInputStream(objectName, file.getInputStream());
                            if (StringUtils.isEmpty(uploadResult)) {
                                throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
                            }
                        } catch (IOException e) {
                            throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
                        }
                        return uploadResult;
                    }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(attachmentUrlList)) {
                todo.setAttachments(JsonUtil.toJSONString(attachmentUrlList));
            }
        }

        if (StringUtils.isNotBlank(createTodoParam.getDescription())) {
            if (TodoTypeEnum.PROFILE_REVIEW.getCode() == createTodoParam.getTodoType()) {
                UserProfileVO userProfileVO =
                        JsonUtil.parseObject(createTodoParam.getDescription(), UserProfileVO.class);
                if (Objects.isNull(userProfileVO)) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userName address required");
                }
                if (StringUtils.isEmpty(userProfileVO.getUserName()) ||
                        StringUtils.isEmpty(userProfileVO.getAddress())) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userName address required");
                }
            } else if (TodoTypeEnum.BANKNO_REVIEW.getCode() == createTodoParam.getTodoType()) {
                UserProfileVO userProfileVO =
                        JsonUtil.parseObject(createTodoParam.getDescription(), UserProfileVO.class);
                if (Objects.isNull(userProfileVO)) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "userName address required");
                }
                if (StringUtils.isEmpty(userProfileVO.getBankNo())) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "bankNo required");
                }
            } else {
                if (!NumberUtils.isParsable(createTodoParam.getDescription())) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "deposit or withdraw amount illegal");
                }
            }
            todo.setDescription(createTodoParam.getDescription());
        }
        todo.setTodoStatus(TodoStatusEnum.PENDING.getCode());
        RoleTypeEnum createUserRoleTypeEnum = userService.getRole(createUser);
        if (RoleTypeEnum.ROLE_USER != createUserRoleTypeEnum) {
            todo.setTodoStatus(TodoStatusEnum.ACCEPT.getCode());
        }
        todoService.save(todo);
        if (RoleTypeEnum.ROLE_USER != createUserRoleTypeEnum) {
            applicationContextWrapper.publish(new TodoDoneEvent(this, todo));
        }
        return BaseResp.success(Boolean.TRUE);
    }

    @RateLimit
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/c/todo/l")
    public BaseResp<IPage<TodoPO>> listC(@RequestBody TodoQueryParam todoQueryParam,
                                         @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER) UserPO currentLoginUser) {
        Page<TodoPO> page = new Page<>(todoQueryParam.getPageNo(), todoQueryParam.getPageSize());
        page.addOrder(OrderItem.desc("create_time"));
        LambdaQueryWrapper<TodoPO> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(TodoPO::getUserId, currentLoginUser.getId());
        if (Objects.nonNull(todoQueryParam.getTodoType())) {
            lambdaQueryWrapper.eq(TodoPO::getTodoType, todoQueryParam.getTodoType());
        }
        if (Objects.nonNull(todoQueryParam.getTodoStatus())) {
            lambdaQueryWrapper.eq(TodoPO::getTodoStatus, todoQueryParam.getTodoStatus());
        }
        IPage<TodoPO> pageResult = todoService.page(page, lambdaQueryWrapper);
        return BaseResp.success(pageResult);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping("/b/todo/l")
    public BaseResp<IPage<TodoPO>> listb(@RequestBody TodoQueryParam todoQueryParam,
                                         @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                         UserPO currentLoginUser) {
        return BaseResp.success(todoService.queryPageToDo(todoQueryParam, currentLoginUser));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_ROOT')")
    @PostMapping("/b/todo/u")
    public BaseResp<Boolean> process(@RequestBody ProcessTodoParam processTodoParam,
                                     @RequestAttribute(CommonConstant.CURRENT_LOGIN_USER)
                                     UserPO currentLoginUser) {
        if (Objects.isNull(processTodoParam.getTodoId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "todoId required");
        }
        if (Objects.isNull(processTodoParam.getAcceptOrDeny())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "confirm accept or deny");
        }
        if (processTodoParam.getAcceptOrDeny() != TodoStatusEnum.ACCEPT.getCode() &&
                processTodoParam.getAcceptOrDeny() != TodoStatusEnum.DENY.getCode()) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "param acceptOrDeny invalidate(1-accept,2-deny)");
        }
        if (TodoStatusEnum.DENY.getCode() == processTodoParam.getAcceptOrDeny()) {
            if (StringUtils.isBlank(processTodoParam.getFeedBack())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "when deny, feedback needed");
            }
        }
        TodoPO todo = todoService.getOne(
                Wrappers.<TodoPO>lambdaQuery().eq(TodoPO::getTodoStatus, TodoStatusEnum.PENDING.getCode())
                        .eq(TodoPO::getId, processTodoParam.getTodoId()).last("limit 1"));
        if (Objects.isNull(todo)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding todo or done by other admin");
        }
        if (!userService.isManageable(currentLoginUser, todo.getUserId())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no auth to manage this user");
        }
        LambdaUpdateWrapper<TodoPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(TodoPO::getId, processTodoParam.getTodoId())
                .eq(TodoPO::getTodoStatus, TodoStatusEnum.PENDING.getCode())
                .set(TodoPO::getTodoStatus, TodoStatusEnum.getByCode(processTodoParam.getAcceptOrDeny()).getCode())
                .set(TodoPO::getUpdateBy, currentLoginUser.getId())
                .set(TodoPO::getFeedBack,
                        StringUtils.isNotBlank(processTodoParam.getFeedBack()) ? processTodoParam.getFeedBack() :
                                StringUtils.EMPTY);

        if (todoService.update(null, lambdaUpdateWrapper)) {
            TodoPO todoPO = todoService.getById(processTodoParam.getTodoId());
            applicationContextWrapper.publish(new TodoDoneEvent(this, todoPO));
        }
        return BaseResp.success(Boolean.TRUE);
    }
}
