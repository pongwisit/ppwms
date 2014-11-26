package com.ese.beans;

import com.ese.model.db.StaffModel;
import com.ese.security.SimpleAuthenticationManager;
import com.ese.service.LoginService;
import com.ese.utils.AttributeName;
import com.ese.utils.FacesUtil;
import com.ese.utils.MessageDialog;
import com.ese.utils.Utils;
import com.ese.security.UserDetail;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Getter
@Setter
@ViewScoped
@ManagedBean(name = "loginBean")
public class LoginBean extends Bean{
    @ManagedProperty("#{loginService}") private LoginService loginService;

    private String userName = "";
    private String password = "";
    private UserDetail userDetail;

    @PostConstruct
    private void init(){
        if(!Utils.isNull(SecurityContextHolder.getContext().getAuthentication())){
            userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }

    public String login(){
        log.info("-- SessionRegistry principle size: {}", sessionRegistry.getAllPrincipals().size());
        if(!Utils.isZero(userName.length()) && !Utils.isZero(password.length())) {
            if(loginService.isUserExist(getUserName(), getPassword())){
                StaffModel staffModel = loginService.getStaffModel();
                userDetail = new UserDetail(userName,password, staffModel.getRole());
                userDetail.setFirstName("Narongchai");
                userDetail.setLastName("Plaiyim");
                HttpServletRequest httpServletRequest = FacesUtil.getRequest();
                HttpServletResponse httpServletResponse = FacesUtil.getResponse();
                UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(userDetail, this.password);
                request.setDetails(new WebAuthenticationDetails(httpServletRequest));
                SimpleAuthenticationManager simpleAuthenticationManager = new SimpleAuthenticationManager();
                Authentication result = simpleAuthenticationManager.authenticate(request);
                log.debug("-- authentication result: {}", result.toString());
                SecurityContextHolder.getContext().setAuthentication(result);
                compositeSessionAuthenticationStrategy.onAuthentication(request, httpServletRequest, httpServletResponse);
                HttpSession httpSession = FacesUtil.getSession(false);
                httpSession.setAttribute(AttributeName.USER_DETAIL.getName(), userDetail);
                log.debug("-- userDetail[{}]", userDetail.toString());
                return userDetail.getRole();
            }
        }
        showDialog(MessageDialog.WARNING.getMessageHeader(), "Username or Password is incorrect.");
        return "loggedOut";
    }

    public String logout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        SecurityContextHolder.clearContext();
        return "loggedOut";
    }

    public void test(){
        System.out.println("test");
//        loginService.test();
    }


}
