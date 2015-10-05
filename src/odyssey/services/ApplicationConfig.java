///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package odyssey.services;
//
//import java.util.Set;
//import javax.ws.rs.core.Application;
//
///**
// *
// * @author Shagy
// */
//@javax.ws.rs.ApplicationPath("api/v1")
//public class ApplicationConfig extends Application {
//
//    /**
//     *
//     * @return
//     */
//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> resources = new java.util.HashSet<>();
//        addRestResourceClasses(resources);
//        return resources;
//    }
//
//    /**
//     * Do not modify addRestResourceClasses() method.
//     * It is automatically populated with
//     * all resources defined in the project.
//     * If required, comment out calling this method in getClasses().
//     */
//    private void addRestResourceClasses(Set<Class<?>> resources) {
//        resources.add(Odyssey.services.LibrariesResource.class);
//        resources.add(Odyssey.services.LibrariesResource.SongsResource.class);
//        resources.add(Odyssey.services.LoginResource.class);
//        resources.add(Odyssey.services.LogoutResource.class);
//        resources.add(Odyssey.services.UsersResource.class);
//    }
//    
//}
