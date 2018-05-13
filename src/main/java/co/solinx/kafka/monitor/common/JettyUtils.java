//package co.solinx.kafka.monitor.common;
//
//import org.eclipse.jetty.servlet.DefaultServlet;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
//import org.eclipse.jetty.websocket.WebSocketServlet;
//
//import javax.servlet.Servlet;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URL;
//
///**
// * Created by xin on 2017-01-07.
// *
// * @author linx
// */
//public class JettyUtils {
//
//
//    public static ServletContextHandler createServletHandler(Servlet servlet) {
//        return createServletHandler(servlet, servlet.getClass().getAnnotation(WebServlet.class).urlPatterns()[0]);
//    }
//
//    /**
//     * ServletHandler
//     *
//     * @param path
//     * @return
//     */
//    public static ServletContextHandler createServletHandler(Servlet servlet, String path) {
//
//        ServletContextHandler handler = new ServletContextHandler();
////        ServletHolder holder = new ServletHolder(servlet);
////        handler.addServlet(holder, path);
////        handler.setContextPath("/data");
//
//        return handler;
//    }
//
//
//    /**
//     * WebSocket
//     *
//     * @param servlet
//     * @param path
//     * @return
//     */
//    public static ServletContextHandler createWebSockethandler(WebSocketServlet servlet, String path) {
//        ServletContextHandler handler = new ServletContextHandler();
////        ServletHolder holder = new ServletHolder(servlet);
////        handler.addServlet(holder, "/");
////        handler.setContextPath(path);
//        return handler;
//    }
//
//    /**
//     * 静态资源
//     *
//     * @param resourceBase
//     * @param path
//     * @return
//     */
//    public static ServletContextHandler createStaticHandler(String resourceBase, String path) {
//        ServletContextHandler handler = new ServletContextHandler();
////        handler.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false");
////        DefaultServlet staticServlet = new DefaultServlet();
////        ServletHolder holder = new ServletHolder(staticServlet);
////        URL url = JettyUtils.class.getClassLoader().getResource(resourceBase);
////        holder.setInitParameter("resourceBase", url.toString());
////        handler.setContextPath("/");
////        handler.addServlet(holder, path);
//        return handler;
//    }
//
//
//    /**
//     * Servlet
//     *
//     * @param context
//     * @return
//     */
//    public static HttpServlet createServlet(String context) {
//
//
//        HttpServlet servlet = new HttpServlet() {
//            @Override
//            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//                    throws ServletException, IOException {
//
//
//                resp.setContentType("text/html;charset=utf-8");
//                resp.setStatus(HttpServletResponse.SC_OK);
//                resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//                resp.getWriter().println(context);
//            }
//
//            @Override
//            protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//            }
//        };
//        return servlet;
//    }
//}
