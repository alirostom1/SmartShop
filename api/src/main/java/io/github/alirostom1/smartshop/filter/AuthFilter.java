package io.github.alirostom1.smartshop.filter;

import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class AuthFilter implements Filter{
    private final ObjectMapper objectMapper;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if(isPublicEndpoint(path)){
            chain.doFilter(request,response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if(session == null || session.getAttribute("user") == null){
            sendApiErrorResponse(httpResponse,401,path,"You must authenticated to access this endpoint!");
            return;
        }

        chain.doFilter(request,response);
    }


    private boolean isPublicEndpoint(String path){
        if(path.startsWith("/api/v1/auth/login")){
            return true;
        }
        // ADD OTHER ENDPOINTS IF AVAILABLE
        return false;
    }

    private void sendApiErrorResponse(HttpServletResponse response, int status,String path, String message) throws IOException{
        response.setStatus(status);
        response.setContentType("application/json");
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .path(path)
                .status(status)
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }



}
