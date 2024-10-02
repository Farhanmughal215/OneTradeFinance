package com.xstocks.uc.config.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.pojo.constants.CommonConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

/**
 * 认证过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;

    private final UserDetailsService userDetailsService;

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = jwtManager.parse(request.getHeader("Authorization"));
        } catch (Throwable e) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.getOutputStream()
                    .write(JsonUtil.toJSONString(BaseResp.error(ErrorCode.UNAUTHENTICATED, "illegal jwt token"))
                            .getBytes(
                                    StandardCharsets.UTF_8));
            return;
        }
        if (Objects.nonNull(decodedJWT)) {
            String username = decodedJWT.getSubject();
            UserDetails user = userDetailsService.loadUserByUsername(username);

            UserDetail userDetail = (UserDetail) user;
            UserPO userPO = userDetail.getUserPOEntity();

            boolean isSystem = user.getAuthorities().stream().anyMatch(
                    grantedAuthority -> RoleTypeEnum.ROLE_SYSTEM.getCode().equals(grantedAuthority.getAuthority()));
            boolean isExpired = decodedJWT.getExpiresAt().before(new Date());
            if (isSystem) {
                // 系统账户不过期
                isExpired = false;
            }
            if (isExpired) {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json");
                String newToken = userService.generateTokenByUserPO(userPO);
                response.getOutputStream()
                        .write(JsonUtil.toJSONString(BaseResp.error(ErrorCode.UNAUTHENTICATED, CommonConstant.NEW_TOKEN, newToken)).getBytes(
                                StandardCharsets.UTF_8));
                return;
            }
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute(CommonConstant.CURRENT_LOGIN_USER, userPO);
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.getOutputStream().write(JsonUtil.toJSONString(BaseResp.error(ErrorCode.UNAUTHENTICATED)).getBytes(
                    StandardCharsets.UTF_8));
        }
    }
}
