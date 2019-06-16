package com.duodian.youhui.admin.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Desc:
 * @Author HealerJean
 * @Date 2018/7/4  下午2:36.
 */
public class IpUtil {

    public static String getIp(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(request==null)return null;
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if(ip.toLowerCase().contains("x-forwarded-for")){
            String ip_temp = "";
            if(ip.contains(":") && ip.split(":").length>0 && !ip.endsWith(":")){
                ip_temp = ip.split(":")[1];
                if(StringUtils.isBlank(ip_temp)) ip_temp = ip.substring(0,ip.lastIndexOf("X-Forwarded-For"));
            }else{
                ip_temp = ip.substring(0,ip.lastIndexOf("X-Forwarded-For"));
            }
            ip = ip_temp.replaceAll(" ","");
        }
        if (ip.indexOf(",") > -1) {
            String ip_temp = ip.split(",")[0];
            ip_temp = ip_temp.replaceAll(" ", "");
            if(ip_temp.startsWith("10.") && ip.split(",").length>1){
                ip = ip.split(",")[1];
                ip = ip.replaceAll(" ", "");
            }else ip = ip_temp;
        }
        return ip;
    }


       /**
     * 获取服务器ip
     * @return
     */
    public static  String getHostIp(){
        try {
            InetAddress ia2 = InetAddress.getLocalHost();
            return  ia2.getHostAddress();
        } catch (UnknownHostException e) {
            ExceptionLogUtils.log(e,IpUtil.class);
            return  null ;
        }
    }

    /**
     * 根据域名获取ip
     * www.baidu.com
     * @param url
     * @return
     */
    public  static String getIpByUrl(String url) {
        try {
            InetAddress ia2 = InetAddress.getByName(url);
            return ia2.getHostAddress();
        } catch (UnknownHostException e) {
            ExceptionLogUtils.log(e,IpUtil.class );
            return  null;
        }
    }



     /**
     * 获取调用的域名
     * @param urlTarget
     * @return
     */
    public static String getDomainAndPort(String urlTarget)
    {
        //跳转到对应的回调地址
        String domain = "";
        try {
            URL url = new URL(urlTarget);
            String host = url.getHost();
            int port = url.getPort();
            String s = url.toString();
            domain = s.substring(0,s.indexOf(host)+host.length());
            if(port != -1) {
                domain = domain + ":" + port;
            }
        } catch (MalformedURLException e) {
            log.info("获取域名失败");
        }
        return domain ;
    }

    public static String genUrl(String hostPort, List<String> uriSegment , Map<String,String> params) {
        HttpUrl httpUrl = HttpUrl.parse(hostPort);
        HttpUrl.Builder builder = httpUrl.newBuilder();
        if(uriSegment != null && !uriSegment.isEmpty()){
            for (String segment: uriSegment) {
                builder.addPathSegment(segment);
            }
        }
        if(params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        httpUrl = builder.build();
        return httpUrl.toString();
    }

    public static StringBuffer getRealRequestURL(HttpServletRequest request){

        StringBuffer requestURL = request.getRequestURL();
        return requestURL;
    }
}
