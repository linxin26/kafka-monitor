//package co.solinx;
//
//import org.apache.catalina.WebResourceRoot;
//import org.apache.catalina.WebResourceSet;
//import org.apache.catalina.core.StandardContext;
//import org.apache.catalina.startup.Tomcat;
//import org.apache.catalina.webresources.DirResourceSet;
//import org.apache.catalina.webresources.EmptyResourceSet;
//import org.apache.catalina.webresources.StandardRoot;
//import org.apache.tomcat.util.scan.Constants;
//import org.apache.tomcat.util.scan.StandardJarScanFilter;
//
//import java.io.File;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
///**
// * Hello world!
// */
//public class App {
//
//    private static File getRootFolder() {
//        try {
//            File root;
//            String runningJarPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
//            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
//            if (lastIndexOf < 0) {
//                root = new File("");
//            } else {
//                root = new File(runningJarPath.substring(0, lastIndexOf));
//            }
//            System.out.println("application resolved root folder: " + root.getAbsolutePath());
//            return root;
//        } catch (URISyntaxException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//
//            File root = getRootFolder();
//            File webContentFolder = new File(root.getAbsolutePath(), "src/main/webapp/");
//            if (!webContentFolder.exists()) {
//                webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
//            }
//
//            Tomcat tomcat = new Tomcat();
//
//            Path tempPath = Files.createTempDirectory("tomcat-base-dir");
//            tomcat.setBaseDir(tempPath.toString());
//
//            StandardContext context = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
//            context.setParentClassLoader(App.class.getClassLoader());
//
//
//            if (System.getProperty(Constants.SKIP_JARS_PROPERTY) == null && System.getProperty(Constants.SKIP_JARS_PROPERTY) == null) {
//                System.out.println("disabling TLD scanning");
//                StandardJarScanFilter jarScanFilter = (StandardJarScanFilter) context.getJarScanner().getJarScanFilter();
//                jarScanFilter.setTldSkip("*");
//            }
//
//            System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());
//
//            File additionWebInfClassesFolder = new File("target/classes");
//            WebResourceRoot resourceRoot = new StandardRoot(context);
//            WebResourceSet resourceSet;
//            if (additionWebInfClassesFolder.exists()) {
//                resourceSet = new DirResourceSet(resourceRoot, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
//                System.out.println("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
//            } else {
//                resourceSet = new EmptyResourceSet(resourceRoot);
//            }
//            resourceRoot.addPreResources(resourceSet);
//
//            context.setResources(resourceRoot);
//
//            tomcat.setPort(9089);
//
//            start();
//            tomcat.start();
//            tomcat.getServer().await();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void start() {
//          //KafkaBaseInfoService.getInstance();
//    }
//}
