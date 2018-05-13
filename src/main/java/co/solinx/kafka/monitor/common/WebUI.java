//package co.solinx.kafka.monitor.common;
//
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.handler.ContextHandlerCollection;
//import org.eclipse.jetty.servlet.DefaultServlet;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
//import org.eclipse.jetty.websocket.WebSocketHandler;
//import org.eclipse.jetty.websocket.WebSocketServlet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.Servlet;
//import java.net.InetSocketAddress;
//import java.net.URL;
//
///**
// * Created by xin on 2017-01-07.
// */
//public class WebUI {
//
//    private int port;
//    private String name;
//    private String basePath;
//    private Server server;
//    private String host;
//    private ContextHandlerCollection collection = new ContextHandlerCollection();
//    private static Logger logger = LoggerFactory.getLogger(WebUI.class);
//
//    public WebUI(String host, int port, String name, String basePath) {
//        this.port = port;
//        this.name = name;
//        this.basePath = basePath;
//        this.host = host;
//    }
//
//
//    public void attachPage(Servlet name, String path) {
////        attachHandler(JettyUtils.createServletHandler(name, path));
//    }
//
//    /**
//     * 添加页面
//     */
//    public void attachPage(Servlet servlet) {
//
//
////        attachHandler(JettyUtils.createServletHandler(servlet));
//
//    }
//
//    /**
//     * 添加WebSocket服务
//     *
//     * @param webSocketServlet WebSocket服务
//     * @param path
//     */
//    public void attachWebSocket(WebSocketServlet webSocketServlet, String path) {
////        attachHandler(JettyUtils.createWebSockethandler(webSocketServlet, path));
//    }
//
//
//    public void attachHandler(ServletContextHandler handler) {
////        collection.addHandler(handler);
//
//    }
//
//    public void attachHandler(WebSocketHandler handler) {
//        collection.addHandler(handler);
//    }
//
//    public ServletContextHandler staticRsource() {
//        ServletContextHandler handler = new ServletContextHandler();
//        handler.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false");
//        DefaultServlet staticServlet = new DefaultServlet();
//        ServletHolder holder = new ServletHolder(staticServlet);
//        URL url = JettyUtils.class.getClassLoader().getResource("static");
//        holder.setInitParameter("resourceBase", url.toString());
//        handler.setContextPath("/");
//        handler.addServlet(holder, "/");
//        return handler;
//    }
//
//    public ServletContextHandler jerseyHandler() {
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//        ServletHolder servlet = context.addServlet(
//                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//        servlet.setInitOrder(1);
//        servlet.setInitParameter("jersey.config.server.provider.packages", "co.solinx.kafka.monitor.api");
//        context.setContextPath("/data");
//        context.addServlet(servlet, "/");
//        return context;
//    }
//
//
//    public void bind() throws Exception {
//
//        server = new Server(new InetSocketAddress(host, port));
//
//        try {
//
//            ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
//            handlerCollection.addHandler(staticRsource());
//            handlerCollection.addHandler(jerseyHandler());
////            server.setHandler(handlerCollection);
////            server.start();
//            logger.debug("name : {} basePath : {}", name, basePath);
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
//
//    public static void main(String[] args) {
//        WebUI webUI = new WebUI("0.0.0.0", 5050, "WebUi", "/");
//
//
//        try {
//            //webUI.attachHandler(JettyUtils.createStaticHandler(Constant.STATIC_RESOURCE_DIR,"/static"));
//            webUI.attachHandler(JettyUtils.createStaticHandler("static", "/"));
////            webUI.attachPage(new BrokerServlet());
//            webUI.bind();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
